package br.gov.caixa.loterias.feature.onboarding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupState
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.StartupViewModel

@Composable
internal fun OnboardingStartupStatusDestination(
    route: String,
    onRecovered: () -> Unit,
    versionName: String,
    isVersionSupported: IsVersionSupportedUseCase,
    marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase,
    networkSecurity: PrepareNetworkSecurityUseCase,
    networkAvailability: NetworkAvailability,
    dispatchers: CoroutineDispatchers,
    statusPresentation: StartupStatusPresentation,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val initialKind = when (route) {
        "onboarding/update" -> StartupStatusKind.UPDATE_REQUIRED
        "onboarding/unavailable" -> StartupStatusKind.UNAVAILABLE
        else -> error(uiStrings.getString(LocalizedR.string.ui_unknown_startup_status_route_1_s_3643a7, route))
    }
    val viewModel = viewModel(key = "startup-status-$route") {
        StartupViewModel(isVersionSupported, marketplaceFilterParameters, versionName, networkSecurity, dispatchers)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var retryKind by remember { mutableStateOf<StartupStatusKind?>(null) }
    if (state == StartupState.READY) {
        LaunchedEffect(viewModel) { onRecovered() }
        return
    }
    val kind = when (state) {
        StartupState.UPDATE_REQUIRED -> StartupStatusKind.UPDATE_REQUIRED
        StartupState.UNAVAILABLE -> StartupStatusKind.UNAVAILABLE
        else -> retryKind ?: initialKind
    }
    statusPresentation.Content(
        kind = kind,
        onRetry = {
            retryKind = kind
            viewModel.retry()
        },
        networkAvailability = networkAvailability,
        loading = state == StartupState.CHECKING
    )
}
