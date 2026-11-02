package br.gov.caixa.loterias.apostas.navigation.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.DetailRestorationOutcome
import br.gov.caixa.loterias.core.navigation.state.authenticatedContinuation
import br.gov.caixa.loterias.core.navigation.state.postLoginDestinationRoute
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.presentation.components.AccountCompletionDialogs
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountCompletionViewModel

@Composable
internal fun AccountCompletionNavigation(
    completionState: br.gov.caixa.loterias.feature.account.presentation.state.AccountCompletionState,
    completion: AccountCompletionViewModel,
    sessionDependencies: AppSessionDependencies,
    navigation: AppNavigationState,
    state: AppSessionState,
    navController: androidx.navigation.NavHostController,
) {
    LaunchedEffect(completionState.outcome) {
        val outcome = completionState.outcome ?: return@LaunchedEffect
        try {
            val lease = outcome.identity
            if (!sessionDependencies.account.authenticatedSessionIdentity.isCurrent(lease)) return@LaunchedEffect
            state.accountCompletionRequired = outcome.destination != PostLoginDestination.HOME
            if (!state.accountCompletionRequired && state.pendingRestoredRoute != null) {
                navigation.details.reconcile(lease.ownerKey)
                state.visitorReturnRoute =
                    if (navigation.details.restorationOutcome == DetailRestorationOutcome.Restored) state.pendingRestoredRoute else null
                state.visitorReturnOwnerKey = lease.ownerKey
                state.pendingRestoredRoute = null
            }
            state.visitorReturnRoute = authenticatedContinuation(state.visitorReturnRoute, state.visitorReturnOwnerKey, lease.ownerKey)
            navController.navigate(postLoginDestinationRoute(outcome.destination)) { launchSingleTop = true }
        } finally {
            completion.acknowledge(outcome)
        }
    }
    AccountCompletionDialogs(
        completionState,
        onRetry = {
            completion.dismissError()
            completion.recheck()
        },
        onDismiss = completion::dismissError
    )
}
