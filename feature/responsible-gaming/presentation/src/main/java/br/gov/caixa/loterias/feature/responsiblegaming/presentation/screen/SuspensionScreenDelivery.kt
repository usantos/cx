package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.delivery.ResponsibleGamingUiEntry
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Composable
internal fun SuspensionScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    onSubmitSuspension: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.RESPONSIBLE_GAMING,
        entryType = ResponsibleGamingUiEntry::class.java,
        onBack = {},
        access = FeatureEntryAccess.REQUIRED
    ) { entry ->
        entry.SuspensionScreen(state, onAction, onSubmitSuspension)
    }
}
