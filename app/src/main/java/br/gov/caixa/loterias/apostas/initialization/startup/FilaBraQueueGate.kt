package br.gov.caixa.loterias.apostas.initialization.startup

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies
import br.gov.caixa.loterias.apostas.composition.startup.AppStartupStatusPanel
import br.gov.caixa.loterias.apostas.composition.startup.StartupGateContent
import br.gov.caixa.loterias.apostas.initialization.AppInitializationDependencies
import br.gov.caixa.loterias.apostas.navigation.AppNavHost
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.startup.startupDestination
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.queue.gateway.FilaBraQueueGateway
import br.gov.caixa.loterias.feature.account.navigation.AccountEnvironmentDependencies
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.StartupViewModel

@Composable
internal fun FilaBraQueueGate(
    activity: Activity,
    initialization: AppInitializationDependencies,
    featureDependencies: AppFeatureGraphDependencies,
    session: AppSessionDependencies,
    environment: AccountEnvironmentDependencies,
    minimumSplashElapsed: Boolean,
    onForegroundSessionRestart: () -> Unit,
) {
    val queue = viewModel { br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.QueueGateViewModel() }
    androidx.compose.runtime.DisposableEffect(queue) { onDispose { queue.reset() } }
    val queueState by queue.state.collectAsStateWithLifecycle()
    val state = queueState.status
    val attempt = queueState.attempt
    val gateway = remember {
        FilaBraQueueGateway(environment.effectiveEnvironment.queueAccount, environment.effectiveEnvironment.queueSystem)
    }
    val startupViewModel = viewModel {
        StartupViewModel(
            isVersionSupported = initialization.isVersionSupported,
            getFilterParameters = initialization.marketplaceFilterParameters,
            versionName = BuildConfig.VERSION_NAME,
            prepareNetworkSecurity = initialization.networkSecurity,
            dispatchers = initialization.dispatchers
        )
    }
    val startupState by startupViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(attempt) {
        val request = queue.begin()
        br.gov.caixa.loterias.core.queue.android.startFilaBraQueue(
            gateway,
            activity,
            br.gov.caixa.loterias.apostas.QueueActivity::class.java,
            isCurrent = { queue.isCurrent(request) },
            onPass = { if (queue.passed(request)) startupViewModel.queuePassed() },
            onNoInternet = { queue.noInternet(request) },
            onError = { queue.failed(request) }
        )
    }

    when (state) {
        br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueStatus.PASSED -> StartupGateContent(
            startupState,
            startupViewModel::retry,
            initialization.networkAvailability
        ) {
            if (minimumSplashElapsed) {
                AppNavHost(
                    featureDependencies,
                    session,
                    startDestination = remember {
                        initialSessionDestination(session)
                    },
                    onForegroundSessionRestart = onForegroundSessionRestart
                )
            } else {
                SplashScreen(modifier = Modifier.uiId("app.0007"))
            }
        }

        br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueStatus.WAITING -> if (attempt > 0) {
            AppStartupStatusPanel(StartupStatusKind.OFFLINE, onRetry = {
            }, networkAvailability = initialization.networkAvailability, loading = true)
        } else {
            SplashScreen(modifier = Modifier.uiId("app.0008"))
        }

        br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueStatus.NO_INTERNET -> AppStartupStatusPanel(
            StartupStatusKind.OFFLINE,
            onRetry = queue::retry,
            networkAvailability = initialization.networkAvailability
        )

        br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueStatus.ERROR -> AppStartupStatusPanel(
            StartupStatusKind.UNAVAILABLE,
            onRetry = queue::retry,
            networkAvailability = initialization.networkAvailability
        )
    }
}

private fun initialSessionDestination(session: AppSessionDependencies): String {
    val access = session.account.foregroundSession
    val check = access.foregroundCheck(session.localClock.instant().epochSecond)
    if (check.status == br.gov.caixa.loterias.core.auth.session.ForegroundSessionStatus.EXPIRED ||
        check.status == br.gov.caixa.loterias.core.auth.session.ForegroundSessionStatus.MISSING
    ) {
        // Limpa apenas este retrato; um login substituto nunca é invalidado.
        access.clearForegroundSession(check)
    }
    val biometric = session.account.authentication as?
        br.gov.caixa.loterias.feature.account.domain.port.BiometricLoginPreferences
    return startupDestination(
        check.status,
        hasIdentity = session.account.authenticatedSessionIdentity.captureIdentity() != null,
        rememberedLoginAvailable = biometric?.let {
            it.biometricEnabled && it.biometricAvailable && it.biometricCredentialAvailable
        } == true
    )
}
