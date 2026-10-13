package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.presentation.WorkflowSpec
import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial
import br.gov.caixa.loterias.core.domain.onboarding.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BettingFlowUiState(
    val spec: WorkflowSpec,
    val page: Int = 0,
    val doNotShowAgain: Boolean = false,
)

sealed interface BettingFlowEffect {
    data object NavigateBack : BettingFlowEffect
    data object Completed : BettingFlowEffect
}

class BettingFlowViewModel(
    spec: WorkflowSpec,
    private val setTutorialDismissed: SetBettingTutorialDismissedUseCase =
        SetBettingTutorialDismissedUseCase { _, _ -> },
) : ViewModel() {
    private val tutorial = tutorialDefinition(spec.route)
    private val mutableState = MutableStateFlow(BettingFlowUiState(spec))
    val state = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<BettingFlowEffect>()
    val effects = mutableEffects.asSharedFlow()

    fun back() = emit(BettingFlowEffect.NavigateBack)

    fun selectPage(page: Int) {
        if (page in tutorial.pages.indices) mutableState.value = mutableState.value.copy(page = page)
    }

    fun toggleDoNotShowAgain() {
        mutableState.value = mutableState.value.copy(doNotShowAgain = !mutableState.value.doNotShowAgain)
    }

    fun primaryAction() {
        val next = tutorialPageAfterPrimary(tutorial, mutableState.value.page)
        if (next == null) {
            persistDismissalWhenRequested()
            emit(BettingFlowEffect.Completed)
        } else selectPage(next)
    }

    fun secondaryAction() {
        val next = tutorialPageAfterSecondary(tutorial, mutableState.value.page)
        if (next == null) {
            persistDismissalWhenRequested()
            emit(BettingFlowEffect.NavigateBack)
        } else selectPage(next)
    }

    private fun persistDismissalWhenRequested() {
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
