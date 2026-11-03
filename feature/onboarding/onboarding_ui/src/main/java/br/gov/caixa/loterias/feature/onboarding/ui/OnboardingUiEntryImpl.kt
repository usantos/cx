package br.gov.caixa.loterias.feature.onboarding.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.onboarding.presentation.components.IntroductionActions
import br.gov.caixa.loterias.feature.onboarding.presentation.components.RenderIntroductionScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.components.RenderTutorialScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.delivery.OnboardingUiEntry
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingUiState

@Keep
class OnboardingUiEntryImpl : OnboardingUiEntry {
    @Composable
    override fun IntroductionScreen(state: OnboardingUiState, actions: IntroductionActions) {
        RenderIntroductionScreen(state, actions)
    }

    @Composable
    override fun TutorialScreen(checked: Boolean, onToggle: () -> Unit, onStart: () -> Unit, onSkip: () -> Unit) {
        RenderTutorialScreen(checked, onToggle, onStart, onSkip)
    }
}
