package br.gov.caixa.loterias.apostas.composition.startup

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.navigation.destination.openAppStore
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind

@Composable
internal fun AppStartupStatusPanel(
    kind: StartupStatusKind,
    onRetry: () -> Unit,
    networkAvailability: NetworkAvailability,
    loading: Boolean = false,
    connectionFailure: Boolean = false,
) {
    val context = LocalContext.current
    val activity = androidx.activity.compose.LocalActivity.current
    br.gov.caixa.loterias.feature.onboarding.presentation.components.StartupStatusPanelContent(
        kind = kind, onRetry = onRetry, networkAvailability = networkAvailability, loading = loading,
        connectionFailure = connectionFailure,
        appName = stringResource(R.string.status_toolbar_title), versionName = BuildConfig.VERSION_NAME,
        toolbarIcon = { Image(painterResource(br.gov.caixa.loterias.feature.home.R.drawable.icon_loteria), null) },
        illustration = { modifier ->
            Image(painterResource(if (kind == StartupStatusKind.UPDATE_REQUIRED) br.gov.caixa.loterias.feature.home.R.drawable.ic_download
                else br.gov.caixa.loterias.core.designsystem.R.drawable.ic_indisponivel), null, modifier)
        },
        onExit = { activity?.finishAndRemoveTask() },
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
}
