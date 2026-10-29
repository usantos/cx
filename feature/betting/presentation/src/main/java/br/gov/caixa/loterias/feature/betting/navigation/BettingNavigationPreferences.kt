package br.gov.caixa.loterias.feature.betting.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingNavigationPreferencesState
import br.gov.caixa.loterias.feature.betting.presentation.state.NavigationPreferenceFailure
import br.gov.caixa.loterias.feature.betting.presentation.state.NavigationPreferenceMode
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingNavigationPreferencesViewModel

@Composable
internal fun rememberNavigationPreferences(
    dependencies: BettingNavigationDependencies,
    session: SessionNavigationActions,
    mode: NavigationPreferenceMode,
): BettingNavigationPreferencesViewModel {
    val model = viewModel(key = "navigation-preferences-$mode-${session.privateEntryKey}") {
        BettingNavigationPreferencesViewModel(
            mode,
            dependencies.bettingTutorialDismissed,
            dependencies.setBettingTutorialDismissed,
            dependencies.shakeEnabled,
            dependencies.dispatchers,
            isSessionCurrent = { session.identity == null || session.isPrivateSessionCurrent() }
        )
    }
    DisposableEffect(model) {
        model.activate()
        onDispose { model.deactivate() }
    }
    return model
}

@Composable
internal fun navigationShakeEnabled(
    dependencies: BettingNavigationDependencies,
    session: SessionNavigationActions,
    onBack: () -> Unit,
): Boolean {
    val model = rememberNavigationPreferences(dependencies, session, NavigationPreferenceMode.SHAKE)
    val state by model.state.collectAsStateWithLifecycle()
    NavigationPreferenceFailureDialog(state, model::retry, onBack)
    return state.shakeEnabled
}

@Composable
internal fun NavigationPreferenceFailureDialog(state: BettingNavigationPreferencesState, onRetry: () -> Unit, onBack: () -> Unit) {
    val failure = state.failure ?: return
    CaixaDSDialog(
        title = stringResource(R.string.ui_atencao_b0f8a4),
        message = stringResource(
            if (failure == NavigationPreferenceFailure.READ) {
                R.string.betting_navigation_preferences_read_failed
            } else {
                R.string.betting_navigation_preferences_write_failed
            }
        ),
        positiveLabel = stringResource(R.string.ui_tentar_novamente_b9e106),
        onPositive = onRetry,
        negativeLabel = stringResource(R.string.ui_cancelar_bb9dbb),
        onNegative = onBack
    )
}
