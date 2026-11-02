package br.gov.caixa.loterias.apostas.navigation.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.apostas.R as LocalizedR
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionLifecycleObserver
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionNoticeController
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.navigation.state.AppSessionState

@Composable
internal fun rememberForegroundSessionController(
    sessionDependencies: AppSessionDependencies,
    navController: androidx.navigation.NavHostController,
    state: AppSessionState,
    resetForegroundNavigation: () -> Unit,
): ForegroundSessionNoticeController {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val latestForegroundReset by androidx.compose.runtime.rememberUpdatedState(resetForegroundNavigation)
    val foregroundNoticeController = remember(sessionDependencies, navController) {
        ForegroundSessionNoticeController(
            sessionDependencies.account.foregroundSession,
            { sessionDependencies.localClock.instant().epochSecond },
            { latestForegroundReset() }
        )
    }
    val lifecycleOwner = androidx.lifecycle.ProcessLifecycleOwner.get()
    val foregroundObserver = remember(sessionDependencies, navController) {
        ForegroundSessionLifecycleObserver(
            sessionDependencies.account.foregroundSession,
            currentRoute = { navController.currentBackStackEntry?.destination?.route },
            nowSeconds = { sessionDependencies.localClock.instant().epochSecond },
            onLogin = { latestForegroundReset() },
            onExpired = { state.foregroundExpiryMarker = foregroundNoticeController.show(it) },
            backgroundState = sessionDependencies.foregroundBackground,
            prizeChannelHost = { state.prizeChannelHost },
            routeEligible = br.gov.caixa.loterias.apostas.navigation.session.ForegroundSessionRoutePolicy::eligible
        )
    }
    LaunchedEffect(state.prizeChannelHost) { foregroundObserver.onRouteEntered() }
    DisposableEffect(lifecycleOwner, foregroundObserver, navController) {
        val observer = foregroundObserver
        val destinationListener = androidx.navigation.NavController.OnDestinationChangedListener { _, _, _ ->
            observer.onRouteEntered()
        }
        navController.addOnDestinationChangedListener(destinationListener)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            navController.removeOnDestinationChangedListener(destinationListener)
        }
    }
    state.foregroundExpiryMarker?.let { marker ->
        foregroundNoticeController.restore(marker)?.let { accept ->
            LoteriasCaixaDSDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = br.gov.caixa.loterias.core.presentation.formatting.LEGACY_LOGIN_REQUIRED,
                positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
                onPositive = {
                    state.foregroundExpiryMarker = null
                    accept()
                }
            )
        }
    }
    return foregroundNoticeController
}
