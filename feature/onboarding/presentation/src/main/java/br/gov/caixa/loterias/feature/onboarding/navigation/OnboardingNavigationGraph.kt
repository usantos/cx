package br.gov.caixa.loterias.feature.onboarding.navigation

import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.navigation.workflow.nextDestination
import br.gov.caixa.loterias.feature.onboarding.presentation.route.OnboardingRoute
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.onboardingScreens
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel

fun NavGraphBuilder.onboardingGraph(appContainer: OnboardingNavigationDependencies, navController: NavHostController) {
    onboardingScreens { "" }.forEach { spec ->
        composable(spec.route) {
            val uriHandler = LocalUriHandler.current
            if (spec.route == "onboarding/update" || spec.route == "onboarding/unavailable") {
                OnboardingRecoveryNavigationDestination(spec.route, appContainer, navController)
                return@composable
            }
            val onboardingViewModel = viewModel(key = "onboarding-${spec.route}") {
                OnboardingViewModel(
                    spec.route,
                    appContainer.completeIntroduction,
                    appContainer.setBettingTutorialDismissed
                )
            }
            OnboardingRoute(
                viewModel = onboardingViewModel,
                onBack = {
                    if (spec.route == "onboarding/age") {
                        // Uma nova entrada na introdução restaura tanto a primeira página quanto a pergunta sobre idade.
                        val previousIntroduction = navController.previousBackStackEntry
                            ?.destination?.route == "onboarding/introduction"
                        navController.navigate("onboarding/introduction") {
                            popUpTo(if (previousIntroduction) "onboarding/introduction" else "onboarding/age") {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                onComplete = {
                    nextDestination(spec.route)?.let { navController.navigate(it) }
                        ?: navController.popBackStack()
                },
                onUnderage = { navController.navigate("onboarding/age") },
                onResponsibleGaming = {
                    uriHandler.openUri("https://www.caixa.gov.br/jogo-responsavel/Paginas/default.aspx")
                },
                onStartBettingTutorial = { navController.navigate("betting/tutorial-plus") },
                onSkipBettingTutorial = { navController.navigate("betting/simulation") }
            )
        }
    }
}
