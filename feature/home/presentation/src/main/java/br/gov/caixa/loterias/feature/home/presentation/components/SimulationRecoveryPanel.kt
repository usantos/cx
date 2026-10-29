package br.gov.caixa.loterias.feature.home.presentation.components

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.navigation.destination.openAppStore
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.onboarding.presentation.components.StartupStatusPanelContent
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind

@Composable
internal fun SimulationRecoveryPanel(
    loading: Boolean,
    message: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onDismissMessage: () -> Unit,
    networkAvailability: NetworkAvailability,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BackHandler { if (!loading) onBack() }
    val context = LocalContext.current
    StartupStatusPanelContent(
        kind = StartupStatusKind.UNAVAILABLE,
        onRetry = onRetry,
        networkAvailability = networkAvailability,
        loading = loading,
        appName = stringResource(R.string.status_toolbar_title),
        versionName = LocalHomeRuntimeConfiguration.current.versionName,
        toolbarIcon = { Image(painterResource(R.drawable.icon_loteria), null) },
        illustration = { modifier ->
            Image(painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.ic_indisponivel), null, modifier)
        },
        onOpenStore = {
            openAppStore(context.packageName) { url ->
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                    true
                } catch (_: ActivityNotFoundException) {
                    false
                }
            }
        }
    )
    if (message != null) {
        CaixaDSAlertDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            confirmText = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onConfirm = onDismissMessage
        )
    }
}
