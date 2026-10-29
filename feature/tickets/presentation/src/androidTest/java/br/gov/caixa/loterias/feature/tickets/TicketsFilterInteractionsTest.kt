package br.gov.caixa.loterias.feature.tickets

import android.view.KeyEvent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.contestType
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseBetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Exercita diálogos de produção com estado elevado; nenhum serviço, repositório ou ViewModel é substituído. */
class TicketsFilterInteractionsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun purchaseFiltersSelectSpecificAndAllOptionsThenClearApplyCancelAndDismiss() {
        val state = mutableStateOf(
            TicketsUiState(
                paymentMethods = listOf(TicketFilterOption(1, "Cartão"), TicketFilterOption(6, "PIX")),
                purchaseStatuses = listOf(TicketFilterOption(2, "Aguardando"), TicketFilterOption(1, "Confirmada")),
                purchaseFilters = PurchaseFilters(paymentMethodId = 1, statusId = 2)
            )
        )
        val visible = mutableStateOf(true)
        val methods = mutableListOf<Long?>()
        val statuses = mutableListOf<Long?>()
        val applied = mutableListOf<PurchaseFilters>()
        var clears = 0
        var closes = 0
        showPurchaseFilters(state, visible, methods, statuses, applied, { clears++ }, { closes++ })
        radio("Cartão").assertIsSelected()
        radio("Aguardando").assertIsSelected()
        choose("PIX")
        choose("Confirmada")
        radio("PIX").assertIsSelected()
        radio("Cartão").assertIsNotSelected()
        radio("Confirmada").assertIsSelected()
        choose("Todos")
        choose("Todas")
        radio("Todos").assertIsSelected()
        radio("Todas").assertIsSelected()
        choose("PIX")
        choose("Confirmada")
        compose.onNodeWithText("Aplicar").performClick()
        compose.onNodeWithText("Filtrar compras").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(6L, null, 6L), methods)
            assertEquals(listOf(1L, null, 1L), statuses)
            assertEquals(listOf(PurchaseFilters(paymentMethodId = 6, statusId = 1)), applied)
            visible.value = true
        }
        compose.onNodeWithText("Limpar").performClick()
        radio("Todos").assertIsSelected()
        radio("Todas").assertIsSelected()
        choose("Cartão")
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Filtrar compras").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, clears)
            assertEquals(1, closes)
            assertEquals(PurchaseFilters(paymentMethodId = 1), state.value.purchaseFilters)
            visible.value = true
        }
        dismissWithBack()
        compose.onNodeWithText("Filtrar compras").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, closes)
            assertEquals(1, applied.size)
        }
    }

    @Test fun purchaseBetFiltersDeduplicateOptionsAndSupportMissingStatusAndAllSelections() {
        val first = ConfirmedBet(1, "Mega-Sena", 2502, "18/10/2026", "Confirmada", emptyList(), modalityId = 2, statusId = 10)
        val second = ConfirmedBet(2, "Quina", 2500, "18/10/2026", "", emptyList(), modalityId = 3, statusId = 0)
        val state = mutableStateOf(
            TicketsUiState(
                bets = listOf(first, second, first.copy(id = 3), second.copy(id = 4, contest = 0)),
                purchaseBetFilters = PurchaseBetFilters(contest = 2502, statusId = 10, modalityId = 3)
            )
        )
        val visible = mutableStateOf(true)
        val contests = mutableListOf<Int?>()
        val statuses = mutableListOf<Long?>()
        val modalities = mutableListOf<Int?>()
        val applied = mutableListOf<PurchaseBetFilters>()
        var clears = 0
        var closes = 0
        showPurchaseBetFilters(state, visible, contests, statuses, modalities, applied, { clears++ }, { closes++ })
        compose.onAllNodesWithText("2500").assertCountEquals(1)
        compose.onAllNodesWithText("2502").assertCountEquals(1)
        compose.onNodeWithText("0").assertDoesNotExist()
        compose.onAllNodesWithText("Não informada").assertCountEquals(1)
        compose.onAllNodesWithText("Confirmada").assertCountEquals(1)
        compose.onAllNodesWithText("Mega-Sena").assertCountEquals(1)
        compose.onAllNodesWithText("Quina").assertCountEquals(1)
        radio("2502").assertIsSelected()
        radio("Confirmada").assertIsSelected()
        radio("Quina").assertIsSelected()
        choose("2500")
        choose("Não informada")
        choose("Mega-Sena")
        radio("2500").assertIsSelected()
        radio("Não informada").assertIsSelected()
        radio("Mega-Sena").assertIsSelected()
        choose("Todos")
        compose.onAllNodesWithText("Todas")[0].performScrollTo().performClick()
        compose.onAllNodesWithText("Todas")[1].performScrollTo().performClick()
        radio("Todos").assertIsSelected()
        radio("Todas", 0).assertIsSelected()
        radio("Todas", 1).assertIsSelected()
        choose("2502")
        choose("Confirmada")
        choose("Quina")
        compose.onNodeWithText("Aplicar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(2500, null, 2502), contests)
            assertEquals(listOf(0L, null, 10L), statuses)
            assertEquals(listOf(2, null, 3), modalities)
            assertEquals(listOf(PurchaseBetFilters(2502, 10, 3)), applied)
            visible.value = true
        }
        compose.onNodeWithText("Limpar").performClick()
        radio("Todos").assertIsSelected()
        radio("Todas", 0).assertIsSelected()
        radio("Todas", 1).assertIsSelected()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Filtrar apostas").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, clears)
            assertEquals(1, closes)
            visible.value = true
        }
        dismissWithBack()
        compose.onNodeWithText("Filtrar apostas").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, closes)
            assertEquals(1, applied.size)
        }
    }

    @Test fun betFiltersSelectEveryChoiceAndToggleThenClearWithoutApplyingOnCancelOrDismiss() {
        val recent = TicketPeriod(45, description = "Últimos 30 dias")
        val october = TicketPeriod(10, 2026, "Outubro")
        val initial = BetFilters(period = recent)
        val state = mutableStateOf(
            TicketsUiState(
                betConfiguration = ConfirmedBetsConfiguration(
                    periods = listOf(recent, october),
                    statuses = listOf(TicketFilterOption(1, "A conferir"), TicketFilterOption(2, "Conferida")),
                    orderings = listOf(TicketFilterOption(3, "Mais recentes"), TicketFilterOption(2, "Mais antigas")),
                    betTypes = listOf(TicketFilterOption(1, "Individual"), TicketFilterOption(2, "Bolão")),
                    contestTypes = listOf(TicketFilterOption(0, "Regular"), TicketFilterOption(1, "Especial"))
                ),
                modalities = listOf(LotteryModality(2, "Mega-Sena", "megasena"), LotteryModality(3, "Quina", "quina")),
                betFilters = initial
            )
        )
        val visible = mutableStateOf(true)
        val selections = mutableListOf<Pair<String, Any>>()
        val applied = mutableListOf<BetFilters>()
        var clears = 0
        var closes = 0
        compose.setContent {
            CaixaDSTheme {
                if (visible.value) {
                    BetFiltersDialog(
                        state.value,
                        BetFiltersActions(
                            closeBetFilters = {
                                closes++
                                visible.value = false
                            },
                            selectBetPeriod = { value ->
                                selections += "period" to value
                                state.value = state.value.copy(betFilters = state.value.betFilters.copy(period = value))
                            },
                            selectBetStatus = { value ->
                                selections += "status" to value
                                state.value = state.value.copy(betFilters = state.value.betFilters.copy(status = value))
                            },
                            selectBetOrdering = { value ->
                                selections += "ordering" to value
                                state.value = state.value.copy(betFilters = state.value.betFilters.copy(ordering = value))
                            },
                            selectBetType = { value ->
                                selections += "type" to value
                                state.value = state.value.copy(betFilters = state.value.betFilters.copy(betType = value))
                            },
                            selectContestType = { value ->
                                selections += "contest" to value
                                state.value = state.value.copy(betFilters = state.value.betFilters.copy(contestType = value))
                            },
                            selectModality = { value ->
                                selections += "modality" to value
                                state.value = state.value.copy(betFilters = state.value.betFilters.copy(modality = value))
                            },
                            toggleSurprise = {
                                state.value =
                                    state.value.copy(betFilters = state.value.betFilters.copy(surprise = !state.value.betFilters.surprise))
                            },
                            toggleRecurring = {
                                state.value =
                                    state.value.copy(
                                        betFilters = state.value.betFilters.copy(recurring = !state.value.betFilters.recurring)
                                    )
                            },
                            toggleCombo = {
                                state.value =
                                    state.value.copy(betFilters = state.value.betFilters.copy(combo = !state.value.betFilters.combo))
                            },
                            applyBetFilters = {
                                applied += state.value.betFilters
                                visible.value = false
                            },
                            clearBetFilters = {
                                clears++
                                state.value = state.value.copy(betFilters = initial)
                            }
                        )
                    )
                }
            }
        }
        compose.onNodeWithText("Período").assertDoesNotExist()
        compose.onNodeWithText("Individual").assertDoesNotExist()
        listOf("Tipo de aposta", "Modalidades", "Tipo de concurso", "Situação da aposta", "Ordenar por")
            .forEach(::choose)
        chooseWheel("Situação da aposta", "Conferida")
        chooseWheel("Ordenar por", "Mais antigas")
        chooseWheel("Tipo de aposta", "Bolão")
        chooseWheel("Tipo de concurso", "Especial")
        choose("Quina")
        compose.onNodeWithContentDescription(
            "Situação da aposta"
        ).assert(SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "Conferida"))
        compose.onNodeWithContentDescription(
            "Ordenar por"
        ).assert(SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "Mais antigas"))
        compose.onNodeWithContentDescription(
            "Tipo de aposta"
        ).assert(SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "Bolão"))
        compose.onNodeWithContentDescription(
            "Tipo de concurso"
        ).assert(SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "Especial"))
        radio("Quina").assertIsSelected()
        choose("Surpresinha")
        choose("Teimosinha")
        choose("Combos")
        checkbox("Surpresinha").assertIsOn()
        checkbox("Teimosinha").assertIsOn()
        checkbox("Combos").assertIsOn()
        compose.onNodeWithText("OK").performClick()
        compose.runOnIdle {
            assertEquals(
                listOf(
                    "status" to 2L,
                    "ordering" to 2L,
                    "type" to 2L,
                    "contest" to 1L,
                    "modality" to 3
                ),
                selections
            )
            assertEquals(listOf(BetFilters(recent, 2, 2, 2, 1, 3, surprise = true, recurring = true, combo = true)), applied)
            visible.value = true
        }
        listOf("Tipo de aposta", "Modalidades", "Tipo de concurso", "Situação da aposta", "Ordenar por")
            .forEach(::choose)
        compose.onNodeWithText("Limpar").performClick()
        radio("Todas").assertIsSelected()
        checkbox("Surpresinha").assertIsOff()
        checkbox("Teimosinha").assertIsOff()
        checkbox("Combos").assertIsOff()
        choose("Todas")
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Filtrar apostas").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, clears)
            assertEquals(1, closes)
            visible.value = true
        }
        dismissWithBack()
        compose.onNodeWithText("Filtrar apostas").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, closes)
            assertEquals(1, applied.size)
        }
    }

    private fun showPurchaseFilters(
        state: MutableState<TicketsUiState>,
        visible: MutableState<Boolean>,
        methods: MutableList<Long?>,
        statuses: MutableList<Long?>,
        applied: MutableList<PurchaseFilters>,
        onClear: () -> Unit,
        onClose: () -> Unit,
    ) {
        compose.setContent {
            CaixaDSTheme {
                if (visible.value) {
                    PurchaseFiltersDialog(
                        state.value,
                        PurchaseFiltersActions(
                            closePurchaseFilters = {
                                onClose()
                                visible.value = false
                            },
                            selectPaymentMethod = { value ->
                                methods += value
                                state.value = state.value.copy(purchaseFilters = state.value.purchaseFilters.copy(paymentMethodId = value))
                            },
                            selectPurchaseStatus = { value ->
                                statuses += value
                                state.value = state.value.copy(purchaseFilters = state.value.purchaseFilters.copy(statusId = value))
                            },
                            applyPurchaseFilters = {
                                applied += state.value.purchaseFilters
                                visible.value = false
                            },
                            clearPurchaseFilters = {
                                onClear()
                                state.value = state.value.copy(purchaseFilters = PurchaseFilters())
                            }
                        )
                    )
                }
            }
        }
    }

    private fun showPurchaseBetFilters(
        state: MutableState<TicketsUiState>,
        visible: MutableState<Boolean>,
        contests: MutableList<Int?>,
        statuses: MutableList<Long?>,
        modalities: MutableList<Int?>,
        applied: MutableList<PurchaseBetFilters>,
        onClear: () -> Unit,
        onClose: () -> Unit,
    ) {
        compose.setContent {
            CaixaDSTheme {
                if (visible.value) {
                    PurchaseBetFiltersDialog(
                        state.value,
                        PurchaseBetFiltersActions(
                            closePurchaseBetFilters = {
                                onClose()
                                visible.value = false
                            },
                            selectPurchaseBetContest = { value ->
                                contests += value
                                state.value = state.value.copy(purchaseBetFilters = state.value.purchaseBetFilters.copy(contest = value))
                            },
                            selectPurchaseBetStatus = { value ->
                                statuses += value
                                state.value = state.value.copy(purchaseBetFilters = state.value.purchaseBetFilters.copy(statusId = value))
                            },
                            selectPurchaseBetModality = { value ->
                                modalities += value
                                state.value = state.value.copy(purchaseBetFilters = state.value.purchaseBetFilters.copy(modalityId = value))
                            },
                            applyPurchaseBetFilters = {
                                applied += state.value.purchaseBetFilters
                                visible.value = false
                            },
                            clearPurchaseBetFilters = {
                                onClear()
                                state.value = state.value.copy(purchaseBetFilters = PurchaseBetFilters())
                            }
                        )
                    )
                }
            }
        }
    }

    private fun chooseWheel(group: String, option: String) {
        val node = compose.onNodeWithContentDescription(group).performScrollTo()
        val action = node.fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsActions.CustomActions]
            .single { it.label == option }
        compose.runOnIdle { check(action.action()) }
    }

    private fun choose(label: String) {
        compose.onNodeWithText(label).performScrollTo().performClick()
    }

    private fun radio(label: String, index: Int = 0): SemanticsNodeInteraction =
        compose.onAllNodes(isSelectable() and hasAnySibling(hasText(label)), useUnmergedTree = true)[index]

    private fun checkbox(label: String): SemanticsNodeInteraction =
        compose.onNode(isToggleable() and hasAnySibling(hasText(label)), useUnmergedTree = true)

    private fun dismissWithBack() {
        val dialogTitle = hasText("Filtrar compras") or hasText("Filtrar apostas")
        // runOnIdle pode ter reaberto o diálogo: aguarde sua janela antes de enviar a ação Voltar.
        compose.onNode(dialogTitle).assertIsDisplayed()
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodes(dialogTitle).fetchSemanticsNodes().isEmpty()
        }
        compose.waitForIdle()
    }
}
