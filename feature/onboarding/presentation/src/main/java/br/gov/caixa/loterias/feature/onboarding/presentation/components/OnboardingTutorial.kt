package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.onboarding.presentation.delivery.OnboardingUiEntry

@Composable
fun TutorialScreen(checked: Boolean, onToggle: () -> Unit, onStart: () -> Unit, onSkip: () -> Unit) {
    FeatureModuleContent(
        module = AppModule.ONBOARDING,
        entryType = OnboardingUiEntry::class.java,
        onBack = onSkip,
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = false
    ) { entry ->
        entry.TutorialScreen(checked, onToggle, onStart, onSkip)
    }
}
