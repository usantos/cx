package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.tickets.*
import br.gov.caixa.loterias.feature.tickets.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList

/** Real NavHost, production entry/callback/session composition keys and ticket consumers. Only service boundaries are synthetic. */
class VisitorPrivateLoadingUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private var owner by mutableStateOf<AuthenticatedSessionIdentity?>(null)
    private var request by mutableStateOf<String?>(null)
    private var continuation: String? = null
    private lateinit var nav: NavHostController
    private val reads = CopyOnWriteArrayList<String>()
    private val first = AuthenticatedSessionIdentity("a".repeat(64))
    private val second = AuthenticatedSessionIdentity("b".repeat(64))
    private var model: TicketsViewModel? = null

    private fun show(route: String) {
        compose.setContent {
            CaixaDSTheme {
                nav = rememberNavController()
                val entryKey = rememberPrivateEntryKey(owner)
                NavHost(nav, startDestination = "home") {
                    composable("home") { TextButton({ openVisitorDrawerDestination(route, nav) { request = it } }) { Text("Abrir drawer") } }
                    composable(route) {
                        key(entryKey) {
                            val identity = owner
                            val name = if (identity?.ownerKey == first.ownerKey) "Owner A" else "Owner B"
                            val vm = viewModel(key = "history-$entryKey") {
                                TicketsViewModel(
                                    getConfirmedBets = GetConfirmedBetsUseCase {
                                        reads.add("bets:$name")
                                        AppResult.Success(listOf(ConfirmedBet(1, name, 2890, "01/09/2026", "Ativa", listOf(1, 2, 3))))
                                    },
                                    getPurchases = GetPurchasesUseCase {
                                        reads.add("purchases:$name")
                                        AppResult.Success(listOf(Purchase(9, "01/09/2026", name, "Pix", BigDecimal.TEN)))
                                    },
                                    getPurchaseBets = GetPurchaseBetsUseCase { error("Unexpected purchase detail read") },
                                    checkTicket = CheckTicketUseCase { error("Unexpected ticket read") },
                                    getBetsConfiguration = GetConfirmedBetsConfigurationUseCase {
                                        reads.add("configuration:$name"); AppResult.Success(ConfirmedBetsConfiguration())
                                    },
                                    getPurchaseMonths = GetPurchaseMonthsUseCase {
                                        reads.add("months:$name"); AppResult.Success(emptyList())
                                    },
                                )
                            }
                            SideEffect { model = vm }
                            if (route == "tickets/my-bets") ConfirmedBetsDataRoute(vm, onBack = {}, onOpen = {}, privateDataEnabled = identity != null)
                            else PurchasesDataRoute(vm, onBack = {}, onOpen = {}, privateDataEnabled = identity != null)
                        }
                    }
                    composable("account/login?authorize={authorize}") { TextButton({
                        owner = first
                        nav.navigate(continuation!!) { popUpTo("home") { inclusive = false } }
                    }) { Text("Autenticar") } }
                }
                request?.let { requested ->
                    val callbacks = VisitorLoginCallbacks(requested, nav, { request }, { request = null }, { continuation = it })
                    CaixaDSLegacyDialog("Atenção", "Efetuar login?", "Sim", callbacks::onPositive, "Não", callbacks::onNegative, visitorPrompt = true)
                }
            }
        }
        compose.onNodeWithText("Abrir drawer").performClick()
        compose.waitForIdle()
        assertTrue(reads.isEmpty())
        compose.runOnIdle { assertEquals(route, nav.currentDestination?.route) }
    }

    @Test fun authenticatedHistoryRestorationReusesLoadedViewModelWithoutSecondPrivateQuery() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            CaixaDSTheme {
                val entryKey = rememberPrivateEntryKey(first)
                key(entryKey) {
                    val vm = viewModel(key = "restored-history-$entryKey") {
                        TicketsViewModel(
                            GetConfirmedBetsUseCase { reads.add("bets:Owner A"); AppResult.Success(emptyList()) },
                            GetPurchasesUseCase { error("Unexpected purchase read") },
                            GetPurchaseBetsUseCase { error("Unexpected detail read") },
                            CheckTicketUseCase { error("Unexpected ticket read") },
                        )
                    }
                    SideEffect { model = vm }
                    ConfirmedBetsDataRoute(vm, {}, {}, privateDataEnabled = true)
                }
            }
        }
        compose.waitUntil(10_000) { reads.size == 1 && model?.state?.value?.loaded == true }
        compose.waitForIdle()
        val loadedModel = model
        restoration.emulateSavedInstanceStateRestore()
        compose.waitForIdle()
        assertSame(loadedModel, model)
        assertEquals(listOf("bets:Owner A"), reads.toList())
    }

    @Test fun guestDeclineClosesActualHistoryUnderlayWithoutPrivateQueries() {
        show("tickets/my-bets")
        compose.onNodeWithText("Últimos 7 dias").assertExists()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route); assertNull(continuation) }
        assertTrue(reads.isEmpty())
    }

    @Test fun guestAcceptAuthenticatesCurrentOwnerOnceAndNeverShowsPreviousOwnerSnapshot() {
        show("tickets/my-bets")
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle { assertEquals("tickets/my-bets", continuation); assertTrue(reads.isEmpty()) }
        compose.onNodeWithText("Autenticar").performClick()
        compose.waitUntil(10_000) { reads.size == 2 && model?.state?.value?.loaded == true }
        compose.waitForIdle()
        assertEquals(listOf("configuration:Owner A", "bets:Owner A"), reads.toList())
        val old = model
        compose.runOnIdle { owner = null }
        compose.waitForIdle()
        assertNotSame(old, model)
        assertFalse(model!!.state.value.loaded)
        assertTrue(model!!.state.value.bets.isEmpty())
        compose.onAllNodesWithText("Owner A", substring = true).assertCountEquals(0)
        assertEquals(2, reads.size)
        compose.runOnIdle { owner = second }
        compose.waitUntil(10_000) { reads.size == 4 && model?.state?.value?.loaded == true }
        compose.waitForIdle()
        assertEquals(listOf("configuration:Owner A", "bets:Owner A", "configuration:Owner B", "bets:Owner B"), reads.toList())
        compose.onAllNodesWithText("Owner A", substring = true).assertCountEquals(0)
        // A new validated session of the same owner also gets a fresh VM; cached private UI cannot become the initial state.
        val secondModel = model
        compose.runOnIdle { owner = AuthenticatedSessionIdentity("b".repeat(64)) }
        compose.waitUntil(10_000) { reads.size == 6 && model?.state?.value?.loaded == true }
        compose.waitForIdle()
        assertNotSame(secondModel, model)
        assertEquals(6, reads.size)
    }

    @Test fun purchasesRemainUnqueriedBehindPromptThenLoadOnceForAuthenticatedOwner() {
        show("tickets/purchases")
        compose.onNodeWithText("Selecione o mês e ano").assertExists()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle { assertTrue(reads.isEmpty()) }
        compose.onNodeWithText("Autenticar").performClick()
        compose.waitUntil(10_000) { reads.size == 2 && model?.state?.value?.loaded == true }
        compose.waitForIdle()
        assertEquals(listOf("months:Owner A", "purchases:Owner A"), reads.toList())
    }
}
