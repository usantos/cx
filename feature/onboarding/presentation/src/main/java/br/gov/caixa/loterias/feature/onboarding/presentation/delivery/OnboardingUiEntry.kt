package br.gov.caixa.loterias.feature.onboarding.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.onboarding.presentation.components.IntroductionActions
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingUiState

interface OnboardingUiEntry : FeatureUiEntry {
    @Composable
    fun IntroductionScreen(state: OnboardingUiState, actions: IntroductionActions)

    @Composable
    fun TutorialScreen(checked: Boolean, onToggle: () -> Unit, onStart: () -> Unit, onSkip: () -> Unit)
}
