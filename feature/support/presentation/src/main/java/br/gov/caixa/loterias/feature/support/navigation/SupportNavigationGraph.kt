package br.gov.caixa.loterias.feature.support.navigation

import br.gov.caixa.loterias.feature.support.presentation.formatting.localizedMessage as messageText

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.feature.support.presentation.route.SupportRoute
import br.gov.caixa.loterias.feature.support.presentation.screen.supportScreens
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import br.gov.caixa.loterias.feature.support.presentation.viewmodel.SupportViewModel

fun NavGraphBuilder.supportGraph(
    appContainer: SupportNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
) {
    gameHelpGraph(appContainer, navController)
    supportTermsGraph(appContainer, navController, session)
}

private fun NavGraphBuilder.gameHelpGraph(appContainer: SupportNavigationDependencies, navController: NavHostController) {
    composable(
        route = "support/game-help?modalityId={modalityId}&modalityName={modalityName}",
        arguments = listOf(
            navArgument("modalityId") {
                type = NavType.IntType
                defaultValue = 2
            },
            navArgument("modalityName") {
                type = NavType.StringType
                defaultValue = "Mega-Sena"
            }
        )
    ) { entry ->
        val modalityId = entry.arguments?.getInt("modalityId") ?: 2
        val modalityName = entry.arguments?.getString("modalityName").orEmpty().ifBlank { "Mega-Sena" }
        val supportViewModel = viewModel(key = "support-game-help-$modalityId-$modalityName") {
            SupportViewModel(
                getBettingParameters = appContainer.bettingParameters,
                getSections = appContainer.supportSections,
                getQuestions = appContainer.supportQuestions,
                getTerms = appContainer.terms,
                acceptTermsUseCase = appContainer.acceptTerms,
                privacyHtml = messageText("h2_politica_de_privacidade_h2_cb0073"),
                dispatchers = appContainer.dispatchers,
                initialState = SupportUiState(
                    mode = SupportMode.GAME_HELP,
                    gameHelpModalityId = modalityId,
                    gameHelpModalityName = modalityName
                )
            )
        }
        SupportRoute(
            viewModel = supportViewModel,
            onBack = navController::popBackStack,
            onAcceptTerms = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.supportTermsGraph(
    appContainer: SupportNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
) {
    supportScreens { "" }.filterNot { it.route == "support/game-help" }.forEach { spec ->
        val routePattern = if (spec.route == "support/terms") "support/terms?tab={tab}" else spec.route
        composable(
            routePattern,
            arguments = if (spec.route == "support/terms") {
                listOf(
                    navArgument("tab") {
                        type = NavType.StringType
                        defaultValue = "TERMS"
                    }
                )
            } else {
                emptyList()
            }
        ) { supportEntry ->
            val initialState = when (spec.route) {
                "support/terms-view" -> SupportUiState(
                    mode = SupportMode.TERMS_READ_ONLY,
                    selectedTab = SupportTab.TERMS
                )

                "support/terms" -> SupportUiState(
                    selectedTab = if (supportEntry.arguments?.getString("tab") == "FAQ") SupportTab.FAQ else SupportTab.TERMS,
                    showTermsTabs = true
                )

                "support/terms-acceptance" -> SupportUiState(
                    selectedTab = SupportTab.TERMS,
                    acceptanceRequired = true
                )

                else -> SupportUiState(selectedTab = SupportTab.FAQ)
            }
            val supportViewModel = viewModel(key = "support-${spec.route}-${initialState.selectedTab}") {
                SupportViewModel(
                    getSections = appContainer.supportSections,
                    getQuestions = appContainer.supportQuestions,
                    getTerms = appContainer.terms,
                    acceptTermsUseCase = appContainer.acceptTerms,
                    privacyHtml = messageText("h2_politica_de_privacidade_h2_cb0073"),
                    dispatchers = appContainer.dispatchers,
                    initialState = initialState
                )
            }
            SupportRoute(
                viewModel = supportViewModel,
                onBack = navController::popBackStack,
                onAcceptTerms = {
                    if (spec.route == "support/terms-acceptance") session.recheckAccountCompletion() else navController.popBackStack()
                }
            )
        }
    }
}
