package br.gov.caixa.loterias.feature.betting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialPageAfterPrimary
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialPageAfterSecondary
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowEffect
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingFlowState
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.launch

class BettingFlowViewModel(
    spec: WorkflowSpec,
    private val setTutorialDismissed: SetBettingTutorialDismissedUseCase =
        SetBettingTutorialDismissedUseCase { _, _ -> },
) : ViewModel() {
    private val tutorial = tutorialDefinition(spec.route)
    private val mutableState = reduxStateStore<BettingFlowUiState, BettingFlowAction>(
        BettingFlowUiState(spec),
        ::reduceBettingFlowState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val mutableEffects = SingleConsumerEventQueue<BettingFlowEffect>()
    val effects = mutableEffects.events

    fun back() = emit(BettingFlowEffect.NavigateBack)

    fun selectPage(page: Int) {
        if (page in tutorial.pages.indices) mutableState.dispatch(BettingFlowAction.PageSelected(page))
    }

    fun toggleDoNotShowAgain() {
        mutableState.dispatch(BettingFlowAction.DismissalToggled)
    }

    fun primaryAction() {
        val next = tutorialPageAfterPrimary(tutorial, mutableState.value.page)
        if (next == null) {
            viewModelScope.launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
                persistDismissalWhenRequested()
                mutableEffects.emit(BettingFlowEffect.Completed)
            }
        } else {
            selectPage(next)
        }
    }

    fun secondaryAction() {
        val next = tutorialPageAfterSecondary(tutorial, mutableState.value.page)
        if (next == null) {
            viewModelScope.launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
                persistDismissalWhenRequested()
                mutableEffects.emit(BettingFlowEffect.NavigateBack)
            }
        } else {
            selectPage(next)
        }
    }

    private suspend fun persistDismissalWhenRequested() {
        val kind = when (mutableState.value.spec.route) {
            "betting/tutorial" -> BettingTutorial.POOL
            "betting/tutorial-plus" -> BettingTutorial.MAIS_MILIONARIA
            "betting/tutorial-super-seven" -> BettingTutorial.SUPER_SETE
            else -> return
        }
        val dismissed = kind == BettingTutorial.SUPER_SETE || mutableState.value.doNotShowAgain
        if (dismissed) setTutorialDismissed(kind, true)
    }

    private fun emit(effect: BettingFlowEffect) {
        viewModelScope.launch { mutableEffects.emit(effect) }
    }
}
