// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependencies
import br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.tickets.navigation.ticketsGraph
import java.math.BigDecimal
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class TicketsNavigationIntegrationUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private lateinit var navigation: AppNavigationState
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val bet =
        ConfirmedBet(
            9, "Mega-Sena", 123, "22/10/2026", "Premiada",
            listOf(
                1,
                2,
                3,
                4,
                5,
                6
            ),
            prize = BigDecimal.TEN, modalityId = 2, statusId = 100
        )
    private val purchase = Purchase(77, "22/10/2026", "Efetivada", "Pix", BigDecimal.TEN, statusId = 6)
    private val cart = ShoppingCart(1, BigDecimal.TEN, emptyList(), totalBetCount = 1)
    private val game =
        LotteryGameParameters(
            LotteryModality(
                2,
                "Mega-Sena",
                "MEGA_SENA"
            ),
            124, "23/10/2026", 60, 6, 6, 10, emptyList(), listOf(BetPrice(6, 0, BigDecimal.TEN))
        )
    private val addedDrafts = mutableListOf<BetDraft>()
    private val savedPurchaseNames = mutableListOf<Pair<Long, String>>()

    @Test fun historySelectionTransfersBetIntoDetailAndPublicResultDestination() {
        show("tickets/my-bets")
        compose.onNodeWithContentDescription("Detalhe da aposta").performClick()
        compose.onNodeWithContentDescription("Ver resultado").performScrollTo().performClick()
        compose.onNodeWithText("Destino resultado").assertExists()
        compose.runOnIdle { assertEquals(bet, navigation.details.selectedBet) }
    }

    @Test fun historyRepeatWaitsForRecoveryAndAddsCurrentNumbersWithoutNavigatingAway() {
        show("tickets/my-bets")
        compose.onNodeWithContentDescription("Adicionar ao carrinho").performClick()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertExists()
        compose.runOnIdle {
            assertEquals(1, addedDrafts.size)
            assertEquals(bet.numbers, addedDrafts.single().numbers)
            assertEquals(2, addedDrafts.single().modalityId)
            assertEquals("NORMAL", addedDrafts.single().contestType)
        }
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertDoesNotExist()
        compose.runOnIdle { assertEquals("tickets/my-bets", nav.currentDestination?.route) }
    }

    @Test fun historyHelpUsesFaqAndCartOpensCurrentCart() {
        show("tickets/my-bets")
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.onNodeWithText("FAQ").assertExists()
        compose.runOnIdle { nav.popBackStack() }
        compose.onNodeWithText("Valor atual do carrinho", substring = true).performClick()
        compose.onNodeWithText("Destino carrinho").assertExists()
    }

    @Test fun detailEditTransfersRecoveredBetAndCorrectModality() {
        show("tickets/bet-detail", seedBet = true)
        compose.onNodeWithText("Editar aposta").performClick()
        compose.onNodeWithText("Editar modalidade 2").assertExists()
        compose.runOnIdle { assertEquals(bet, navigation.details.betToEdit) }
    }

    @Test fun detailPrizeRedemptionUsesRecoveredIdentifier() {
        show("tickets/bet-detail", seedBet = true)
        compose.onNodeWithContentDescription("Resgatar prêmio").performScrollTo().performClick()
        compose.onNodeWithText("Resgatar aposta 9").assertExists()
    }

    @Test fun detailHelpPassesModalityIdentifierAndHumanName() {
        show("tickets/bet-detail", seedBet = true)
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.onNodeWithText("Ajuda modalidade 2").assertExists()
        compose.runOnIdle { assertEquals("Mega-Sena", nav.currentBackStackEntry?.arguments?.getString("modalityName")) }
    }

    @Test fun detailCartUsesCurrentCartDestination() {
        show("tickets/bet-detail", seedBet = true)
        compose.onNodeWithContentDescription("Carrinho de apostas, 1 itens").performClick()
        compose.onNodeWithText("Destino carrinho").assertExists()
    }

    @Test fun missingBetAndPurchaseReturnToOriginWithoutMockFixtures() {
        show("tickets/bet-detail")
        compose.onNodeWithText("Origem").assertExists()
        compose.runOnIdle { nav.navigate("tickets/purchase-detail") }
        compose.onNodeWithText("Origem").assertExists()
        compose.runOnIdle {
            assertNull(navigation.details.selectedBet)
            assertNull(navigation.details.selectedPurchase)
        }
    }

    @Test fun purchaseSelectionTransfersPurchaseAndDetailHelpUsesFaq() {
        show("tickets/purchases")
        compose.onNodeWithText("Situação: Efetivada").performClick()
        compose.onNodeWithText("Apostas da Compra").assertExists()
        compose.runOnIdle { assertEquals(purchase, navigation.details.selectedPurchase) }
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.onNodeWithText("FAQ").assertExists()
    }

    @Test fun purchaseFavoriteSaveUsesPurchaseIdentifierAndNavigatesOnlyAfterAcknowledgment() {
        show("tickets/purchases")
        compose.onNodeWithText("Situação: Efetivada").performClick()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").performClick()
        compose.onNode(hasSetTextAction()).performTextInput("Meus concursos")
        compose.onNodeWithText("OK").performClick()
        compose.onNodeWithText("Carrinho favorito salvo com sucesso.").assertExists()
        compose.runOnIdle {
            assertEquals(listOf(77L to "Meus concursos"), savedPurchaseNames)
            assertEquals("tickets/purchase-detail", nav.currentDestination?.route)
        }
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Destino favoritos").assertExists()
    }

    @Test fun scannerModalityChoiceSavesIntoScannerAndPreservesChoiceOnReturn() {
        val application = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
        org.robolectric.Shadows.shadowOf(application).denyPermissions(android.Manifest.permission.CAMERA)
        show("tickets/read-game-choice")
        compose.onNodeWithText("Mega-Sena").performClick()
        compose.onNodeWithText("Salvar").assertIsEnabled().performClick()
        compose.onNodeWithText("Permita o acesso à câmera para ler o código do bilhete.").assertExists()
        compose.runOnIdle { assertTrue(nav.popBackStack()) }
        compose.onNodeWithText("Selecione a aposta realizada").assertExists()
        compose.onNodeWithText("Salvar").assertIsEnabled()
    }

    @Test fun scannerChoiceHelpUsesFaqAndBackReturnsToOrigin() {
        show("tickets/read-game-choice")
        compose.onNodeWithContentDescription("Termos de Uso").performClick()
        compose.onNodeWithText("FAQ").assertExists()
        compose.runOnIdle { nav.popBackStack() }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Origem").assertExists()
    }

    private fun show(route: String, seedBet: Boolean = false) {
        compose.setContent {
            nav = rememberNavController()
            navigation = rememberAppNavigationState(identity.ownerKey)
            CaixaDSTheme {
                NavHost(nav, startDestination = "origin") {
                    composable("origin") { CaixaDSText("Origem") }
                    composable("home") { CaixaDSText("Destino início") }
                    composable("cart/current") { CaixaDSText("Destino carrinho") }
                    composable("cart/favorites") { CaixaDSText("Destino favoritos") }
                    composable("results/confirmed-bet") { CaixaDSText("Destino resultado") }
                    composable("betting/simulation/{modalityId}") { entry ->
                        CaixaDSText("Editar modalidade ${entry.arguments?.getString("modalityId")}")
                    }
                    composable("retail/prize/{betId}") { entry -> CaixaDSText("Resgatar aposta ${entry.arguments?.getString("betId")}") }
                    composable(
                        "support/terms?tab={tab}",
                        arguments = listOf(
                            navArgument("tab") {
                                type = NavType.StringType
                                defaultValue =
                                    ""
                            }
                        )
                    ) { CaixaDSText("FAQ") }
                    composable("support/game-help?modalityId={modalityId}&modalityName={modalityName}") { entry ->
                        CaixaDSText("Ajuda modalidade ${entry.arguments?.getString("modalityId")}")
                    }
                    ticketsGraph(adapter, nav, navigation, session, AppNavigationActions({ _, _ -> }, {}, {}, {}, {}, {}))
                }
            }
        }
        compose.runOnIdle {
            if (seedBet) navigation.details.selectedBet = bet
            nav.navigate(route)
        }
        compose.waitForIdle()
    }

    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity get() = this@TicketsNavigationIntegrationUiTest.identity
        override val privateEntryKey = "test-owner"
        override val isPrivateSessionCurrent: () -> Boolean = { true }
        override val onLogout: () -> Unit = {}
        override val requestVisitorLogin: (String) -> Unit = {}
        override val recheckAccountCompletion: () -> Unit = {}
        override val onAuthenticated: (String) -> Unit = {}
        override val onTryNow: () -> Unit = {}
    }

    private val adapter by lazy {
        TicketsNavigationDependenciesAdapter(
            checkTicket = dependencies.checkTicket,
            currentCart = dependencies.currentCart,
            downloadBetReceipt = dependencies.downloadBetReceipt,
            downloadPrizeReceipt = dependencies.downloadPrizeReceipt,
            validateFavoriteCartName = dependencies.validateFavoriteCartName,
            hostConfiguration = br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration(
                appVersionName = "",
                accountReferrerUri = "",
                ssoAccountUrl = "",
                ssoClientId = "",
                environmentSelectorVisible = false,
                homeNavigationEnvironmentLabel = null,
                mockBackendEnabled = false,
                firebaseAppId = "",
                firebaseApiKey = "",
                firebaseProjectId = "",
                firebaseSenderId = "",
                backendApiBaseUrl = "",
                backendSessionNamespace = "",
                ssoAuthUrl = "",
                ssoTokenUrl = "",
                ssoLogoutUrl = "",
                ssoRedirectUri = "",
                productionBuild = false
            ),
            ticketsPurchaseDependencies = br.gov.caixa.loterias.feature.tickets.navigation.TicketsPurchaseDependencies(
                purchaseBets = dependencies.purchaseBets,
                purchaseDetails = dependencies.purchaseDetails,
                purchaseMonths = dependencies.purchaseMonths,
                purchasePaymentMethods = dependencies.purchasePaymentMethods,
                purchaseProgress = dependencies.purchaseProgress,
                purchaseStatuses = dependencies.purchaseStatuses,
                purchases = dependencies.purchases,
                savePurchaseAsFavorite = dependencies.savePurchaseAsFavorite,
                validateDam = dependencies.validateDam
            ),
            ticketsBetDependencies = br.gov.caixa.loterias.feature.tickets.navigation.TicketsBetDependencies(
                addBetToCart = dependencies.addBetToCart,
                bettingParameters = dependencies.bettingParameters,
                confirmedBetResults = dependencies.confirmedBetResults,
                confirmedBets = dependencies.confirmedBets,
                confirmedBetsConfiguration = dependencies.confirmedBetsConfiguration,
                loadConfirmedBet = dependencies.loadConfirmedBet,
                lotteryModalities = dependencies.lotteryModalities,
                recoverConfirmedBet = dependencies.recoverConfirmedBet,
                saveFavoriteBet = dependencies.saveFavoriteBet,
                validateFavoriteBet = dependencies.validateFavoriteBet
            ),
            ticketsAccountDependencies = br.gov.caixa.loterias.feature.tickets.navigation.TicketsAccountDependencies(
                authenticatedSessionIdentity = dependencies.authenticatedSessionIdentity,
                accountProfile = dependencies.accountProfile,
                prepareMercadoPagoRegistration = dependencies.prepareMercadoPagoRegistration
            ),
            runtime = br.gov.caixa.loterias.feature.tickets.navigation.TicketsRuntimeDependencies(
                dispatchers = dependencies.dispatchers,
                localClock = dependencies.localClock,
                networkAvailability = dependencies.networkAvailability
            )
        )
    }

    @Test fun navigationAdapterPreservesTransactionAndSessionDependencyIdentity() {
        assertSame(dependencies.addBetToCart, adapter.addBetToCart)
        assertSame(dependencies.bettingParameters, adapter.bettingParameters)
        assertSame(dependencies.currentCart, adapter.currentCart)
        assertSame(dependencies.dispatchers, adapter.dispatchers)
        assertSame(dependencies.authenticatedSessionIdentity, adapter.authenticatedSessionIdentity)
        assertEquals(false, adapter.mockBackendEnabled)
    }

    private val dependencies = object : TicketsNavigationDependencies {
        override val addBetToCart: AddBetToCartUseCase = AddBetToCartUseCase { draft ->
            addedDrafts.add(draft)
            AppResult.Success(cart)
        }
        override val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === this@TicketsNavigationIntegrationUiTest.identity
        }
        override val bettingParameters: GetBettingParametersUseCase =
            GetBettingParametersUseCase { AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal.TEN, 50, listOf(game))) }
        override val checkTicket: CheckTicketUseCase = CheckTicketUseCase { AppResult.Success(listOf(bet)) }
        override val confirmedBetResults: GetConfirmedBetResultsUseCase =
            GetConfirmedBetResultsUseCase { ServiceResult.Success(emptyList()) }
        override val confirmedBets: GetConfirmedBetsUseCase = GetConfirmedBetsUseCase { AppResult.Success(listOf(bet)) }
        override val confirmedBetsConfiguration: GetConfirmedBetsConfigurationUseCase =
            GetConfirmedBetsConfigurationUseCase { AppResult.Success(ConfirmedBetsConfiguration()) }
        override val currentCart: GetCurrentCartUseCase = GetCurrentCartUseCase { AppResult.Success(cart) }
        override val downloadBetReceipt: DownloadBetReceiptUseCase = DownloadBetReceiptUseCase { AppResult.Failure(AppError.Network) }
        override val downloadPrizeReceipt: DownloadPrizeReceiptUseCase = DownloadPrizeReceiptUseCase { AppResult.Failure(AppError.Network) }
        override val loadConfirmedBet: LoadConfirmedBetUseCase = LoadConfirmedBetUseCase { ServiceResult.Success(it) }
        override val lotteryModalities: GetLotteryModalitiesUseCase =
            GetLotteryModalitiesUseCase { AppResult.Success(listOf(LotteryModality(2, "Mega-Sena", "MEGA_SENA"))) }
        override val purchaseBets: GetPurchaseBetsUseCase = GetPurchaseBetsUseCase { AppResult.Success(listOf(bet)) }
        override val purchaseDetails: GetPurchaseDetailsUseCase =
            GetPurchaseDetailsUseCase { AppResult.Success(PurchaseDetails(it, listOf(bet))) }
        override val purchaseMonths: GetPurchaseMonthsUseCase =
            GetPurchaseMonthsUseCase { AppResult.Success(listOf(PurchaseMonth(10, 2026, "Outubro/2026"))) }
        override val purchasePaymentMethods: GetPurchasePaymentMethodsUseCase =
            GetPurchasePaymentMethodsUseCase { AppResult.Success(emptyList()) }
        override val purchaseProgress: GetPurchaseProgressUseCase =
            GetPurchaseProgressUseCase { AppResult.Success(PurchaseProgress(it, 8)) }
        override val purchaseStatuses: GetPurchaseStatusesUseCase = GetPurchaseStatusesUseCase { AppResult.Success(emptyList()) }
        override val purchases: GetPurchasesUseCase = GetPurchasesUseCase { AppResult.Success(listOf(purchase)) }
        override val recoverConfirmedBet: RecoverConfirmedBetUseCase = RecoverConfirmedBetUseCase { AppResult.Success(it) }
        override val saveFavoriteBet: SaveFavoriteBetUseCase = SaveFavoriteBetUseCase { AppResult.Success(Unit) }
        override val savePurchaseAsFavorite: SavePurchaseAsFavoriteUseCase =
            SavePurchaseAsFavoriteUseCase { id, name, _ ->
                savedPurchaseNames.add(id to name)
                AppResult.Success(Unit)
            }
        override val validateDam: ValidateDamUseCase = ValidateDamUseCase { AppResult.Success(DamValidation(false, "")) }
        override val validateFavoriteBet: ValidateFavoriteBetUseCase = ValidateFavoriteBetUseCase { AppResult.Success(Unit) }
        override val validateFavoriteCartName: ValidateFavoriteCartNameUseCase = ValidateFavoriteCartNameUseCase { AppResult.Success(Unit) }
        override val accountProfile: GetAccountProfileUseCase = GetAccountProfileUseCase { AppResult.Failure(AppError.Network) }
        override val prepareMercadoPagoRegistration: PrepareMercadoPagoRegistrationUseCase =
            PrepareMercadoPagoRegistrationUseCase { AppResult.Failure(AppError.Network) }
        override val dispatchers: CoroutineDispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        override val localClock: Clock = java.time.Clock.systemUTC()
        override val networkAvailability: NetworkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED }
        override val mockBackendEnabled: Boolean = false
        override fun visualConfirmedBet(variant: String, originalRecurringBet: Boolean, redeemedPrize: Boolean): ConfirmedBet =
            error("Production navigation must not request a visual fixture")
    }
}
