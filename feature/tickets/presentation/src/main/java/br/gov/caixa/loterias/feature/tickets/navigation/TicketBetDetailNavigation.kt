package br.gov.caixa.loterias.feature.tickets.navigation

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.activeCartSummary
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleAdmission
import br.gov.caixa.loterias.core.presentation.delivery.rememberFeatureWritePermission
import br.gov.caixa.loterias.feature.tickets.presentation.route.BetDetailDataRoute

fun NavGraphBuilder.ticketBetDetailNavigation(
    appContainer: TicketsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    actions: AppNavigationActions,
    session: SessionNavigationActions? = null,
) {
    composable(
        route = "tickets/bet-detail?original={original}&resgate={resgate}&variant={variant}",
        arguments = listOf(
            navArgument("original") {
                type = NavType.BoolType
                defaultValue = false
            },
            navArgument("resgate") {
                type = NavType.BoolType
                defaultValue = false
            },
            navArgument("variant") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) { entry ->
        FeatureModuleAdmission(
            AppModule.TICKETS,
            { navController.popBackStack() },
            access = if (navigation.details.selectedBet?.let { session?.purchaseRecovery?.permitsSelectedBet(it.id) } ==
                true
            ) {
                FeatureEntryAccess.CONTINUATION
            } else {
                FeatureEntryAccess.OPTIONAL
            }
        ) {
            val canStartTicketsOperation = rememberFeatureWritePermission(AppModule.TICKETS)
            val original = entry.arguments?.getBoolean("original") == true
            val redeemed = entry.arguments?.getBoolean("resgate") == true
            val variant = entry.arguments?.getString("variant").orEmpty()
            val bet = navigation.details.selectedBet ?: if (appContainer.mockBackendEnabled) {
                appContainer.visualConfirmedBet(variant, original, redeemed)
            } else {
                null
            }
            if (bet == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@FeatureModuleAdmission
            }
            val detailViewModel = viewModel(key = "bet-detail-${bet.id}-${session?.privateEntryKey.orEmpty()}") {
                createTicketBetDetailViewModel(bet, appContainer, canStartTicketsOperation, appContainer.confirmedBetResults)
            }
            val betDetailCart = activeCartSummary(appContainer.currentCart, 0)
            BetDetailDataRoute(
                viewModel = detailViewModel,
                cartTotal = betDetailCart?.total ?: java.math.BigDecimal.ZERO,
                cartBetCount = betDetailCart?.betCount ?: 0,
                onBack = navController::popBackStack,
                onServiceRedirect = actions.onTicketServiceRedirect,
                onViewResult = { recoveredBet ->
                    navigation.details.selectedBet = recoveredBet
                    navController.navigate("results/confirmed-bet")
                },
                onRedeemPrize = if (bet.id > 0) {
                    (
                        { recoveredBet ->
                            navController.navigate("retail/prize/${recoveredBet.id}")
                        }
                        )
                } else {
                    null
                },
                onHelp = {
                    navController.navigate(
                        "support/game-help?modalityId=${bet.modalityId}&modalityName=${Uri.encode(bet.modality)}"
                    )
                },
                onEdit = { recoveredBet ->
                    navigation.details.betToEdit = recoveredBet
                    navController.navigate("betting/simulation/${recoveredBet.modalityId}")
                },
                onCart = { navController.navigate("cart/current") }
            )
        }
    }
}
