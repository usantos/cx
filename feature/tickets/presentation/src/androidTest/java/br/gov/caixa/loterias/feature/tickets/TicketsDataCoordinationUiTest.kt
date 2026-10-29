// Literais numéricos descrevem códigos de bilhete, identificadores de compra e valores dos filtros de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.navigation.ticketManualNavigation
import br.gov.caixa.loterias.feature.tickets.presentation.route.ConfirmedBetsDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.ManualTicketDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.PurchasesDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.TicketScannerRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Caracterização da rota pública com casos de uso locais; nenhum serviço autenticado é usado. */
class TicketsDataCoordinationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun publicTicketResultDoesNotOfferSaveActionAbsentFromLegacy() {
        compose.setContent {
            CaixaDSTheme {
                br.gov.caixa.loterias.feature.tickets.presentation.route.TicketResultDataRoute(
                    bets = emptyList(),
                    onBack = {}
                )
            }
        }
        compose.onNodeWithText("Salvar").assertDoesNotExist()
    }

    @Test fun publicTicketResultKeepsLegacyToolbarHelp() {
        compose.setContent {
            CaixaDSTheme {
                br.gov.caixa.loterias.feature.tickets.presentation.route.TicketResultDataRoute(
                    bets = emptyList(),
                    onBack = {}
                )
            }
        }
        compose.onNodeWithContentDescription("Dúvidas").assertHasClickAction()
    }

    @Test fun betNowFromPublicTicketRequestsItsModalityOnHome() {
        lateinit var navigation: br.gov.caixa.loterias.core.navigation.state.AppNavigationState
        val check = object : CheckTicketUseCase {
            override suspend fun invoke(query: TicketQuery) = AppResult.Success(emptyList<ConfirmedBet>())
            override suspend fun result(query: TicketQuery) = AppResult.Success(
                br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult(
                    statusId = 9,
                    modalityId = 8,
                    modalityName = "Lotofácil"
                )
            )
        }
        val dependencies = java.lang.reflect.Proxy.newProxyInstance(
            br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependencies::class.java.classLoader,
            arrayOf(br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependencies::class.java)
        ) { _, method, _ ->
            when (method.name) {
                "getConfirmedBets" -> GetConfirmedBetsUseCase { AppResult.Success(emptyList()) }

                "getPurchases" -> GetPurchasesUseCase { AppResult.Success(emptyList()) }

                "getPurchaseBets" -> GetPurchaseBetsUseCase { AppResult.Success(emptyList()) }

                "getCheckTicket" -> check

                "getDispatchers" -> FixedCoroutineDispatchers(Dispatchers.Main.immediate)

                "getConfirmedBetsConfiguration" -> GetConfirmedBetsConfigurationUseCase { AppResult.Success(ConfirmedBetsConfiguration()) }

                "getLotteryModalities" -> br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase {
                    AppResult.Success(emptyList())
                }

                "getPurchaseMonths" -> br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase {
                    AppResult.Success(emptyList())
                }

                "getPurchasePaymentMethods" -> br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase {
                    AppResult.Success(emptyList())
                }

                "getPurchaseStatuses" -> br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase {
                    AppResult.Success(emptyList())
                }

                "getBettingParameters" -> br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase {
                    AppResult.Failure(AppError.Network)
                }

                else -> error("Unexpected dependency: ${method.name}")
            }
        } as br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependencies
        compose.setContent {
            navigation = br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState(null)
            val nav = androidx.navigation.compose.rememberNavController()
            CaixaDSTheme {
                androidx.navigation.compose.NavHost(nav, startDestination = "tickets/manual-code") {
                    ticketManualNavigation(dependencies, nav, navigation)
                    composable("home") { androidx.compose.material3.Text("Home destino") }
                }
            }
        }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("123456789012345678901")
        compose.onNodeWithText("Continuar").performClick()
        compose.onNodeWithText("Aposte\nagora").performClick()
        compose.onNodeWithText("Home destino").assertIsDisplayed()
        compose.runOnIdle { assertEquals(8, navigation.requestedHomeModalityId) }
    }

    @Test fun confirmedBetsLoadOnlyWhenPrivateDataBecomesAvailableAndDoNotReloadLoadedState() {
        val fixture = Fixture()
        val privateData = mutableStateOf(false)
        compose.setContent {
            val model = remember { fixture.model() }
            CaixaDSTheme { ConfirmedBetsDataRoute(model, {}, {}, privateDataEnabled = privateData.value) }
        }
        compose.runOnIdle {
            assertEquals(0, fixture.betLoads)
            privateData.value = true
        }
        compose.runOnIdle {
            assertEquals(1, fixture.betLoads)
            privateData.value = false
        }
        compose.runOnIdle { privateData.value = true }
        compose.runOnIdle { assertEquals(1, fixture.betLoads) }
    }

    @Test fun purchasesLoadOnlyWhenPrivateDataBecomesAvailableAndDoNotReloadLoadedState() {
        val fixture = Fixture()
        val privateData = mutableStateOf(false)
        compose.setContent {
            val model = remember { fixture.model() }
            CaixaDSTheme { PurchasesDataRoute(model, {}, {}, privateDataEnabled = privateData.value) }
        }
        compose.runOnIdle {
            assertEquals(0, fixture.purchaseLoads)
            privateData.value = true
        }
        compose.runOnIdle {
            assertEquals(1, fixture.purchaseLoads)
            privateData.value = false
        }
        compose.runOnIdle { privateData.value = true }
        compose.runOnIdle { assertEquals(1, fixture.purchaseLoads) }
    }

    @OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
    @Test
    fun cancelKeepsSelectedBetFilterButOnlyApplyReloadsTheList() {
        val fixture = Fixture()
        val applyLabel = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(br.gov.caixa.loterias.feature.tickets.R.string.tickets_filter_apply)
        lateinit var model: TicketsViewModel
        compose.setContent {
            model = remember { fixture.model() }
            CaixaDSTheme { ConfirmedBetsDataRoute(model, {}, {}) }
        }
        compose.onNodeWithContentDescription("Filtrar").performClick()
        compose.onNodeWithText("Situação da aposta").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Situação da aposta").performCustomAccessibilityActionWithLabel("Conferida")
        compose.runOnIdle {
            assertEquals(2L, model.state.value.betFilters.status)
            assertEquals(1, fixture.betLoads)
        }
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertFalse(model.state.value.showBetFilters)
            assertEquals(2L, model.state.value.betFilters.status)
            assertEquals(1, fixture.betLoads)
        }
        compose.waitUntilDoesNotExist(hasText(applyLabel), timeoutMillis = 10_000)
        compose.onNodeWithContentDescription("Filtrar").performClick()
        compose.runOnIdle { assertTrue(model.state.value.showBetFilters) }
        compose.waitUntilAtLeastOneExists(hasText(applyLabel), timeoutMillis = 10_000)
        compose.onNodeWithText(applyLabel).performClick()
        compose.runOnIdle { assertEquals(2, fixture.betLoads) }
    }

    @Test fun manualInputKeepsMasksAndReturnsSuccessfulEmptyResultWithRetailerQuery() {
        val fixture = Fixture()
        val results = mutableListOf<List<ConfirmedBet>>()
        showManual(fixture, results)
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("123456789012345678")
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("12345678901234567890123")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("123456789")
        compose.onAllNodes(hasSetTextAction())[0].assertTextEquals("1234-56789012345678901-23")
        compose.onAllNodes(hasSetTextAction())[1].assertTextEquals("12.345678-9")
        compose.onNodeWithText("Continuar").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(listOf(TicketQuery("12345678901234567890123", qrCode = true, retailerCode = "123456789")), fixture.queries)
            assertEquals(listOf(emptyList<ConfirmedBet>()), results)
        }
    }

    @Test fun manualFailureDoesNotReturnAndCanRetryWithoutOptionalRetailer() {
        val fixture = Fixture().apply { ticketResult = AppResult.Failure(AppError.Network) }
        val results = mutableListOf<List<ConfirmedBet>>()
        showManual(fixture, results)
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("123456789012345678901")
        compose.onNodeWithText("Continuar").performClick()
        compose.onNodeWithText("Não foi possível carregar os dados. Tente novamente.").assertIsDisplayed()
        compose.runOnIdle {
            assertTrue(results.isEmpty())
            fixture.ticketResult = AppResult.Success(emptyList())
        }
        compose.onNodeWithText("Continuar").performClick()
        compose.runOnIdle {
            assertEquals(List(2) { TicketQuery("123456789012345678901") }, fixture.queries)
            assertEquals(listOf(emptyList<ConfirmedBet>()), results)
        }
    }

    @Test fun scannerCanReturnFromResultWithoutOpeningTheSameResultAgain() = verifyQueryReturn(scanner = true)

    @Test fun manualCanReturnFromResultWithoutOpeningTheSameResultAgain() = verifyQueryReturn(scanner = false)

    private fun verifyQueryReturn(scanner: Boolean) {
        val fixture = Fixture()
        val showingQuery = mutableStateOf(true)
        var results = 0
        lateinit var model: TicketsViewModel
        compose.setContent {
            model = remember { fixture.model() }
            CaixaDSTheme {
                if (showingQuery.value) {
                    val onResult: (List<ConfirmedBet>) -> Unit = {
                        results++
                        showingQuery.value = false
                    }
                    if (scanner) {
                        TicketScannerRoute(model, {}, {}, onResult)
                    } else {
                        ManualTicketDataRoute(model, {}, onResult)
                    }
                }
            }
        }
        compose.runOnIdle {
            if (scanner) {
                model.submitScannedTicketCode("12345678901234567890123")
            } else {
                model.changeTicketCode("12345678901234567890123")
                model.checkTicket()
            }
        }
        compose.runOnIdle {
            assertEquals(1, results)
            showingQuery.value = true
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, results)
            assertNotNull(model.state.value.ticketResult)
            if (scanner) {
                model.submitScannedTicketCode("12345678901234567890124")
            } else {
                model.checkTicket()
            }
        }
        compose.runOnIdle {
            assertEquals(2, results)
            assertEquals(2, fixture.queries.size)
        }
    }

    private fun showManual(fixture: Fixture, results: MutableList<List<ConfirmedBet>>) {
        compose.setContent {
            val model = remember { fixture.model() }
            CaixaDSTheme { ManualTicketDataRoute(model, {}, { results.add(it) }) }
        }
    }

    private class Fixture {
        var betLoads = 0
        var purchaseLoads = 0
        var ticketResult: AppResult<List<ConfirmedBet>> = AppResult.Success(emptyList())
        val queries = mutableListOf<TicketQuery>()

        fun model() = TicketsViewModel(
            GetConfirmedBetsUseCase {
                betLoads++
                AppResult.Success(emptyList())
            },
            GetPurchasesUseCase {
                purchaseLoads++
                AppResult.Success(emptyList())
            },
            GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
            CheckTicketUseCase { query ->
                queries.add(query)
                ticketResult
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            getBetsConfiguration = GetConfirmedBetsConfigurationUseCase {
                AppResult.Success(
                    ConfirmedBetsConfiguration(
                        statuses = listOf(
                            TicketFilterOption(1, "Efetivada"),
                            TicketFilterOption(2, "Conferida")
                        )
                    )
                )
            }
        )
    }
}
