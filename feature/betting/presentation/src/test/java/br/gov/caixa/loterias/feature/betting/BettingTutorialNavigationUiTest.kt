package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddComboToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContestsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependencies
import br.gov.caixa.loterias.feature.betting.navigation.bettingTutorialGraph
import br.gov.caixa.loterias.feature.betting.presentation.screen.bettingScreens
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class BettingTutorialNavigationUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private lateinit var navigation: AppNavigationState
    private val persisted = mutableListOf<Pair<BettingTutorial, Boolean>>()

    @Test fun poolCompletionTransfersSpecialModalityFilterAndRemovesTutorialFromStack() {
        show("betting/tutorial?modalityId=2&contestTypeId=2")
        compose.onNodeWithTag("pool-tutorial-skip").performClick()
        compose.onNodeWithTag("pool-tutorial-checkbox-row").performClick()
        compose.onNodeWithTag("pool-tutorial-primary").performClick()
        compose.onNodeWithText("Destino bolões").assertExists()
        compose.runOnIdle {
            assertEquals(
                MarketplaceFilter.forModality(2, 2),
                navigation.details.marketplaceFilter
            )
            assertEquals(navigation.details.marketplaceFilter, navigation.details.marketplaceClearFilter)
            assertNull(navigation.details.lastAppliedFilterRetailer)
            assertEquals("origin", nav.previousBackStackEntry?.destination?.route)
            assertEquals(listOf(BettingTutorial.POOL to true), persisted)
        }
    }

    @Test fun poolWithoutModalityCompletesWithEmptyFilterAndDoesNotPersistUncheckedPreference() {
        show("betting/tutorial")
        compose.onNodeWithTag("pool-tutorial-skip").performClick()
        compose.onNodeWithTag("pool-tutorial-primary").performClick()
        compose.onNodeWithText("Destino bolões").assertExists()
        compose.runOnIdle {
            assertEquals(
                MarketplaceFilter.empty(),
                navigation.details.marketplaceFilter
            )
            assertEquals(emptyList<Pair<BettingTutorial, Boolean>>(), persisted)
        }
    }

    @Test fun closingPoolIntroductionReturnsToOriginWithoutSelectingFilter() {
        show("betting/tutorial?modalityId=8&contestTypeId=1")
        compose.onNodeWithTag("pool-tutorial-close").performClick()
        compose.onNodeWithText("Origem").assertExists()
        compose.runOnIdle { assertEquals(emptyList<Pair<BettingTutorial, Boolean>>(), persisted) }
    }

    @Test fun superSevenCompletionUsesSimulationAndRemembersTutorialAutomatically() {
        show("betting/tutorial-super-seven")
        compose.onNodeWithText("Avançar").performClick()
        compose.onNodeWithText("Avançar").performClick()
        compose.onNodeWithText("Iniciar aposta").performClick()
        compose.onNodeWithText("Destino simulação").assertExists()
        compose.runOnIdle { assertEquals(listOf(BettingTutorial.SUPER_SETE to true), persisted) }
    }

    @Test fun superSevenSkipReturnsToOriginAndStillRemembersTutorial() {
        show("betting/tutorial-super-seven")
        compose.onNodeWithText("Pular").performClick()
        compose.onNodeWithText("Origem").assertExists()
        compose.runOnIdle { assertEquals(listOf(BettingTutorial.SUPER_SETE to true), persisted) }
    }

    private fun show(route: String) {
        compose.setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            nav = rememberNavController()
            navigation = rememberAppNavigationState(null)
            CaixaDSTheme {
                NavHost(nav, startDestination = "origin") {
                    composable("origin") { CaixaDSText("Origem") }
                    composable("betting/pools") { CaixaDSText("Destino bolões") }
                    composable("betting/simulation") { CaixaDSText("Destino simulação") }
                    bettingTutorialGraph(dependencies, nav, navigation, bettingScreens(context::getString))
                }
            }
        }
        compose.runOnIdle { nav.navigate(route) }
        compose.waitForIdle()
    }

    private val dependencies = object : BettingNavigationDependencies {
        override val bettingCheckpoints = testCheckpointFactory()
        override val addBetToCart: AddBetToCartUseCase get() = error(
            "Tutorial graph must not access addBetToCart"
        )
        override val addComboToCart: AddComboToCartUseCase get() = error(
            "Tutorial graph must not access addComboToCart"
        )
        override val addFavoriteBetToCart: AddFavoriteBetToCartUseCase get() = error(
            "Tutorial graph must not access addFavoriteBetToCart"
        )
        override val betCombos: GetBetCombosUseCase get() = error(
            "Tutorial graph must not access betCombos"
        )
        override val bettingSpecialContests: GetBettingSpecialContestsUseCase get() = error(
            "Tutorial graph must not access bettingSpecialContests"
        )
        override val bettingParameters: GetBettingParametersUseCase get() = error(
            "Tutorial graph must not access bettingParameters"
        )
        override val currentCart: GetCurrentCartUseCase get() = error(
            "Tutorial graph must not access currentCart"
        )
        override val deleteFavoriteBet: DeleteFavoriteBetUseCase get() = error(
            "Tutorial graph must not access deleteFavoriteBet"
        )
        override val dispatchers: CoroutineDispatchers get() = error(
            "Tutorial graph must not access dispatchers"
        )
        override val favoriteBets: GetFavoriteBetsUseCase get() = error(
            "Tutorial graph must not access favoriteBets"
        )
        override val generateRapidBets: GenerateRapidBetsUseCase get() = error(
            "Tutorial graph must not access generateRapidBets"
        )
        override val localClock: java.time.Clock get() = error("Tutorial graph must not access localClock")
        override val lotteryModalities: GetLotteryModalitiesUseCase get() = error(
            "Tutorial graph must not access lotteryModalities"
        )
        override val networkAvailability: NetworkAvailability get() = error(
            "Tutorial graph must not access networkAvailability"
        )
        override val prepareHomeBettingAction: PrepareHomeBettingActionUseCase get() = error(
            "Tutorial graph must not access prepareHomeBettingAction"
        )
        override val rapidBetConfiguration: GetRapidBetConfigurationUseCase get() = error(
            "Tutorial graph must not access rapidBetConfiguration"
        )
        override val removeCartItem: RemoveCartItemUseCase get() = error(
            "Tutorial graph must not access removeCartItem"
        )
        override val saveFavoriteBet: SaveFavoriteBetUseCase get() = error(
            "Tutorial graph must not access saveFavoriteBet"
        )
        override val saveRapidBetConfiguration: SaveRapidBetConfigurationUseCase get() = error(
            "Tutorial graph must not access saveRapidBetConfiguration"
        )
        override val bettingTutorialDismissed: GetBettingTutorialDismissedUseCase get() = error(
            "Tutorial graph must not access onboarding"
        )
        override val setBettingTutorialDismissed: SetBettingTutorialDismissedUseCase =
            SetBettingTutorialDismissedUseCase { kind, dismissed ->
                persisted.add(kind to dismissed)
            }
        override val shakeEnabled: GetShakeEnabledUseCase get() = error(
            "Tutorial graph must not access shakeSettings"
        )
        override val validateCart: ValidateCartUseCase get() = error(
            "Tutorial graph must not access validateCart"
        )
        override val validateFavoriteBet: ValidateFavoriteBetUseCase get() = error(
            "Tutorial graph must not access validateFavoriteBet"
        )
        override val verifyAccountAccess: VerifyAccountAccessUseCase get() = error(
            "Tutorial graph must not access verifyAccountAccess"
        )
    }
}
