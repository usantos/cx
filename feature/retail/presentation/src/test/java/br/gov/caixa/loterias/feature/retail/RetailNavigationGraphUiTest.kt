// Literais numéricos descrevem dados sintéticos, níveis de API da plataforma e limites esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.retail

import androidx.compose.runtime.key
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionOption
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.model.RetailLocationEligibility
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocationUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.feature.retail.navigation.RetailNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.retail.navigation.retailGraph
import java.math.BigDecimal
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RetailNavigationGraphUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private val requested = mutableListOf<Long>()
    private var syntheticSubmissionEnabled = false
    private val syntheticSubmissions = mutableListOf<Triple<Long, PrizeRedemptionChannel, String>>()
    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity = AuthenticatedSessionIdentity("a".repeat(64))
        override val privateEntryKey = "private-entry"
        override val isPrivateSessionCurrent = { true }
        override val onLogout = {}
        override val requestVisitorLogin: (String) -> Unit = { error("unexpected visitor login") }
        override val recheckAccountCompletion = {}
        override val onAuthenticated: (String) -> Unit = {}
        override val onTryNow = {}
    }
    private fun dependencies(mock: Boolean) = RetailNavigationDependenciesAdapter(
        checkRetailLocation = object : CheckRetailLocationUseCase {
            override suspend fun invoke(mockInsideBrazil: Boolean) = RetailLocationEligibility.ALLOWED
        },
        bettingParameters = GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 100, emptyList()))
        },
        downloadBetReceipt = DownloadBetReceiptUseCase { error("No document expected") },
        prepareBetEntry = PrepareBetEntryUseCase { AppResult.Failure(AppError.Unexpected) },
        searchRetailers = SearchRetailersUseCase { AppResult.Success(emptyList()) },
        setRetailerFavorite = SetRetailerFavoriteUseCase { _, _ -> error("No favorite expected") },
        hostConfiguration = HostFeatureConfiguration(
            "1.2.3", "app:/account", "https://s.example/account", "client", false, null, mock,
            "app",
            "key",
            "project",
            "sender",
            "https://api.example/",
            "https://api.example/",
            "https://s.example/auth",
            "https://s.example/token",
            "https://s.example/logout", "app:/callback", !mock
        ),
        retailRedemptionDependencies = br.gov.caixa.loterias.feature.retail.navigation.RetailRedemptionDependencies(
            cancelPixRedemption = CancelPixRedemptionUseCase { AppResult.Success(Unit) },
            consultPixRedemptionKey = ConsultPixRedemptionKeyUseCase { id, key ->
                check(syntheticSubmissionEnabled)
                assertEquals(9L, id)
                AppResult.Success(PixBeneficiary("masked", "Synthetic User", key, "Synthetic Bank"))
            },
            downloadPrizeReceipt = DownloadPrizeReceiptUseCase { error("No document expected") },
            lotteryRedemptionCode = GenerateLotteryRedemptionCodeUseCase {
                AppResult.Success(LotteryRedemptionCode("", "synthetic-code", "synthetic-nsb", 15))
            },
            prizeDetails = GetPrizeDetailsUseCase { id ->
                requested += id
                AppResult.Success(
                    PrizeDetails(
                        id,
                        BigDecimal("125.50"),
                        "synthetic-nsbi",
                        PrizeRedemptionChannel.entries.map { PrizeRedemptionOption(it, it.name, "Synthetic option") }
                    )
                )
            },
            redeemPrize = RedeemPrizeUseCase { id, channel, value ->
                check(syntheticSubmissionEnabled)
                syntheticSubmissions += Triple(id, channel, value)
                AppResult.Success(PrizeRedemptionResult.Redeemed())
            }
        ),
        retailAccountDependencies = br.gov.caixa.loterias.feature.retail.navigation.RetailAccountDependencies(
            accountProfile = GetAccountProfileUseCase { error("No registration expected") },
            prepareMercadoPagoRegistration = PrepareMercadoPagoRegistrationUseCase { error("No registration expected") },
            currentUserIdentity = CurrentUserIdentityProvider {
                if (syntheticSubmissionEnabled) AuthenticatedUser("Synthetic User", "synthetic-cpf", "fixture@example.test", "") else null
            }
        ),
        runtime = br.gov.caixa.loterias.feature.retail.navigation.RetailRuntimeDependencies(
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            localClock = Clock.systemUTC(),
            networkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED }
        )
    )
    private fun show(mock: Boolean) {
        val dependencies = dependencies(mock)
        val actions = AppNavigationActions(
            { _, _ -> error("No bet entry expected") },
            { error("No bet entry expected") },
            { error("No redirect expected") },
            { error("No redirect expected") },
            { error("No payment expected") },
            {}
        )
        compose.setContent {
            CaixaDSTheme {
                nav = rememberNavController()
                NavHost(nav, "home") {
                    composable("home") { CaixaDSText("HOME FIXTURE") }
                    composable("tickets/my-bets") { CaixaDSText("MY BETS FIXTURE") }
                    composable("support/terms?tab={tab}") { CaixaDSText("HELP FIXTURE") }
                    retailGraph(dependencies, nav, session, actions)
                }
            }
        }
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }
    private fun open(route: String) {
        compose.runOnIdle { nav.navigate(route) { popUpTo("home") { inclusive = false } } }
        flush()
    }

    @Test fun `typed prize route retains channel owner on help and clears foreground channel host when returning`() {
        show(false)
        open("retail/prize/9")
        compose.onNodeWithText("Resultado da aposta").assertExists()
        compose.runOnIdle {
            assertEquals(listOf(9L), requested)
            assertFalse(session.state.prizeChannelHost)
        }
        compose.onNodeWithText("PIX").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Resgate de Prêmio via Pix").assertExists()
        compose.runOnIdle { assertTrue(session.state.prizeChannelHost) }
        compose.onNodeWithContentDescription("Ajuda").performClick()
        flush()
        compose.onNodeWithText("HELP FIXTURE").assertExists()
        compose.runOnIdle { nav.popBackStack() }
        flush()
        compose.onNodeWithText("Resgate de Prêmio via Pix").assertExists()
        compose.runOnIdle { assertEquals(listOf(9L), requested) }
        compose.onNodeWithText("Cancelar").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Resultado da aposta").assertExists()
        compose.runOnIdle { assertFalse(session.state.prizeChannelHost) }
        compose.onNodeWithContentDescription("Voltar").performClick()
        flush()
        compose.onNodeWithText("HOME FIXTURE").assertExists()
    }

    @Test fun `production rejects every mock alias before any prize service invocation`() {
        show(false)
        for (route in aliases) {
            open(route)
            compose.onNodeWithText("MY BETS FIXTURE").assertExists()
            compose.runOnIdle { assertEquals("tickets/my-bets", nav.currentDestination!!.route) }
        }
        assertTrue(requested.isEmpty())
    }

    @Test fun `mock aliases instantiate requested channel and controlled success routes without submitting money`() {
        show(true)
        for (route in aliases) {
            open(route)
            compose.runOnIdle { assertEquals(route, nav.currentDestination!!.route) }
            val title = when (route) {
                "retail/prize" -> "Resultado da aposta"
                "retail/prize-guidance", "retail/prize-lottery" -> "Lotérica"
                "retail/prize-agency" -> "Agência"
                "retail/prize-pix", "retail/prize-confirmation" -> "Resgate de Prêmio via Pix"
                else -> "Resgate de Prêmio via Mercado Pago"
            }
            compose.onNodeWithText(title).assertExists()
            if (route == "retail/prize-confirmation") {
                compose.onNodeWithText("Seu resgate foi solicitado com sucesso!").assertExists()
                compose.onNodeWithText("Acesse \nMinhas apostas").performScrollTo().performClick()
                flush()
                compose.onNodeWithText("MY BETS FIXTURE").assertExists()
            } else if (route == "retail/prize-mercado-pago-success") {
                compose.onNodeWithText("Voltar ao início").performClick()
                flush()
                compose.onNodeWithText("HOME FIXTURE").assertExists()
            }
        }
        assertTrue(requested.isNotEmpty())
        assertTrue(requested.all { it == 100L })
    }

    @Test fun `mock location gate advances to search and search help returns to same destination`() {
        show(true)
        open("retail/location")
        compose.onNodeWithText("Buscar lotéricas").assertExists()
        compose.runOnIdle { assertEquals("retail/search", nav.currentDestination!!.route) }
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        flush()
        compose.onNodeWithText("HELP FIXTURE").assertExists()
        compose.runOnIdle { nav.popBackStack() }
        flush()
        compose.onNodeWithText("Buscar lotéricas").assertExists()
        compose.onNodeWithContentDescription("Voltar").performClick()
        flush()
        compose.onNodeWithText("HOME FIXTURE").assertExists()
    }

    @Test fun `typed synthetic prize success returns Pix to tickets and Mercado Pago to home`() {
        syntheticSubmissionEnabled = true
        show(false)
        open("retail/prize/9")
        compose.onNodeWithText("PIX").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("synthetic-cpf").assertExists()
        compose.onNodeWithText("Continuar").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Synthetic User").assertExists()
        compose.onNodeWithText("Resgatar").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Acesse \nMinhas apostas").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("MY BETS FIXTURE").assertExists()
        open("retail/prize/10")
        compose.onNodeWithText("MERCADO_PAGO").performScrollTo().performClick()
        flush()
        compose.onNode(hasSetTextAction()).performTextReplacement("updated@example.test")
        flush()
        compose.onNodeWithText("Resgatar prêmio").performClick()
        flush()
        compose.onNodeWithText("Voltar ao início").performClick()
        flush()
        compose.onNodeWithText("HOME FIXTURE").assertExists()
        compose.runOnIdle {
            assertEquals(
                listOf(
                    Triple(9L, PrizeRedemptionChannel.PIX, "synthetic-cpf"),
                    Triple(10L, PrizeRedemptionChannel.MERCADO_PAGO, "updated@example.test")
                ),
                syntheticSubmissions
            )
            assertFalse(session.state.prizeChannelHost)
        }
    }
    private val aliases = listOf(
        "retail/prize",
        "retail/prize-guidance",
        "retail/prize-lottery",
        "retail/prize-agency",
        "retail/prize-pix",
        "retail/prize-confirmation",
        "retail/prize-mercado-pago",
        "retail/prize-mercado-pago-success"
    )
}
