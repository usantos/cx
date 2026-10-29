package br.gov.caixa.loterias.feature.onboarding.presentation.state

sealed interface OnboardingAction {
    data class PageChanged(val page: Int) : OnboardingAction
    data object AgeAnswered : OnboardingAction
    data object TutorialPreferenceToggled : OnboardingAction
    data object StorageErrorDismissed : OnboardingAction
    data object StorageFailed : OnboardingAction
}

fun reduceOnboardingState(state: OnboardingUiState, action: OnboardingAction): OnboardingUiState = when (action) {
    is OnboardingAction.PageChanged -> state.copy(introductionPage = action.page.coerceIn(0, 2))
    OnboardingAction.AgeAnswered -> state.copy(ageConfirmationVisible = false)
    OnboardingAction.TutorialPreferenceToggled -> state.copy(tutorialDoNotShowAgain = !state.tutorialDoNotShowAgain)
    OnboardingAction.StorageErrorDismissed -> state.copy(storageFailed = false)
    OnboardingAction.StorageFailed -> state.copy(storageFailed = true)
}
