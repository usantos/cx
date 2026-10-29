// Literais numéricos descrevem seleções de loteria, identificadores e limites das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
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
import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.betting.navigation.bettingGraph
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import java.math.BigDecimal
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class BettingNavigationIntegrationUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private lateinit var navigation: AppNavigationState
    private var tutorialDismissed = false
    private var authenticated = false
    private var entryDecision: AppResult<HomeBettingDecision> = AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_LIST))
    private val savedConfigurations = mutableListOf<RapidBetConfiguration>()
    private val comboRequests = mutableListOf<Triple<Int, Int, Boolean>>()
    private val modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val game = LotteryGameParameters(modality, 123, "22/10/2026", 60, 6, 6, 10, emptyList(), listOf(BetPrice(6, 0, BigDecimal.TEN)))
    private val parameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game))
    private val cart = ShoppingCart(1, BigDecimal.TEN, emptyList(), totalBetCount = 1)
    private val combo = BetCombo(
        5,
        "Combo da semana",
        "Apostas Mega-Sena",
        false,
        BigDecimal.TEN,
        listOf(BetComboEntry(modality, "NORMAL", 1, 0, 6, 0, 123))
    )

    @Test fun homeCatalogOpensSelectedSimulationAndClearsPreparedParametersAfterEntry() {
        show("betting/home")
        compose.onNodeWithText("Mega-Sena").performClick()
        val cartButton = hasTestTag("betting-cart-footer") and hasContentDescription("Carrinho de apostas, 1 itens")
        // A entrada precisa concluir o resume e a leitura assíncrona do carrinho antes de expor a contagem.
        compose.waitUntil(timeoutMillis = CART_READY_TIMEOUT_MILLIS) {
            compose.onAllNodes(cartButton).fetchSemanticsNodes().size == 1
        }
        compose.onNode(cartButton).assertExists().assertHasClickAction()
        compose.runOnIdle {
            assertTrue(nav.currentDestination?.route?.startsWith("betting/simulation/") == true)
            assertEquals(2, nav.currentBackStackEntry?.arguments?.getInt("modalityId"))
            assertEquals(null, navigation.preparedBettingParameters)
            assertEquals(null, navigation.details.betToEdit)
        }
    }

    @Test fun modalitiesAliasReturnsToOriginThroughToolbar() {
        show("betting/modalities")
        compose.onNodeWithText("Mega-Sena").assertExists()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Origem").assertExists()
    }

    @Test fun defaultSimulationHelpOpensFaq() {
        show("betting/simulation")
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.onNodeWithText("FAQ").assertExists()
    }

    @Test fun selectedSimulationCartOpensCurrentCart() {
        show("betting/simulation/2")
        val cartButton = hasTestTag("betting-cart-footer") and hasContentDescription("Carrinho de apostas, 1 itens")
        compose.waitUntil(timeoutMillis = CART_READY_TIMEOUT_MILLIS) {
            compose.onAllNodes(cartButton).fetchSemanticsNodes().size == 1
        }
        compose.onNode(cartButton).assertExists().assertHasClickAction().performClick()
        compose.onNodeWithText("Destino carrinho").assertExists()
    }

    @Test fun selectedSimulationBackReturnsToCatalogOrigin() {
        show("betting/simulation/2")
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Origem").assertExists()
    }

    @Test fun comboSelectionOpensDetailWithCorrectIdentifier() {
        show("betting/combos")
        compose.onNodeWithText("Combo da semana").performClick()
        compose.onNodeWithText("Quantidade de Apostas").assertExists()
        compose.runOnIdle { assertEquals(5, nav.currentBackStackEntry?.arguments?.getInt("comboId")) }
    }

    @Test fun addingComboTransfersComputedAmountAndMinimumToCartConfirmation() {
        show("betting/combo-detail/5")
        compose.onNodeWithContentDescription("Adicionar apostas").performClick()
        compose.onNodeWithText("Adicionar\nao carrinho de apostas").assertIsDisplayed().performClick()
        compose.onNodeWithText("Aposta incluída").assertExists()
        compose.runOnIdle {
            assertEquals(listOf(Triple(5, 2, true)), comboRequests)
            assertEquals("20", nav.currentBackStackEntry?.arguments?.getString("amount"))
            assertEquals("30", nav.currentBackStackEntry?.arguments?.getString("minimum"))
            assertEquals("COMBO", nav.currentBackStackEntry?.arguments?.getString("modality"))
        }
    }

    @Test fun combosCartOpensCurrentCart() {
        show("betting/combos")
        compose.onNodeWithText("Valor atual do carrinho", substring = true).performClick()
        compose.onNodeWithText("Destino carrinho").assertExists()
    }

    @Test fun visitorFavoriteHelpUsesTermsWithoutFetchingPrivateFavorites() {
        show("betting/favorites")
        compose.onNodeWithText("Apostas favoritas").assertExists()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.onNodeWithText("Termos").assertExists()
    }

    @Test fun rapidListConfigurationEntersWelcomeBeforeTutorialDismissal() {
        show("betting/rapid-list")
        compose.onNodeWithText("Nenhuma aposta foi gerada.").assertExists()
        compose.onNodeWithContentDescription("Configuração").performClick()
        compose.onNodeWithText("Bem-vindo ao").assertExists()
        compose.runOnIdle { assertEquals("betting/rapid-config", nav.currentDestination?.route) }
    }

    @Test fun rapidAliasForNewVisitorShowsWelcomeAndBackReturnsToOrigin() {
        show("betting/rapid")
        compose.onNodeWithText("Bem-vindo ao").assertExists()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Origem").assertExists()
    }

    @Test fun rapidAliasAfterTutorialDismissalUsesList() {
        tutorialDismissed = true
        show("betting/rapid")
        compose.onNodeWithText("Nenhuma aposta foi gerada.").assertExists()
        compose.onNodeWithContentDescription("Configuração").assertExists()
    }

    @Test fun authenticatedRapidFailureAcknowledgmentReturnsToOrigin() {
        authenticated = true
        entryDecision = AppResult.Failure(AppError.Network)
        show("betting/rapid")
        compose.onNodeWithText("Não foi possível carregar o Rapidão. Verifique sua internet.").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Origem").assertExists()
    }

    @Test fun rapidAccountCompletionRemovesPrivateEntryAndRemembersReturnRoute() {
        authenticated = true
        entryDecision = AppResult.Success(HomeBettingDecision.CompleteAccount(PostLoginDestination.REGISTRATION))
        show("betting/rapid")
        compose.onNodeWithText("Completar cadastro").assertExists()
        compose.runOnIdle {
            assertEquals("betting/rapid", session.state.visitorReturnRoute)
            assertEquals("origin", nav.previousBackStackEntry?.destination?.route)
        }
    }

    @Test fun existingConfigurationSaveReturnsToPriorScreenAndPersistsExactConfiguration() {
        authenticated = true
        tutorialDismissed = true
        show("betting/rapid-config")
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Origem").assertExists()
        compose.runOnIdle {
            assertEquals(1, savedConfigurations.size)
            assertEquals(listOf(modality), savedConfigurations.single().modalities)
            assertEquals(BigDecimal.TEN, savedConfigurations.single().minimumAmount)
            assertTrue(tutorialDismissed)
        }
    }

    @Test fun rapidEntryConfigurationSaveReplacesEntryWithGeneratedList() {
        authenticated = true
        tutorialDismissed = true
        entryDecision = AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_CONFIGURATION))
        show("betting/rapid")
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Nenhuma aposta foi gerada.").assertExists()
        compose.runOnIdle {
            assertEquals(1, savedConfigurations.size)
            assertEquals("betting/rapid-list", nav.currentDestination?.route)
            assertEquals("origin", nav.previousBackStackEntry?.destination?.route)
        }
    }

    @Test fun rapidConfigurationPagerKeepsPrizeAndNumberEditsUntilSaved() {
        authenticated = true
        show("betting/rapid-config")
        compose.onNodeWithText("Bem-vindo ao").assertExists()
        compose.onNodeWithContentDescription("Etapa 2 de 7").performClick()
        compose.onNodeWithText("Prêmio mínimo").assertExists()
        compose.onNodeWithText("20 Mi").performClick()
        compose.onNodeWithContentDescription("Etapa 3 de 7").performClick()
        compose.onNodeWithText("Valor máximo da aposta").assertExists()
        compose.onNodeWithContentDescription("Etapa 4 de 7").performClick()
        compose.onNodeWithText("Selecione as opções que você quer contemplar no Rapidão.").assertExists()
        compose.onNodeWithContentDescription("Etapa 5 de 7").performClick()
        compose.onNodeWithText("Números obrigatórios").assertExists()
        compose.onNodeWithText("01").performClick()
        compose.onNodeWithContentDescription("Etapa 6 de 7").performClick()
        compose.onNodeWithText("Números proibidos").assertExists()
        compose.onNodeWithText("02").performClick()
        compose.onNodeWithContentDescription("Etapa 7 de 7").performClick()
        compose.onNodeWithContentDescription("Etapa 7 de 7").assertIsSelected()
        compose.onNodeWithText("Resumo").assertExists()
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Nenhuma aposta foi gerada.").assertExists()
        compose.runOnIdle {
            assertEquals(setOf(1), savedConfigurations.single().requiredNumbers)
            assertEquals(setOf(2), savedConfigurations.single().excludedNumbers)
            assertEquals(BigDecimal("20000000"), savedConfigurations.single().minimumPrize)
            assertTrue(tutorialDismissed)
        }
    }

    private fun show(route: String) {
        compose.setContent {
            nav = rememberNavController()
            navigation = rememberAppNavigationState(null)
            CaixaDSTheme {
                NavHost(nav, startDestination = "origin") {
                    composable("origin") { CaixaDSText("Origem") }
                    composable("account/register") { CaixaDSText("Completar cadastro") }
                    composable("cart/current") { CaixaDSText("Destino carrinho") }
                    composable("support/terms?tab={tab}") { CaixaDSText("FAQ") }
                    composable("support/terms") { CaixaDSText("Termos") }
                    composable("cart/included?modality={modality}&amount={amount}&minimum={minimum}") { CaixaDSText("Aposta incluída") }
                    bettingGraph(adapter, nav, navigation, session)
                }
            }
        }
        compose.runOnIdle { nav.navigate(route) }
        compose.waitForIdle()
    }

    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity get() = if (authenticated) AuthenticatedSessionIdentity("b".repeat(64)) else null
        override val privateEntryKey = "visitor"
        override val isPrivateSessionCurrent: () -> Boolean = { true }
        override val onLogout: () -> Unit = {}
        override val requestVisitorLogin: (String) -> Unit = { error("No private action was requested") }
        override val recheckAccountCompletion: () -> Unit = {}
        override val onAuthenticated: (String) -> Unit = {}
        override val onTryNow: () -> Unit = {}
    }

    private val adapter by lazy {
        BettingNavigationDependenciesAdapter(
            recoveryMessages = dependencies.recoveryMessages,
            serverTime = dependencies.serverTime,
            bettingCheckpoints = testCheckpointFactory(),
            addBetToCart = dependencies.addBetToCart,
            addComboToCart = dependencies.addComboToCart,
            addFavoriteBetToCart = dependencies.addFavoriteBetToCart,
            betCombos = dependencies.betCombos,
            bettingSpecialContests = dependencies.bettingSpecialContests,
            bettingParameters = dependencies.bettingParameters,
            currentCart = dependencies.currentCart,
            deleteFavoriteBet = dependencies.deleteFavoriteBet,
            dispatchers = dependencies.dispatchers,
            favoriteBets = dependencies.favoriteBets,
            generateRapidBets = dependencies.generateRapidBets,
            localClock = dependencies.localClock,
            lotteryModalities = dependencies.lotteryModalities,
            networkAvailability = dependencies.networkAvailability,
            prepareHomeBettingAction = dependencies.prepareHomeBettingAction,
            rapidBetConfiguration = dependencies.rapidBetConfiguration,
            removeCartItem = dependencies.removeCartItem,
            saveFavoriteBet = dependencies.saveFavoriteBet,
            saveRapidBetConfiguration = dependencies.saveRapidBetConfiguration,
            bettingTutorialDismissed = dependencies.bettingTutorialDismissed,
            setBettingTutorialDismissed = dependencies.setBettingTutorialDismissed,
            shakeEnabled = dependencies.shakeEnabled,
            validateCart = dependencies.validateCart,
            validateFavoriteBet = dependencies.validateFavoriteBet,
            verifyAccountAccess = dependencies.verifyAccountAccess
        )
    }

    @Test fun navigationAdapterPreservesTransactionAndSessionDependencyIdentity() {
        assertSame(dependencies.addBetToCart, adapter.addBetToCart)
        assertSame(dependencies.bettingParameters, adapter.bettingParameters)
        assertSame(dependencies.currentCart, adapter.currentCart)
        assertSame(dependencies.dispatchers, adapter.dispatchers)
        assertSame(dependencies.saveRapidBetConfiguration, adapter.saveRapidBetConfiguration)
        assertSame(dependencies.shakeEnabled, adapter.shakeEnabled)
    }

    private val dependencies = object : BettingNavigationDependencies {
        override val bettingCheckpoints = testCheckpointFactory()
        override val addBetToCart: AddBetToCartUseCase = AddBetToCartUseCase { AppResult.Success(cart) }
        override val addComboToCart: AddComboToCartUseCase =
            AddComboToCartUseCase { id, quantity, surprise ->
                comboRequests.add(Triple(id, quantity, surprise))
                AppResult.Success(cart)
            }
        override val addFavoriteBetToCart: AddFavoriteBetToCartUseCase = AddFavoriteBetToCartUseCase { _, _ -> AppResult.Success(cart) }
        override val betCombos: GetBetCombosUseCase = GetBetCombosUseCase { AppResult.Success(listOf(combo)) }
        override val bettingSpecialContests: GetBettingSpecialContestsUseCase =
            GetBettingSpecialContestsUseCase { AppResult.Failure(AppError.Network) }
        override val bettingParameters: GetBettingParametersUseCase = GetBettingParametersUseCase { AppResult.Success(parameters) }
        override val currentCart: GetCurrentCartUseCase = GetCurrentCartUseCase { AppResult.Success(cart) }
        override val deleteFavoriteBet: DeleteFavoriteBetUseCase =
            DeleteFavoriteBetUseCase { error("Unexpected service: deleteFavoriteBet") }
        override val dispatchers: CoroutineDispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        override val favoriteBets: GetFavoriteBetsUseCase = GetFavoriteBetsUseCase { AppResult.Success(emptyList()) }
        override val generateRapidBets: GenerateRapidBetsUseCase =
            GenerateRapidBetsUseCase { ServiceResult.Success(cart) }
        override val localClock: Clock = Clock.systemUTC()
        override val lotteryModalities: GetLotteryModalitiesUseCase = GetLotteryModalitiesUseCase { AppResult.Success(listOf(modality)) }
        override val networkAvailability: NetworkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED }
        override val prepareHomeBettingAction: PrepareHomeBettingActionUseCase =
            PrepareHomeBettingActionUseCase { entryDecision }
        override val rapidBetConfiguration: GetRapidBetConfigurationUseCase =
            GetRapidBetConfigurationUseCase {
                ServiceResult.Success(
                    RapidBetConfiguration(BigDecimal.TEN, BigDecimal("100"), BigDecimal.ZERO, emptySet(), emptySet(), listOf(modality))
                )
            }
        override val removeCartItem: RemoveCartItemUseCase = RemoveCartItemUseCase { error("Unexpected service: removeCartItem") }
        override val saveFavoriteBet: SaveFavoriteBetUseCase = SaveFavoriteBetUseCase { AppResult.Success(Unit) }
        override val saveRapidBetConfiguration: SaveRapidBetConfigurationUseCase =
            SaveRapidBetConfigurationUseCase { configuration ->
                savedConfigurations.add(configuration)
                ServiceResult.Success(configuration)
            }
        override val bettingTutorialDismissed = GetBettingTutorialDismissedUseCase { tutorialDismissed }
        override val setBettingTutorialDismissed: SetBettingTutorialDismissedUseCase =
            SetBettingTutorialDismissedUseCase { _, dismissed -> tutorialDismissed = dismissed }
        override val shakeEnabled = GetShakeEnabledUseCase { false }
        override val validateCart: ValidateCartUseCase = ValidateCartUseCase { error("Unexpected service: validateCart") }
        override val validateFavoriteBet: ValidateFavoriteBetUseCase = ValidateFavoriteBetUseCase { AppResult.Success(Unit) }
        override val verifyAccountAccess: VerifyAccountAccessUseCase =
            VerifyAccountAccessUseCase { AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal("500"))) }
    }
}

private const val CART_READY_TIMEOUT_MILLIS = 5_000L
