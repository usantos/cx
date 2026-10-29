package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR

@Composable
fun SimulationRecoveryPanel(
    loading: Boolean,
    message: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onDismissMessage: () -> Unit,
    networkAvailability: NetworkAvailability,
    statusPanel: @Composable (loading: Boolean, onRetry: () -> Unit, networkAvailability: NetworkAvailability) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BackHandler { if (!loading) onBack() }
    statusPanel(loading, onRetry, networkAvailability)
    if (message != null) {
        CaixaDSAlertDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            confirmText = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onConfirm = onDismissMessage
        )
    }
}
