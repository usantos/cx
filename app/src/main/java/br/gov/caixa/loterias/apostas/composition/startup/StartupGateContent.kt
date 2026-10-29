package br.gov.caixa.loterias.apostas.composition.startup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupState

/** Mantém a tela de status visível enquanto o botão de nova tentativa indica progresso. */
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
    if (kind == null) {
        SplashScreen(modifier = Modifier.uiId("app.0004"))
    } else {
        AppStartupStatusPanel(
            kind = kind,
            networkAvailability = networkAvailability,
            loading = state == StartupState.CHECKING,
            onRetry = {
                retryKind = kind
                onRetry()
            }
        )
    }
}
