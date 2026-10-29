package br.gov.caixa.loterias.apostas.initialization.startup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.R as LocalizedR
import br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies
import br.gov.caixa.loterias.apostas.composition.startup.StartupGateContent
import br.gov.caixa.loterias.apostas.initialization.AppInitializationDependencies
import br.gov.caixa.loterias.apostas.navigation.AppNavHost
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.startup.mockStartDestination
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSStatusAction as StatusAction
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.StartupViewModel

@Composable
internal fun MockBackendAppGate(
    initialization: AppInitializationDependencies,
    featureDependencies: AppFeatureGraphDependencies,
    session: AppSessionDependencies,
    minimumSplashElapsed: Boolean,
    onForegroundSessionRestart: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val scenario = remember { MockBackendScenario.from(BuildConfig.MOCK_BACKEND_SCENARIO) }
    var suspensionAcknowledged by remember { mutableStateOf(false) }
    val startupViewModel = viewModel(key = "mock-startup-${scenario.name}") {
        StartupViewModel(
            isVersionSupported = initialization.isVersionSupported,
            getFilterParameters = initialization.marketplaceFilterParameters,
            versionName = BuildConfig.VERSION_NAME,
            prepareNetworkSecurity = initialization.networkSecurity,
            dispatchers = initialization.dispatchers
        )
    }
    val startupState by startupViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(startupViewModel) { startupViewModel.queuePassed() }
    StartupGateContent(startupState, startupViewModel::retry, initialization.networkAvailability) {
        if (minimumSplashElapsed) {
            if (scenario == MockBackendScenario.ACCOUNT_SUSPENDED && !suspensionAcknowledged) {
                StatusAction(
                    title = uiStrings.getString(LocalizedR.string.ui_conta_temporariamente_suspensa_e2ea25),
                    message = uiStrings.getString(LocalizedR.string.ui_sua_conta_esta_temporariamente_suspe_decbd5),
                    action = stringResource(LocalizedR.string.ui_entendi_9f9373),
                    onAction = { suspensionAcknowledged = true }
                )
            } else {
                AppNavHost(
                    featureDependencies,
                    session,
                    startDestination = mockStartDestination(scenario),
                    onForegroundSessionRestart = onForegroundSessionRestart
                )
            }
        } else {
            SplashScreen(modifier = Modifier.uiId("app.0006"))
        }
    }
}
