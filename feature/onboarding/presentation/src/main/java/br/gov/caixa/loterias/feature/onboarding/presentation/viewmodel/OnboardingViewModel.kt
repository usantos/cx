package br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingEffect
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingUiState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceOnboardingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val INTRODUCTION_ROUTE = "onboarding/introduction"

class OnboardingViewModel(
    route: String,
    private val completeIntroduction: CompleteIntroductionUseCase,
    private val setTutorialDismissed: SetBettingTutorialDismissedUseCase =
        SetBettingTutorialDismissedUseCase { _, _ -> }
) : ViewModel() {
    private val mutableState = reduxStateStore<OnboardingUiState, OnboardingAction>(
        OnboardingUiState(route = route),
        ::reduceOnboardingState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val mutableEffects = SingleConsumerEventQueue<OnboardingEffect>()
    val effects = mutableEffects.events

    fun pageChanged(page: Int) {
        mutableState.dispatch(OnboardingAction.PageChanged(page))
    }

    fun confirmAdult() {
        mutableState.dispatch(OnboardingAction.AgeAnswered)
    }

    fun rejectAdult() {
        mutableState.dispatch(OnboardingAction.AgeAnswered)
        emit(OnboardingEffect.Underage)
    }

    fun complete() = persistAndEmit(OnboardingEffect.Completed) {
        if (state.value.route == INTRODUCTION_ROUTE) completeIntroduction()
    }

    fun toggleTutorialDoNotShowAgain() {
        mutableState.dispatch(OnboardingAction.TutorialPreferenceToggled)
    }

    fun startBettingTutorial() = persistAndEmit(OnboardingEffect.StartBettingTutorial) {
        if (state.value.tutorialDoNotShowAgain) setTutorialDismissed(BettingTutorial.MAIS_MILIONARIA, true)
    }

    fun dismissStorageError() {
        mutableState.dispatch(OnboardingAction.StorageErrorDismissed)
    }

    private fun persistAndEmit(effect: OnboardingEffect, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                mutableEffects.emit(effect)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.dispatch(OnboardingAction.StorageFailed)
            }
        }
    }

    fun skipBettingTutorial() = emit(OnboardingEffect.SkipBettingTutorial)

    private fun emit(effect: OnboardingEffect) {
        viewModelScope.launch { mutableEffects.emit(effect) }
    }
}
