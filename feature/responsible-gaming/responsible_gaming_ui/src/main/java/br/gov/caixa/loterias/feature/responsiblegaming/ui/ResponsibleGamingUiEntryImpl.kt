package br.gov.caixa.loterias.feature.responsiblegaming.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.delivery.ResponsibleGamingUiEntry
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Keep
class ResponsibleGamingUiEntryImpl : ResponsibleGamingUiEntry {
    @Composable
    override fun SuspensionScreen(
        state: ResponsibleGamingUiState,
        onAction: (ResponsibleGamingAction) -> Unit,
        onSubmitSuspension: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.DeliveredSuspensionScreen(state, onAction, onSubmitSuspension)
    }

    @Composable
    override fun AssessmentFormScreen(
        state: ResponsibleGamingUiState,
        onAction: (ResponsibleGamingAction) -> Unit,
        privateDataEnabled: Boolean,
    ) {
        br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.DeliveredAssessmentFormScreen(
            state,
            onAction,
            privateDataEnabled
        )
    }

    @Composable
    override fun AssessmentStartScreen(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
        br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.DeliveredAssessmentStartScreen(state, onAction)
    }

    @Composable
    override fun SocialTransfersScreen(
        state: ResponsibleGamingUiState,
        onAction: (ResponsibleGamingAction) -> Unit,
        onOpenTerms: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.DeliveredSocialTransfersScreen(state, onAction, onOpenTerms)
    }
}
