package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks
import br.gov.caixa.loterias.core.navigation.workflow.openVisitorDrawerDestination
import br.gov.caixa.loterias.core.navigation.workflow.rememberPrivateEntryKey
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.route.ConfirmedBetsDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.PurchasesDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * NavHost e consumidores de bilhetes reais, com chaves de composição de entrada, retorno e sessão de produção.
 * Somente as fronteiras dos serviços são simuladas.
 */
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
                    composable("home") {
                        TextButton({ openVisitorDrawerDestination(route, nav) { request = it } }) { Text("Abrir drawer") }
                    }
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
                                        reads.add("configuration:$name")
                                        AppResult.Success(ConfirmedBetsConfiguration())
                                    },
                                    getPurchaseMonths = GetPurchaseMonthsUseCase {
                                        reads.add("months:$name")
                                        AppResult.Success(emptyList())
                                    }
                                )
                            }
                            SideEffect { model = vm }
                            if (route ==
                                "tickets/my-bets"
                            ) {
                                ConfirmedBetsDataRoute(
                                    vm,
                                    onBack = {},
                                    onOpen = {},
                                    privateDataEnabled =
                                        identity != null
                                )
                            } else {
                                PurchasesDataRoute(vm, onBack = {}, onOpen = {}, privateDataEnabled = identity != null)
                            }
                        }
                    }
                    composable(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern) {
                        TextButton({
                            owner = first
                            nav.navigate(continuation!!) { popUpTo("home") { inclusive = false } }
                        }) { Text("Autenticar") }
                    }
                }
                request?.let { requested ->
                    val callbacks = VisitorLoginCallbacks(requested, nav, { request }, { request = null }, { continuation = it })
                    CaixaDSDialog(
                        "Atenção",
                        "Efetuar login?",
                        "Sim",
                        callbacks::onPositive,
                        "Não",
                        callbacks::onNegative,
                        visitorPrompt = true
                    )
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
                            GetConfirmedBetsUseCase {
                                reads.add("bets:Owner A")
                                AppResult.Success(emptyList())
                            },
                            GetPurchasesUseCase { error("Unexpected purchase read") },
                            GetPurchaseBetsUseCase { error("Unexpected detail read") },
                            CheckTicketUseCase { error("Unexpected ticket read") }
                        )
                    }
                    SideEffect { model = vm }
                    ConfirmedBetsDataRoute(vm, {}, {}, privateDataEnabled = true)
                }
            }
        }
        compose.waitUntil(10_000) {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            reads.size ==
                1 &&
                model?.state?.value?.loaded == true
        }
        compose.waitForIdle()
        val loadedModel = model
        restoration.emulateSavedInstanceStateRestore()
        compose.waitForIdle()
        assertSame(loadedModel, model)
        assertEquals(listOf("bets:Owner A"), reads.toList())
    }

    @Test fun savedHistoryFromOwnerARestoredAsGuestCannotReuseLoadedPrivateViewModel() {
        restoreHistoryWithDifferentLease(null)
    }

    @Test fun savedHistoryFromOwnerARestoredAsOwnerBLoadsFreshPrivateDataOnce() {
        restoreHistoryWithDifferentLease(second)
    }

    @Test fun savedHistoryRestoredWithNewLeaseOfSameOwnerLoadsFreshPrivateDataOnce() {
        restoreHistoryWithDifferentLease(AuthenticatedSessionIdentity("a".repeat(64)))
    }

    private fun restoreHistoryWithDifferentLease(replacement: AuthenticatedSessionIdentity?) {
        var restoringLease: AuthenticatedSessionIdentity? = first
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            CaixaDSTheme {
                val lease = restoringLease
                val label = if (lease === first) "Lease A" else "Restored lease"
                val entryKey = rememberPrivateEntryKey(lease)
                // Altera a concessão atual somente após capturar o estado salvo e descartar a
                // composição anterior, impedindo que as entradas de rememberSaveable tratem a alteração durante a execução.
                DisposableEffect(Unit) { onDispose { restoringLease = replacement } }
                key(entryKey) {
                    val vm = viewModel(key = "lease-restored-history-$entryKey") {
                        TicketsViewModel(
                            GetConfirmedBetsUseCase {
                                reads.add(label)
                                AppResult.Success(listOf(ConfirmedBet(1, label, 2890, "01/09/2026", "Ativa", listOf(1, 2, 3))))
                            },
                            GetPurchasesUseCase { error("Unexpected purchase read") },
                            GetPurchaseBetsUseCase { error("Unexpected detail read") },
                            CheckTicketUseCase { error("Unexpected ticket read") },
                            isSessionCurrent = { lease != null && restoringLease === lease }
                        )
                    }
                    SideEffect { model = vm }
                    ConfirmedBetsDataRoute(vm, {}, {}, privateDataEnabled = lease != null)
                }
            }
        }
        compose.waitUntil(10_000) {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            reads.size ==
                1 &&
                model?.state?.value?.loaded == true
        }
        compose.waitForIdle()
        val oldModel = model
        restoration.emulateSavedInstanceStateRestore()
        compose.waitForIdle()
        assertNotSame("A restored marker must belong to this exact validated lease", oldModel, model)
        compose.onAllNodesWithText("Lease A", substring = true).assertCountEquals(0)
        if (replacement == null) {
            assertTrue(model!!.state.value.bets.isEmpty())
            assertFalse(model!!.state.value.loaded)
            assertEquals(listOf("Lease A"), reads.toList())
        } else {
            compose.waitUntil(10_000) {
                compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
                compose.waitForIdle()
                model?.state?.value?.loaded ==
                    true
            }
            compose.waitForIdle()
            assertEquals(listOf("Lease A", "Restored lease"), reads.toList())
            assertEquals("Restored lease", model!!.state.value.bets.single().modality)
        }
    }

    @Test fun guestDeclineClosesActualHistoryUnderlayWithoutPrivateQueries() {
        show("tickets/my-bets")
        compose.onNodeWithText("Últimos 7 dias").assertExists()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertEquals("home", nav.currentDestination?.route)
            assertNull(continuation)
        }
        assertTrue(reads.isEmpty())
    }

    @Test fun guestAcceptAuthenticatesCurrentOwnerOnceAndNeverShowsPreviousOwnerSnapshot() {
        show("tickets/my-bets")
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle {
            assertEquals("tickets/my-bets", continuation)
            assertTrue(reads.isEmpty())
        }
        compose.onNodeWithText("Autenticar").performClick()
        compose.waitUntil(10_000) {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            reads.size ==
                2 &&
                model?.state?.value?.loaded == true
        }
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
        compose.waitUntil(10_000) {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            reads.size ==
                4 &&
                model?.state?.value?.loaded == true
        }
        compose.waitForIdle()
        assertEquals(listOf("configuration:Owner A", "bets:Owner A", "configuration:Owner B", "bets:Owner B"), reads.toList())
        compose.onAllNodesWithText("Owner A", substring = true).assertCountEquals(0)
        // Uma nova sessão validada do mesmo titular recebe outro VM; a interface privada em cache não pode ser o estado inicial.
        val secondModel = model
        compose.runOnIdle { owner = AuthenticatedSessionIdentity("b".repeat(64)) }
        compose.waitUntil(10_000) {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            reads.size ==
                6 &&
                model?.state?.value?.loaded == true
        }
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
        compose.waitUntil(10_000) {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            reads.size ==
                2 &&
                model?.state?.value?.loaded == true
        }
        compose.waitForIdle()
        assertEquals(listOf("months:Owner A", "purchases:Owner A"), reads.toList())
    }
}
