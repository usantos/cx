package br.gov.caixa.loterias.feature.support.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.rule.lotteryGameRulesHtml
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase
import br.gov.caixa.loterias.feature.support.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMutation
import br.gov.caixa.loterias.feature.support.presentation.state.SupportStateStore
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SupportViewModel(
    private val getSections: GetSupportSectionsUseCase,
    private val getQuestions: GetSupportQuestionsUseCase,
    private val getTerms: GetTermsUseCase,
    private val acceptTermsUseCase: AcceptTermsUseCase,
    private val privacyHtml: String,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    initialState: SupportUiState = SupportUiState(),
    private val getBettingParameters: GetBettingParametersUseCase? = null,
) : ViewModel() {
    private val stateStore = SupportStateStore(initialState).also { addCloseable(it) }
    val state = stateStore.state

    private val requestIds = AtomicLong()
    private var loadJob: Job? = null

    private val mutableTermsAccepted = SingleConsumerEventQueue<Unit>()
    val termsAccepted = mutableTermsAccepted.events

    init {
        loadSelectedContent()
    }

    fun onAction(action: SupportAction) {
        stateStore.dispatch(SupportMutation.Action(action))
        when (action) {
            is SupportAction.SelectTab -> loadSelectedContent()
            is SupportAction.OpenSection -> loadQuestions(action.id)
            SupportAction.CloseSection -> loadJob?.cancel()
            SupportAction.AcceptTerms -> acceptTerms()
            else -> Unit
        }
    }

    private fun loadQuestions(sectionId: Long) = launchLoad { requestId ->
        when (val outcome = getQuestions(sectionId)) {
            is AppResult.Success -> completeLoad(requestId, SupportAction.QuestionsLoaded(outcome.value))
            is AppResult.Failure -> showLoadFailure(requestId)
        }
    }

    fun loadSelectedContent() {
        if (state.value.mode == SupportMode.GAME_HELP) {
            loadGameHelp()
            return
        }
        val selectedTab = state.value.selectedTab
        launchLoad { requestId ->
            when (selectedTab) {
                SupportTab.FAQ -> when (val outcome = getSections()) {
                    is AppResult.Success -> completeLoad(requestId, SupportAction.SectionsLoaded(outcome.value))
                    is AppResult.Failure -> showLoadFailure(requestId)
                }

                SupportTab.TERMS -> when (val outcome = getTerms()) {
                    is AppResult.Success -> completeLoad(requestId, SupportAction.ContentLoaded(outcome.value))
                    is AppResult.Failure -> showLoadFailure(requestId)
                }

                SupportTab.PRIVACY -> completeLoad(requestId, SupportAction.ContentLoaded(privacyHtml))
            }
        }
    }

    private fun launchLoad(block: suspend (Long) -> Unit) {
        loadJob?.cancel()
        val requestId = requestIds.incrementAndGet()
        stateStore.dispatch(SupportMutation.LoadStarted(requestId))
        loadJob = viewModelScope.launch(dispatchers.io) {
            try {
                block(requestId)
            } finally {
                stateStore.dispatch(SupportMutation.LoadFinished(requestId))
            }
        }
    }

    private fun completeLoad(requestId: Long, action: SupportAction) {
        stateStore.dispatch(SupportMutation.LoadCompleted(requestId, action))
    }

    private fun loadGameHelp() {
        val rules = lotteryGameRulesHtml(state.value.gameHelpModalityId)
            .ifBlank { messageText("as_regras_desta_modalidade_nao_e_069199") }
        stateStore.dispatch(SupportMutation.GameHelpLoaded(rules))
        val parameters = getBettingParameters ?: return
        val modalityId = state.value.gameHelpModalityId
        launchLoad load@{ requestId ->
            val outcome = parameters()
            if (outcome is AppResult.Success) {
                val game = outcome.value.games.firstOrNull {
                    bettingModalityId(it.modality.id) == bettingModalityId(modalityId)
                } ?: return@load
                stateStore.dispatch(
                    SupportMutation.ParametersReceived(
                        requestId,
                        SupportMutation.GameParametersLoaded(
                            game.contestNumber.takeIf { number -> number > 0 },
                            game.drawDate,
                            game.closingDateTime
                        )
                    )
                )
            }
        }
    }

    private fun bettingModalityId(id: Int): Int = when (id) {
        3 -> 1
        16 -> 5
        13 -> 19
        else -> id
    }

    private fun showLoadFailure(requestId: Long) {
        completeLoad(requestId, SupportAction.LoadFailed(messageText("nao_foi_possivel_carregar_o_cont_63e02a")))
    }

    private fun acceptTerms() {
        if (state.value.mode == SupportMode.TERMS_READ_ONLY || !state.value.termsAccepted) return
        viewModelScope.launch(dispatchers.io) {
            when (acceptTermsUseCase()) {
                is AppResult.Success<*> -> mutableTermsAccepted.emit(Unit)

                is AppResult.Failure -> stateStore.dispatch(
                    SupportMutation.AcceptanceFailed(messageText("nao_foi_possivel_aceitar_os_term_5a4c46"))
                )
            }
        }
    }
}
