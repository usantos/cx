package br.gov.caixa.loterias.feature.betting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddComboToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingMutationBarrier
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessage
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingCatalogState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
class BettingCatalogViewModel(
    private val mode: BettingCatalogMode,
    private val comboId: Int? = null,
    private val getModalities: GetLotteryModalitiesUseCase,
    private val getParameters: GetBettingParametersUseCase,
    private val getCombos: GetBetCombosUseCase,
    private val addCombo: AddComboToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val recoveryMessages: BettingRecoveryMessages = BettingRecoveryMessages.Identifiers,
    private val checkpointSession: BettingCheckpointSession? = null,
) : ViewModel() {
    private val mutableState = reduxStateStore<BettingCatalogUiState, BettingCatalogAction>(
        BettingCatalogUiState(),
        ::reduceBettingCatalogState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private var additionJob: Job? = null
    private val mutation = BettingMutationBarrier(checkpointSession)

    init {
        load()
    }

    fun load() = viewModelScope.launch(dispatchers.io) {
        mutableState.dispatch(BettingCatalogAction.LoadingStarted(mode))
        when (mode) {
            BettingCatalogMode.MODALITIES -> loadModalities()
            BettingCatalogMode.COMBOS, BettingCatalogMode.COMBO_DETAIL -> loadCombos()
        }
    }

    fun increaseQuantity() = mutableState.dispatch(BettingCatalogAction.QuantityIncreased)

    fun decreaseQuantity() = mutableState.dispatch(BettingCatalogAction.QuantityDecreased)

    fun toggleSurprise() = mutableState.dispatch(BettingCatalogAction.SurpriseToggled)

    fun addSelectedCombo() {
        val task = synchronized(mutableState) {
            val current = state.value
            if (additionJob?.isCompleted == false || current.loading || current.parameters == null) return
            val combo = current.selectedCombo ?: return
            mutableState.dispatch(BettingCatalogAction.CartAdditionStarted)
            viewModelScope.launch(dispatchers.io, start = CoroutineStart.LAZY) {
                executeComboAddition(combo.id, current.comboQuantity, current.surprise)
            }.also { additionJob = it }
        }
        task.start()
    }

    private suspend fun executeComboAddition(comboId: Int, quantity: Int, surprise: Boolean) {
        val result = try {
            mutation.run("ADD_COMBO", { it: AppResult<*> -> it is AppResult.Success }) {
                addCombo(comboId, quantity, surprise)
            }
        } catch (cancelled: CancellationException) {
            if (viewModelScope.isActive && checkpointSession?.isCurrent() != false) {
                mutableState.dispatch(
                    BettingCatalogAction.RecoveryRequired(recoveryMessages.text(BettingRecoveryMessage.RECONCILE_REQUIRED))
                )
            }
            throw cancelled
        } catch (_: Exception) {
            if (checkpointSession?.isCurrent() != false) {
                mutableState.dispatch(
                    BettingCatalogAction.RecoveryRequired(recoveryMessages.text(BettingRecoveryMessage.RECONCILE_REQUIRED))
                )
            }
            return
        }
        if (checkpointSession?.isCurrent() == false) return
        when (result) {
            is AppResult.Success -> mutableState.dispatch(BettingCatalogAction.AddedToCart)
            is AppResult.Failure -> applyFailure(result.error)
        }
    }

    fun consumeCartAddition(): Boolean = synchronized(mutableState) {
        if (!mutableState.value.addedToCart) return@synchronized false
        mutableState.dispatch(BettingCatalogAction.CartAdditionConsumed)
        true
    }

    private suspend fun loadModalities() {
        when (val modalities = getModalities()) {
            is AppResult.Failure -> applyFailure(modalities.error)

            is AppResult.Success -> when (val parameters = getParameters()) {
                is AppResult.Failure -> applyFailure(parameters.error)
                is AppResult.Success -> mutableState.dispatch(BettingCatalogAction.ModalitiesLoaded(modalities.value, parameters.value))
            }
        }
    }

    private suspend fun loadCombos() {
        when (val result = getCombos()) {
            is AppResult.Failure -> applyFailure(result.error)

            is AppResult.Success -> {
                val combos = result.value.filter { it.id > 0 }
                val selected = if (mode == BettingCatalogMode.COMBO_DETAIL) {
                    combos.find { it.id == comboId }
                } else {
                    null
                }
                if (mode == BettingCatalogMode.COMBO_DETAIL && selected == null) {
                    applyFailure(AppError.Validation(messageText("combo_nao_encontrado_0bb727")))
                } else {
                    val parameters = if (mode == BettingCatalogMode.COMBO_DETAIL) {
                        when (val result = getParameters()) {
                            is AppResult.Success -> result.value

                            is AppResult.Failure -> {
                                applyFailure(result.error)
                                return
                            }
                        }
                    } else {
                        null
                    }
                    mutableState.dispatch(BettingCatalogAction.CombosLoaded(combos, selected, parameters))
                }
            }
        }
    }

    private fun applyFailure(error: AppError) = mutableState.dispatch(BettingCatalogAction.Failed(error.userMessage()))

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> messageText("nao_foi_possivel_carregar_as_opc_1953eb")
        is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_atualizar_a_apo_da2fb5") }
        AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_ao_ca_2cdaea")
    }
}
