package br.gov.caixa.loterias.apostas.initialization.startup

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.apostas.composition.startup.AppStartupStatusPanel
import br.gov.caixa.loterias.apostas.initialization.AppInitializationDependencies
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.HostStartupViewModel

@Composable
internal fun FilaBraInitialization(dependencies: AppInitializationDependencies, content: @Composable (Boolean) -> Unit) {
    val model = viewModel { HostStartupViewModel(dependencies.networkSecurity) }
    val state by model.state.collectAsStateWithLifecycle()
    DisposableEffect(model) {
        model.start()
        onDispose { model.stop() }
    }
    if (state.securityFailed || (!state.securityReady && state.attempt > 0)) {
        AppStartupStatusPanel(
            networkAvailability = dependencies.networkAvailability,
            kind = StartupStatusKind.UNAVAILABLE,
            loading = !state.securityFailed,
            onRetry = model::retry
        )
    } else if (!state.securityReady) {
        SplashScreen(modifier = Modifier.uiId("app.0005"))
    } else {
        content(state.minimumSplashElapsed)
    }
}
