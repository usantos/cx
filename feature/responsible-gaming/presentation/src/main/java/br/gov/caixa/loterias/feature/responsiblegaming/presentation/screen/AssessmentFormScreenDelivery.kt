package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.delivery.ResponsibleGamingUiEntry
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Composable
internal fun AssessmentFormScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    privateDataEnabled: Boolean = true,
) {
    FeatureModuleContent(
        module = AppModule.RESPONSIBLE_GAMING,
        entryType = ResponsibleGamingUiEntry::class.java,
        onBack = { if (!state.assessmentRequired) onAction(ResponsibleGamingAction.Back) },
        access = if (state.assessmentRequired) FeatureEntryAccess.REQUIRED else FeatureEntryAccess.OPTIONAL
    ) { entry ->
        entry.AssessmentFormScreen(state, onAction, privateDataEnabled)
    }
}
