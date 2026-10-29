package br.gov.caixa.loterias.feature.onboarding.presentation.state

private const val INTRODUCTION_ROUTE = "onboarding/introduction"
private const val LAST_INTRODUCTION_PAGE = 2

data class OnboardingUiState(
    val route: String,
    val introductionPage: Int = 0,
    val ageConfirmationVisible: Boolean = route == INTRODUCTION_ROUTE,
    val tutorialDoNotShowAgain: Boolean = false,
    val storageFailed: Boolean = false,
)

sealed interface OnboardingEffect {
    data object Completed : OnboardingEffect
    data object Underage : OnboardingEffect
    data object StartBettingTutorial : OnboardingEffect
    data object SkipBettingTutorial : OnboardingEffect
}
