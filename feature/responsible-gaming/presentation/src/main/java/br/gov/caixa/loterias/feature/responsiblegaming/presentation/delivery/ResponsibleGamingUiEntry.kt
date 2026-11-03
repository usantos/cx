package br.gov.caixa.loterias.feature.responsiblegaming.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

interface ResponsibleGamingUiEntry : FeatureUiEntry {
    @Composable
    fun SuspensionScreen(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit, onSubmitSuspension: () -> Unit)

    @Composable
    fun AssessmentFormScreen(
        state: ResponsibleGamingUiState,
        onAction: (ResponsibleGamingAction) -> Unit,
        privateDataEnabled: Boolean = true,
    )

    @Composable
    fun AssessmentStartScreen(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit)

    @Composable
    fun SocialTransfersScreen(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit, onOpenTerms: () -> Unit)
}
