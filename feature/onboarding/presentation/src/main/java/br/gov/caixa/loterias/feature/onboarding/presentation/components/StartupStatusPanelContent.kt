package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.onboarding.R
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.startupStatusForConnection

@Composable
fun StartupStatusPanelContent(
    kind: StartupStatusKind,
    onRetry: () -> Unit,
    networkAvailability: NetworkAvailability,
    loading: Boolean,
    appName: String,
    versionName: String,
    toolbarIcon: @Composable () -> Unit,
    illustration: @Composable (Modifier) -> Unit,
    onOpenStore: () -> Boolean,
    onExit: (() -> Unit)? = null,
    connectionFailure: Boolean = false
) {
    var availability by remember(networkAvailability, kind) { mutableStateOf(networkAvailability.current()) }
    // Uma falha da página não comprova ausência de internet no aparelho.
    val failureKind = if (connectionFailure && kind == StartupStatusKind.OFFLINE) StartupStatusKind.UNAVAILABLE else kind
    val displayKind = startupStatusForConnection(failureKind, availability)
    var storeError by remember { mutableStateOf(false) }
    var exitConfirmation by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(networkAvailability, kind, loading) { availability = networkAvailability.current() }
    LifecycleResumeEffect(networkAvailability) {
        availability = networkAvailability.current()
        onPauseOrDispose { }
    }
    BackHandler(enabled = onExit != null || loading || kind == StartupStatusKind.UPDATE_REQUIRED) {
        if (kind != StartupStatusKind.UPDATE_REQUIRED && onExit != null) exitConfirmation = true
    }
    StartupStatusScreen(
        kind = displayKind,
        appName = appName,
        versionName = versionName,
        loading = loading,
        toolbarIcon = toolbarIcon,
        illustration = illustration,
        onAction = {
            if (kind != StartupStatusKind.UPDATE_REQUIRED) {
                availability = networkAvailability.current()
                onRetry()
            } else {
                storeError = !onOpenStore()
            }
        }
    )
    if (exitConfirmation) {
        CaixaDSDialog(
            title = stringResource(R.string.startup_attention),
            message = stringResource(R.string.startup_exit_message),
            positiveLabel = stringResource(R.string.startup_exit_confirm),
            onPositive = {
                exitConfirmation = false
                onExit?.invoke()
            },
            negativeLabel = stringResource(R.string.startup_exit_cancel),
            onNegative = { exitConfirmation = false },
            onDismiss = { exitConfirmation = false }
        )
    }
    if (storeError) {
        CaixaDSAlertDialog(
            stringResource(R.string.startup_attention),
            stringResource(R.string.startup_store_error),
            stringResource(R.string.startup_understood),
            onConfirm = { storeError = false },
            onDismiss = { storeError = false }
        )
    }
}
