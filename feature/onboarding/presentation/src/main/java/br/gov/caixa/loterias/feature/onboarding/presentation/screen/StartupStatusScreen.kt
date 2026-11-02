package br.gov.caixa.loterias.feature.onboarding.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal fun legacyStatusContentMiddle(availableHeight: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp = availableHeight / 2

/** A tela de indisponibilidade de produção segue activity_app_indisponivel_novo do aplicativo CX. */
@Composable
fun StartupStatusScreen(
    kind: StartupStatusKind,
    appName: String,
    versionName: String,
    onAction: () -> Unit,
    toolbarIcon: @Composable () -> Unit,
    illustration: @Composable (Modifier) -> Unit,
    loading: Boolean = false,
) {
    if (kind == StartupStatusKind.UPDATE_REQUIRED) {
        StartupUpdateStatusScreen(
            kind = kind,
            appName = appName,
            versionName = versionName,
            onAction = onAction,
            toolbarIcon = toolbarIcon,
            illustration = illustration
        )
    } else {
        StartupUnavailableScreen(
            kind = kind,
            appName = appName,
            versionName = versionName,
            onAction = onAction,
            toolbarIcon = toolbarIcon,
            loading = loading
        )
    }
}
