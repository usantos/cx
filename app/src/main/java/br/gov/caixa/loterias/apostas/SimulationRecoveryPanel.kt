package br.gov.caixa.loterias.apostas

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.onboarding.StartupStatusKind
import br.gov.caixa.loterias.core.common.NetworkAvailability

/** Recovery owned by the originating feature, without completing onboarding or clearing its data. */
@Composable
internal fun SimulationRecoveryPanel(
    loading: Boolean,
    message: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onDismissMessage: () -> Unit,
    networkAvailability: NetworkAvailability,
) {
    BackHandler { if (!loading) onBack() }
    StartupStatusPanel(StartupStatusKind.UNAVAILABLE, onRetry, networkAvailability, loading)
    if (message != null) CaixaDSAlertDialog(
        title = "Atenção", message = message, confirmText = "Entendi",
        onConfirm = onDismissMessage,
    )
}
