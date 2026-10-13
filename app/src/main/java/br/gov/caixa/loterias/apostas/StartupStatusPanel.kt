package br.gov.caixa.loterias.apostas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.onboarding.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.StartupStatusScreen
import br.gov.caixa.loterias.feature.onboarding.startupStatusForConnection
import br.gov.caixa.loterias.core.common.NetworkAvailability

@Composable
internal fun StartupStatusPanel(
    kind: StartupStatusKind,
    onRetry: () -> Unit,
    networkAvailability: NetworkAvailability,
    loading: Boolean = false,
) {
    val context = LocalContext.current
    var availability by remember(networkAvailability, kind) { mutableStateOf(networkAvailability.current()) }
    val displayKind = startupStatusForConnection(kind, availability)
    var storeError by remember { mutableStateOf(false) }
    BackHandler(enabled = loading || kind == StartupStatusKind.UPDATE_REQUIRED) { }
    StartupStatusScreen(
        kind = displayKind,
        appName = stringResource(R.string.status_toolbar_title),
        versionName = BuildConfig.VERSION_NAME,
        loading = loading,
        toolbarIcon = { Image(painterResource(R.drawable.icon_loteria), null) },
        illustration = { modifier ->
            Image(painterResource(if (kind == StartupStatusKind.UPDATE_REQUIRED) R.drawable.ic_download
                else br.gov.caixa.loterias.core.designsystem.R.drawable.ic_indisponivel), null, modifier)
        },
        onAction = {
            if (kind != StartupStatusKind.UPDATE_REQUIRED) {
                availability = networkAvailability.current()
                onRetry()
            } else {
                storeError = !openAppStore(context.packageName) { url ->
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))); true }
                    catch (_: ActivityNotFoundException) { false }
                }
            }
        },
    )
    if (storeError) CaixaDSAlertDialog("Atenção", "Não foi possível abrir a loja de aplicativos.", "Entendi",
        onConfirm = { storeError = false }, onDismiss = { storeError = false })
}
