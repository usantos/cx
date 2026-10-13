package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.AddComboToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.BetCombo
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBetCombosUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BettingCatalogMode { MODALITIES, COMBOS, COMBO_DETAIL }

data class BettingCatalogUiState(
    val loading: Boolean = true,
    val modalities: List<LotteryModality> = emptyList(),
    val parameters: BettingParameters? = null,
    val combos: List<BetCombo> = emptyList(),
    val selectedCombo: BetCombo? = null,
    val comboQuantity: Int = 1,
    val surprise: Boolean = true,
    val addedToCart: Boolean = false,
    val errorMessage: String? = null,
)

class BettingCatalogViewModel(
    private val mode: BettingCatalogMode,
    private val comboId: Int? = null,
    private val getModalities: GetLotteryModalitiesUseCase,
    private val getParameters: GetBettingParametersUseCase,
    private val getCombos: GetBetCombosUseCase,
    private val addCombo: AddComboToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(BettingCatalogUiState())
    val state = mutableState.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (mode) {
            BettingCatalogMode.MODALITIES -> loadModalities()
            BettingCatalogMode.COMBOS, BettingCatalogMode.COMBO_DETAIL -> loadCombos()
        }
    }

    fun increaseQuantity() = mutableState.update {
        it.copy(comboQuantity = (it.comboQuantity + 1).coerceAtMost(15))
    }

    fun decreaseQuantity() = mutableState.update {
        it.copy(comboQuantity = (it.comboQuantity - 1).coerceAtLeast(1))
    }

    fun toggleSurprise() = mutableState.update { it.copy(surprise = !it.surprise) }

    fun addSelectedCombo() {
        val combo = state.value.selectedCombo ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, errorMessage = null) }
            when (val result = addCombo(combo.id, state.value.comboQuantity, state.value.surprise)) {
                is AppResult.Success -> mutableState.update { it.copy(loading = false, addedToCart = true) }
                is AppResult.Failure -> applyFailure(result.error)
            }
        }
    }

    private suspend fun loadModalities() {
        when (val modalities = getModalities()) {
            is AppResult.Failure -> applyFailure(modalities.error)
            is AppResult.Success -> when (val parameters = getParameters()) {
                is AppResult.Failure -> applyFailure(parameters.error)
                is AppResult.Success -> mutableState.update {
                    it.copy(
                        loading = false,
                        modalities = modalities.value.filter { modality -> modality.id > 0 },
                        parameters = parameters.value,
                        errorMessage = null,
                    )
                }
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
                } else null
                if (mode == BettingCatalogMode.COMBO_DETAIL && selected == null) {
                    applyFailure(AppError.Validation("Combo não encontrado."))
                } else {
                    mutableState.update {
                        it.copy(loading = false, combos = combos, selectedCombo = selected, errorMessage = null)
                    }
                }
            }
        }
    }

    private fun applyFailure(error: AppError) = mutableState.update {
        it.copy(loading = false, errorMessage = error.userMessage())
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível carregar as opções de apostas. Verifique sua internet."
        is AppError.Validation -> reason.ifBlank { "Não foi possível atualizar a aposta." }
        AppError.Unexpected -> "Ocorreu um erro inesperado ao carregar as apostas."
    }
}
