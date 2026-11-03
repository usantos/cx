package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.onboarding.presentation.delivery.OnboardingUiEntry
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingUiState

@Composable
fun IntroductionScreen(state: OnboardingUiState, actions: IntroductionActions) {
    FeatureModuleContent(
        module = AppModule.ONBOARDING,
        entryType = OnboardingUiEntry::class.java,
        onBack = actions.complete,
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = false
    ) { entry ->
        entry.IntroductionScreen(state, actions)
    }
    if (state.ageConfirmationVisible) {
        AgeConfirmationDialog(
            onConfirmAdult = actions.confirmAdult,
            onRejectAdult = actions.rejectAdult
        )
    }
}
