package br.gov.caixa.loterias.apostas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.apostas.navigation.transition.AppScreenNavHost
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies
import br.gov.caixa.loterias.apostas.navigation.action.appNavigationActions
import br.gov.caixa.loterias.apostas.navigation.recovery.rememberRecoveryNavController
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.session.rememberAppSessionCoordinator
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.presentation.state.HomeSection
import br.gov.caixa.loterias.feature.account.navigation.accountGraph
import br.gov.caixa.loterias.feature.account.presentation.screen.accountScreens
import br.gov.caixa.loterias.feature.betting.navigation.bettingGraph
import br.gov.caixa.loterias.feature.betting.navigation.bettingTutorialGraph
import br.gov.caixa.loterias.feature.betting.presentation.screen.bettingScreens
import br.gov.caixa.loterias.feature.cart.navigation.cartGraph
import br.gov.caixa.loterias.feature.cart.presentation.screen.cartScreens
import br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.homeGraph
import br.gov.caixa.loterias.feature.marketplace.navigation.marketplaceGraph
import br.gov.caixa.loterias.feature.notifications.navigation.notificationsGraph
import br.gov.caixa.loterias.feature.notifications.presentation.screen.notificationScreens
import br.gov.caixa.loterias.feature.onboarding.navigation.onboardingGraph
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.onboardingScreens
import br.gov.caixa.loterias.feature.payment.navigation.paymentGraph
import br.gov.caixa.loterias.feature.payment.presentation.screen.paymentScreens
import br.gov.caixa.loterias.feature.responsiblegaming.navigation.responsibleGamingGraph
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.responsibleGamingScreens
import br.gov.caixa.loterias.feature.results.navigation.resultsGraph
import br.gov.caixa.loterias.feature.results.presentation.screen.resultScreens
import br.gov.caixa.loterias.feature.retail.navigation.retailGraph
import br.gov.caixa.loterias.feature.retail.presentation.screen.retailScreens
import br.gov.caixa.loterias.feature.support.navigation.supportGraph
import br.gov.caixa.loterias.feature.support.presentation.screen.supportScreens
import br.gov.caixa.loterias.feature.tickets.navigation.ticketsGraph
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ticketScreens

@Composable
fun AppNavHost(
    featureDependencies: AppFeatureGraphDependencies,
    sessionDependencies: AppSessionDependencies,
    startDestination: String = "account/login",
    onForegroundSessionRestart: () -> Unit = {},
) {
    androidx.compose.runtime.CompositionLocalProvider(
        br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration provides
            br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration(
                BuildConfig.HOME_V2,
                BuildConfig.VERSION_NAME,
                featureDependencies.home.serverTime
            )
    ) {
        val navController = rememberRecoveryNavController()
        val identity by sessionDependencies.account.sessionIdentityChanges.collectAsStateWithLifecycle(
            initialValue = sessionDependencies.account.authenticatedSessionIdentity.captureIdentity()
        )
        val navigation = rememberAppNavigationState(identity?.ownerKey)
        val session = rememberAppSessionCoordinator(sessionDependencies, navController, navigation, identity, onForegroundSessionRestart)
        var pendingLogin by remember { mutableStateOf<PostLoginTutorialRequest?>(null) }
        val actions = appNavigationActions(sessionDependencies, navController, navigation, session)
        val resources = LocalResources.current
        val configuration = LocalConfiguration.current
        val sections = remember(resources, configuration) {
            listOf(
                HomeSection(resources.getString(R.string.workflow_section_onboarding), onboardingScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_account), accountScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_betting), bettingScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_cart), cartScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_payment), paymentScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_ticket), ticketScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_result), resultScreens(resources::getString)),
                HomeSection(
                    resources.getString(R.string.workflow_section_responsiblegaming),
                    responsibleGamingScreens(resources::getString)
                ),
                HomeSection(resources.getString(R.string.workflow_section_notification), notificationScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_retail), retailScreens(resources::getString)),
                HomeSection(resources.getString(R.string.workflow_section_support), supportScreens(resources::getString))
            )
        }
        val allScreens = remember(sections) { sections.flatMap(HomeSection::screens) }

        AppScreenNavHost(navController = navController, startDestination = startDestination) {
            homeGraph(featureDependencies.home, navController, navigation, session, actions)
            bettingTutorialGraph(featureDependencies.betting, navController, navigation, allScreens)
            onboardingGraph(featureDependencies.onboarding, navController)
            accountGraph(featureDependencies.account, navController, session, onAuthenticated = { destination ->
                sessionDependencies.account.authenticatedSessionIdentity.captureIdentity()?.let { authenticated ->
                    pendingLogin = PostLoginTutorialRequest(destination, authenticated)
                }
            })
            notificationsGraph(featureDependencies.notifications, navController)
            responsibleGamingGraph(featureDependencies.responsibleGaming, navController, session)
            bettingGraph(featureDependencies.betting, navController, navigation, session)
            paymentGraph(featureDependencies.payment, navController, session, actions)
            cartGraph(featureDependencies.cart, navController, navigation, session)
            ticketsGraph(featureDependencies.tickets, navController, navigation, session, actions)
            retailGraph(featureDependencies.retail, navController, session, actions)
            resultsGraph(featureDependencies.results, navController, navigation, actions)
            marketplaceGraph(featureDependencies.marketplace, navController, navigation, session)
            supportGraph(featureDependencies.support, navController, session)
        }
        br.gov.caixa.loterias.apostas.navigation.shortcuts.ResultShortcutNavigation(navController)
        pendingLogin?.let { request ->
            PostLoginShakeTutorial(
                request = request,
                currentIdentity = identity,
                identityProvider = sessionDependencies.account.authenticatedSessionIdentity,
                preferences = featureDependencies.home.homePreferences,
                dispatchers = featureDependencies.home.dispatchers,
                onReady = { destination ->
                    pendingLogin = null
                    session.onAuthenticated(destination)
                },
                onCancelled = { pendingLogin = null }
            )
        }
        br.gov.caixa.loterias.feature.account.presentation.route.LogoutOverlay(
            session.logoutViewModel,
            sessionDependencies.account.endSession
        )
    }
}
