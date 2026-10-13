package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.feature.onboarding.LegacySplashScreen
import br.gov.caixa.loterias.feature.onboarding.StartupState
import br.gov.caixa.loterias.feature.onboarding.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.StartupViewModel
import br.gov.caixa.loterias.core.common.NetworkAvailability

/** Direct status routes use the same validation as the application entry gate. */
@Composable
internal fun StartupStatusDestination(route: String, container: AppContainer, onRecovered: () -> Unit) {
    val initialKind = when (route) {
        "onboarding/update" -> StartupStatusKind.UPDATE_REQUIRED
        "onboarding/unavailable" -> StartupStatusKind.UNAVAILABLE
        else -> error("Unknown startup status route: $route")
    }
    val viewModel = viewModel(key = "startup-status-$route") {
        StartupViewModel(container.isVersionSupported, container.marketplaceFilterParameters,
            BuildConfig.VERSION_NAME, container.networkSecurity, container.dispatchers)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    StartupGateContent(state, viewModel::retry, container.networkAvailability, initialKind) {
        LaunchedEffect(viewModel) { onRecovered() }
    }
}

/** Keep the error layout underneath the loading overlay during a retry. */
@Composable
internal fun StartupGateContent(
    state: StartupState,
    onRetry: () -> Unit,
    networkAvailability: NetworkAvailability,
    initialKind: StartupStatusKind? = null,
    ready: @Composable () -> Unit,
) {
    var retryKind by remember { mutableStateOf<StartupStatusKind?>(null) }
    if (state == StartupState.READY) {
        ready()
        return
    }
    val kind = when (state) {
        StartupState.UPDATE_REQUIRED -> StartupStatusKind.UPDATE_REQUIRED
        StartupState.UNAVAILABLE -> StartupStatusKind.UNAVAILABLE
        else -> retryKind ?: initialKind
    }
    if (kind == null) LegacySplashScreen() else StartupStatusPanel(
        kind = kind,
        networkAvailability = networkAvailability,
        loading = state == StartupState.CHECKING,
        onRetry = { retryKind = kind; onRetry() },
    )
}
