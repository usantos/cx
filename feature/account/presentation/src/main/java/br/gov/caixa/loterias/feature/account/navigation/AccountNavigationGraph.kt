package br.gov.caixa.loterias.feature.account.navigation

import android.net.Uri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.core.navigation.workflow.accountStatusRoutes
import br.gov.caixa.loterias.core.navigation.workflow.nextDestination
import br.gov.caixa.loterias.core.navigation.workflow.opensVisitorDestinationBeforeLogin
import br.gov.caixa.loterias.feature.account.presentation.route.AccountRoute
import br.gov.caixa.loterias.feature.account.presentation.route.LoginRoute
import br.gov.caixa.loterias.feature.account.presentation.route.PersonalDataRoute
import br.gov.caixa.loterias.feature.account.presentation.route.RegistrationRoute
import br.gov.caixa.loterias.feature.account.presentation.screen.accountScreens
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountStatusViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel

fun NavGraphBuilder.accountGraph(
    appContainer: AccountNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    onAuthenticated: (String) -> Unit = { destination -> session.onAuthenticated(destination) },
) {
    accountStatusGraph(appContainer, navController, session)
    personalDataGraph(appContainer, navController, session)
    composable("account/register") {
        val registrationViewModel = viewModel {
            RegistrationViewModel(
                identityProvider = appContainer.currentUserIdentity,
                resolveZipCode = appContainer.resolveZipCode,
                registerAccount = appContainer.registerAccount,
                dispatchers = appContainer.dispatchers
            )
        }
        RegistrationRoute(
            viewModel = registrationViewModel,
            onBack = navController::popBackStack,
            onComplete = { navController.navigate("account/register-confirmation") },
            onTerms = { navController.navigate("support/terms") }
        )
    }
    composable(
        route = LoginNavigation.pattern,
        arguments = listOf(
            navArgument("authorize") {
                type = NavType.BoolType
                defaultValue = false
            },
            navArgument("silent") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { entry ->
        val notificationPermissionReady = appContainer.loginNotificationPermissionReady()
        val loginViewModel = viewModel {
            LoginViewModel(
                authentication = appContainer.authentication,
                dispatchers = appContainer.dispatchers,
                verifyAccess = appContainer.verifyPostLoginAccess
            )
        }
        LoginRoute(
            onSessionInvalidated = session.onLogout,
            networkAvailability = appContainer.loginNetworkAvailability,
            offlineScreen = { retry, loading -> appContainer.loginConnectionPresentation.Offline(retry, loading) },
            authorizeOnEnter = notificationPermissionReady && entry.arguments?.getBoolean("authorize") == true,
            silentAuthorizeOnEnter = notificationPermissionReady && entry.arguments?.getBoolean("silent") == true,
            onAuthorizationCancelled = {
                if (entry.arguments?.getBoolean("authorize") == true) {
                    val returnRoute = session.state.visitorReturnRoute
                    session.state.visitorReturnRoute = null
                    session.state.visitorReturnOwnerKey = null
                    navController.popBackStack()
                    if (session.identity == null && returnRoute != null &&
                        opensVisitorDestinationBeforeLogin(returnRoute) &&
                        navController.currentDestination?.route?.substringBefore('?') == returnRoute.substringBefore('?')
                    ) {
                        session.requestVisitorLogin(returnRoute)
                    }
                }
            },
            viewModel = loginViewModel,
            authentication = appContainer.authentication,
            versionName = appContainer.appVersionName,
            environmentSelectorVisible = appContainer.environmentSelectorVisible,
            environmentSelector = { enabled -> appContainer.LoginEnvironmentSelector(enabled) },
            onAuthenticated = onAuthenticated,
            onTryNow = session.onTryNow
        )
    }
}

private fun NavGraphBuilder.accountStatusGraph(
    appContainer: AccountNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
) {
    accountScreens { "" }.filter { it.route in accountStatusRoutes }.forEach { spec ->
        composable(spec.route) {
            val accountStatusViewModel = viewModel(key = "account-status-${spec.route}") {
                AccountStatusViewModel(spec.route, appContainer.dispatchers)
            }
            AccountRoute(
                viewModel = accountStatusViewModel,
                onBack = navController::popBackStack,
                onComplete = {
                    if (spec.route == "account/register-confirmation" &&
                        (session.state.accountCompletionRequired || session.state.visitorReturnRoute != null)
                    ) {
                        session.recheckAccountCompletion()
                    } else {
                        nextDestination(spec.route)?.let { navController.navigate(it) } ?: navController.popBackStack()
                    }
                },
                ssoAccountUrl = appContainer.ssoAccountUrl +
                    "?referrer=" + appContainer.ssoClientId +
                    "&referrer_uri=" + Uri.encode(appContainer.accountReferrerUri)
            )
        }
    }
}

private fun NavGraphBuilder.personalDataGraph(
    appContainer: AccountNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
) {
    composable("account/personal-data") {
        androidx.compose.runtime.key(session.privateEntryKey) {
            val personalDataViewModel = viewModel(key = "personal-data-${session.privateEntryKey}") {
                PersonalDataViewModel(
                    getProfile = appContainer.accountProfile,
                    resolveZipCode = appContainer.resolveZipCode,
                    updatePersonalData = appContainer.updatePersonalData,
                    dispatchers = appContainer.dispatchers,
                    recalculateAuthorizedLimit = appContainer.recalculateAuthorizedLimit,
                    deferInitialLoad = session.identity == null,
                    isSessionCurrent = session.isPrivateSessionCurrent
                )
            }
            PersonalDataRoute(
                viewModel = personalDataViewModel,
                onBack = navController::popBackStack,
                onComplete = { navController.navigate("account/personal-data-confirmation") }
            )
        }
    }
}
