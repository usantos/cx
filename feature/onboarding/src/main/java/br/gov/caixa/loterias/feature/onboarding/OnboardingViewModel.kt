package br.gov.caixa.loterias.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.domain.onboarding.CompleteIntroductionUseCase
import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial
import br.gov.caixa.loterias.core.domain.onboarding.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val INTRODUCTION_ROUTE = "onboarding/introduction"
private const val LAST_INTRODUCTION_PAGE = 2

data class OnboardingUiState(
    val route: String,
    val introductionPage: Int = 0,
    val ageConfirmationVisible: Boolean = route == INTRODUCTION_ROUTE,
    val tutorialDoNotShowAgain: Boolean = false,
)

sealed interface OnboardingEffect {
    data object Completed : OnboardingEffect
    data object Underage : OnboardingEffect
    data object StartBettingTutorial : OnboardingEffect
    data object SkipBettingTutorial : OnboardingEffect
}

class OnboardingViewModel(
    route: String,
    private val completeIntroduction: CompleteIntroductionUseCase,
    private val setTutorialDismissed: SetBettingTutorialDismissedUseCase =
        SetBettingTutorialDismissedUseCase { _, _ -> },
) : ViewModel() {
    private val mutableState = MutableStateFlow(OnboardingUiState(route = route))
    val state = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<OnboardingEffect>()
    val effects = mutableEffects.asSharedFlow()

    fun pageChanged(page: Int) {
        mutableState.update { it.copy(introductionPage = page.coerceIn(0, LAST_INTRODUCTION_PAGE)) }
    }

    fun confirmAdult() {
        mutableState.update { it.copy(ageConfirmationVisible = false) }
    }

    fun rejectAdult() {
        mutableState.update { it.copy(ageConfirmationVisible = false) }
        emit(OnboardingEffect.Underage)
    }

    fun complete() {
        if (state.value.route == INTRODUCTION_ROUTE) completeIntroduction()
        emit(OnboardingEffect.Completed)
    }

    fun toggleTutorialDoNotShowAgain() {
        mutableState.update { it.copy(tutorialDoNotShowAgain = !it.tutorialDoNotShowAgain) }
    }

    fun startBettingTutorial() {
        if (state.value.tutorialDoNotShowAgain) {
            setTutorialDismissed(BettingTutorial.MAIS_MILIONARIA, true)
        }
        emit(OnboardingEffect.StartBettingTutorial)
    }

    fun skipBettingTutorial() = emit(OnboardingEffect.SkipBettingTutorial)

    private fun emit(effect: OnboardingEffect) {
        viewModelScope.launch { mutableEffects.emit(effect) }
    }
}
