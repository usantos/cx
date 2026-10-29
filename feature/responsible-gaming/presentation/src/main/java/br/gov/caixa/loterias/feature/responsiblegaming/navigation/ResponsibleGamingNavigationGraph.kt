package br.gov.caixa.loterias.feature.responsiblegaming.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.workflow.nextDestination
import br.gov.caixa.loterias.core.navigation.workflow.openResponsibleAssessment
import br.gov.caixa.loterias.core.navigation.workflow.requiresVisitorLoginOnSubmit
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.route.ResponsibleGamingRoute
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.responsibleGamingScreens
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.ResponsibleGamingViewModel

fun NavGraphBuilder.responsibleGamingGraph(
    appContainer: ResponsibleGamingNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions
) {
    responsibleGamingScreens { "" }.forEach { spec ->
        composable(spec.route) {
            androidx.compose.runtime.key(session.privateEntryKey) {
                val responsibleGamingViewModel = viewModel(key = "responsible-${spec.route}-${session.privateEntryKey}") {
                    ResponsibleGamingViewModel(
                        route = spec.route,
                        getQuestions = appContainer.selfAssessmentQuestions,
                        submitAssessment = appContainer.submitSelfAssessment,
                        getSocialTransfers = appContainer.socialTransfers,
                        suspendAccount = appContainer.suspendAccount,
                        dispatchers = appContainer.dispatchers,
                        serverTimeSource = appContainer.serverTimeSource,
                        autoLoad = spec.route != "responsible/form" || session.identity != null,
                        isSessionCurrent = if (spec.route == "responsible/form") {
                            session.isPrivateSessionCurrent
                        } else {
                            { true }
                        }
                    )
                }
                ResponsibleGamingRoute(
                    privateDataEnabled = spec.route != "responsible/form" || session.identity != null,
                    route = spec.route,
                    viewModel = responsibleGamingViewModel,
                    onBack = navController::popBackStack,
                    onOpenTerms = { navController.navigate("support/terms?tab=FAQ") },
                    onSubmitSuspension = {
                        if (requiresVisitorLoginOnSubmit(spec.route) && appContainer.currentUserIdentity.currentUser() == null) {
                            session.requestVisitorLogin(spec.route)
                        } else {
                            responsibleGamingViewModel.onAction(ResponsibleGamingAction.SubmitSuspension)
                        }
                    },
                    onComplete = { completeResponsibleDestination(spec.route, navController, session) }
                )
            }
        }
    }
    composable("responsible/start/required") {
        val responsibleGamingViewModel = viewModel(key = "responsible-start-required") {
            ResponsibleGamingViewModel(
                route = "responsible/start",
                getQuestions = appContainer.selfAssessmentQuestions,
                submitAssessment = appContainer.submitSelfAssessment,
                getSocialTransfers = appContainer.socialTransfers,
                suspendAccount = appContainer.suspendAccount,
                dispatchers = appContainer.dispatchers,
                serverTimeSource = appContainer.serverTimeSource,
                assessmentRequired = true
            )
        }
        ResponsibleGamingRoute(
            route = "responsible/start",
            viewModel = responsibleGamingViewModel,
            onBack = navController::popBackStack,
            onOpenTerms = { navController.navigate("support/terms?tab=FAQ") },
            onComplete = { navController.navigate("responsible/form") }
        )
    }
}

private fun completeResponsibleDestination(route: String, navController: NavHostController, session: SessionNavigationActions) {
    if (route == "responsible/suspension") {
        session.onLogout()
    } else if (route == "responsible/form" &&
        (session.state.accountCompletionRequired || session.state.visitorReturnRoute != null)
    ) {
        session.recheckAccountCompletion()
    } else {
        nextDestination(route)?.let { destination ->
            if (route == "responsible/start" && destination == "responsible/form") {
                openResponsibleAssessment(navController)
                if (session.identity == null) session.requestVisitorLogin(destination)
            } else {
                navController.navigate(destination)
            }
        } ?: navController.popBackStack()
    }
}
