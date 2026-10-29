// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDataCard
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyConfirmedBetRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseStatusDialogView
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsListActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.LegacyPurchaseDetailHeader
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesListActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseBetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseDialogAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseStatusDialog
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Transições de estado são renderizadas com semântica real do Compose e ações do usuário. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TicketsContentStateUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun manualQueryPrioritizesLoadingThenErrorThenEmptyThenBets() {
        val state = mutableStateOf(TicketsUiState(isLoading = true, errorMessage = "Falha de consulta"))
        show { ManualTicketContent(state.value, {}, {}, false, ManualTicketActions({}, {}, {})) }
        compose.onNodeWithText("Falha de consulta").assertDoesNotExist()
        compose.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)).assertExists()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false) }
        compose.onNodeWithText("Falha de consulta").assertExists()
        compose.runOnIdle { state.value = TicketsUiState(loaded = true) }
        compose.onNodeWithText("Nenhuma aposta encontrada neste bilhete.").assertExists()
        compose.runOnIdle { state.value = TicketsUiState(loaded = true, bets = listOf(bet())) }
        compose.onNodeWithText("Mega-Sena • Concurso 123").assertExists()
        compose.onNodeWithText("Nenhuma aposta encontrada neste bilhete.").assertDoesNotExist()
    }

    @Test fun manualHelpAndValidCodeDispatchActions() {
        var help = 0
        var checks = 0
        val codes = mutableListOf<String>()
        val retailers = mutableListOf<String>()
        val state = mutableStateOf(TicketsUiState(ticketCode = "abcd123456789012345678901", ticketRetailerCode = "123456789"))
        show {
            ManualTicketContent(
                state.value,
                {},
                { help++ },
                true,
                ManualTicketActions(
                    { value ->
                        codes.add(value)
                        state.value = state.value.copy(ticketCode = value.filter(Char::isLetterOrDigit))
                    },
                    { value ->
                        retailers.add(value)
                        state.value =
                            state.value.copy(ticketRetailerCode = value.filter(Char::isLetterOrDigit))
                    },
                    { checks++ }
                )
            )
        }
        compose.onAllNodes(hasSetTextAction())[0].assertTextEquals("ABCD-12345678901234567-89")
        compose.onAllNodes(hasSetTextAction())[1].assertTextEquals("12.345678-9")
        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("WXYZ-12345678901234567-89")
        compose.onAllNodes(hasSetTextAction())[1].performTextReplacement("98.765432-1")
        compose.onAllNodes(hasSetTextAction())[0].assertTextEquals("WXYZ-12345678901234567-89")
        compose.onAllNodes(hasSetTextAction())[1].assertTextEquals("98.765432-1")
        compose.onNodeWithContentDescription("Termos de uso").performClick()
        compose.onNodeWithText("Continuar").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(listOf("WXYZ-12345678901234567-89"), codes)
            assertEquals(listOf("98.765432-1"), retailers)
            assertEquals(1, help)
            assertEquals(1, checks)
        }
    }

    @Test fun purchasesShowRetryAndReplaceFailureWithEmptyState() {
        var retries = 0
        val state = mutableStateOf(TicketsUiState(errorMessage = "Servidor indisponível"))
        show { PurchasesContent(state.value, {}, {}, PurchasesListActions({}, {}, { retries++ }, {})) }
        compose.onNodeWithText("Servidor indisponível").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            assertEquals(1, retries)
            state.value = TicketsUiState(loaded = true)
        }
        compose.onNodeWithText("Você ainda não possui compras neste mês.").assertExists()
        compose.onNodeWithText("Servidor indisponível").assertDoesNotExist()
    }

    @Test fun purchaseListDispatchesSelectedPurchaseAndLoadsNextPage() {
        val selected = mutableListOf<Purchase>()
        var pages = 0
        val purchase = purchase()
        show {
            PurchasesContent(
                TicketsUiState(purchases = listOf(purchase), canLoadMore = true),
                {},
                selected::add,
                PurchasesListActions({}, {}, {}, { pages++ })
            )
        }
        compose.onNodeWithText("Situação: Efetivada").performClick()
        compose.runOnIdle {
            assertEquals(listOf(purchase), selected)
            assertEquals(1, pages)
        }
    }

    @Test fun confirmedBetsVisitorUsesDefaultPeriodAndCanBetFromEmptyState() {
        var bets = 0
        var filters = 0
        var help = 0
        show {
            ConfirmedBetsContent(
                TicketsUiState(loaded = true), {}, {}, null, { bets++ }, { help++ }, BigDecimal.ZERO, 0, null,
                false, ConfirmedBetsListActions({ filters++ }, {}, {})
            )
        }
        compose.onNodeWithText("Últimos 7 dias").assertExists()
        compose.onNodeWithText("Aposte aqui").performClick()
        compose.onNodeWithContentDescription("Filtrar").performClick()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.runOnIdle {
            assertEquals(1, bets)
            assertEquals(1, filters)
            assertEquals(1, help)
        }
    }

    @Test fun confirmedBetsErrorCanRetryWithoutOfferingEmptyStateAction() {
        var retries = 0
        show {
            ConfirmedBetsContent(
                TicketsUiState(errorMessage = "Erro no histórico"), {}, {}, null, null, null,
                BigDecimal.ZERO, 0, null, true, ConfirmedBetsListActions({}, { retries++ }, {})
            )
        }
        compose.onNodeWithText("Erro no histórico").assertExists()
        compose.onNodeWithText("Aposte aqui").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").performScrollTo().assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun purchaseDetailWaitsForMatchingPurchaseAndRetryKeepsRequestedPurchase() {
        val requested = purchase()
        val state = mutableStateOf(TicketsUiState(selectedPurchase = requested.copy(id = 99), errorMessage = "Falhou"))
        val loads = mutableListOf<Purchase>()
        show { PurchaseDetailContent(state.value, requested, {}, detailActions(load = loads::add)) }
        compose.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)).assertExists()
        compose.onNodeWithText("Falhou").assertDoesNotExist()
        compose.runOnIdle { state.value = state.value.copy(selectedPurchase = requested) }
        compose.onNodeWithText("Falhou").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(listOf(requested), loads) }
    }

    @Test fun purchaseDetailFilteredEmptyResultCanClearFiltersAndOpenToolbarActions() {
        var clear = 0
        var filters = 0
        var help = 0
        val purchase = purchase()
        show {
            PurchaseDetailContent(
                TicketsUiState(selectedPurchase = purchase, bets = listOf(bet()), purchaseBetFilters = PurchaseBetFilters(contest = 999)),
                purchase,
                {},
                detailActions(clear = { clear++ }, filters = { filters++ }, help = { help++ })
            )
        }
        compose.onNodeWithText("Nenhuma aposta encontrada").assertExists()
        compose.onNodeWithText("Limpar filtros").performClick()
        compose.onNodeWithContentDescription("Filtrar").performClick()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.runOnIdle {
            assertEquals(1, clear)
            assertEquals(1, filters)
            assertEquals(1, help)
        }
    }

    @Test fun purchaseDetailRendersNumericRowThenFallsBackForSurpriseAndHidesFavoriteForPool() {
        val purchase = purchase()
        val state = mutableStateOf(TicketsUiState(selectedPurchase = purchase, bets = listOf(bet().copy(recurringDraws = 2))))
        show { PurchaseDetailContent(state.value, purchase, {}, detailActions()) }
        compose.onNodeWithText("Seus números").assertExists()
        compose.onNodeWithText("2 Teimosinhas").assertExists()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertExists()
        compose.runOnIdle { state.value = state.value.copy(bets = listOf(bet().copy(surprise = true, poolShare = true))) }
        compose.onNodeWithText("Mega-Sena • Concurso 123").assertExists()
        compose.onNodeWithText("Seus números").assertDoesNotExist()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertDoesNotExist()
    }

    @Test fun purchaseHeaderIncludesSettledPixIdentifierAndFavoriteCallback() {
        var favorites = 0
        show {
            LegacyPurchaseDetailHeader(
                purchase().copy(paymentMethodId = 6, endToEndId = "E123", finalizedTime = "13:14"),
                androidx.compose.ui.graphics.Color.Blue,
                true,
                { favorites++ }
            )
        }
        compose.onNodeWithText("ID do Pagamento:\nE123").assertExists()
        compose.onNodeWithText("Hora: 13:14").assertExists()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").performClick()
        compose.runOnIdle { assertEquals(1, favorites) }
    }

    @Test fun legacyHistoryStatusCodesRetainTheirCustomerLabels() {
        val current = mutableStateOf(bet())
        show { LegacyConfirmedBetRow(current.value, {}, null) }
        val labels = mapOf(
            100L to "Premiada",
            11L to "Prêmio pago",
            9L to "Aposta não premiada",
            101L to "Concurso não apurado",
            102L to "Aposta não premiada concorrendo em concursos futuros",
            10L to "Aposta prescrita",
            4L to "Aposta efetivada",
            999L to "Efetivada"
        )
        labels.forEach { (code, label) ->
            compose.runOnIdle { current.value = bet().copy(statusId = code) }
            compose.onNodeWithText("situação:\u00a0${label.lowercase(java.util.Locale.forLanguageTag("pt-BR"))}").assertExists()
        }
    }

    @Test fun legacyHistoryAllowsRepeatOnlyForNonPoolNonLotecaBets() {
        var opens = 0
        var adds = 0
        val current = mutableStateOf(bet())
        show { LegacyConfirmedBetRow(current.value, { opens++ }, { adds++ }) }
        compose.onNodeWithContentDescription("Detalhe da aposta").performClick()
        compose.onNodeWithContentDescription("Adicionar ao carrinho").performClick()
        compose.runOnIdle {
            assertEquals(1, opens)
            assertEquals(1, adds)
            current.value = bet().copy(poolShare = true)
        }
        compose.onNodeWithContentDescription("Adicionar ao carrinho").assertDoesNotExist()
        compose.runOnIdle { current.value = bet().copy(modalityId = 19) }
        compose.onNodeWithContentDescription("Adicionar ao carrinho").assertDoesNotExist()
    }

    @Test fun favoritePurchaseShowsSurpriseOptionAndDispatchesEditedNameAndSave() {
        val names = mutableListOf<String>()
        var toggles = 0
        var saves = 0
        var closes = 0
        show {
            FavoritePurchaseDialog(
                TicketsUiState(bets = listOf(bet().copy(surprise = true)), errorMessage = "Nome obrigatório"),
                FavoritePurchaseActions({ closes++ }, names::add, { toggles++ }, { saves++ })
            )
        }
        compose.onNodeWithText("Nome obrigatório").assertExists()
        compose.onNode(hasSetTextAction()).performTextInput("Jogos de sábado")
        compose.onNodeWithText("Manter surpresinhas").performClick()
        compose.onNodeWithText("OK").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(listOf("Jogos de sábado"), names)
            assertEquals(1, toggles)
            assertEquals(1, saves)
            assertEquals(1, closes)
        }
    }

    @Test fun favoritePurchaseOmitsSurpriseOptionForOrdinaryBet() {
        show {
            FavoritePurchaseDialog(
                TicketsUiState(favoriteName = "Existente", bets = listOf(bet())),
                FavoritePurchaseActions({
                }, {}, {}, {})
            )
        }
        compose.onNode(hasSetTextAction()).assertTextEquals("Existente")
        compose.onNodeWithText("Nome do carrinho").assertDoesNotExist()
        compose.onNodeWithText("Manter surpresinhas").assertDoesNotExist()
    }

    @Test fun purchaseDialogDispatchesEachConfiguredAction() {
        val actions = mutableListOf<PurchaseDialogAction>()
        show {
            PurchaseStatusDialogView(
                PurchaseStatusDialog(
                    "Compra",
                    "Escolha uma ação",
                    listOf(
                        PurchaseDialogAction.NEW_PIX to "Gerar Pix",
                        PurchaseDialogAction.CHANGE_PAYMENT to "Trocar pagamento",
                        PurchaseDialogAction.HOME to "Início"
                    )
                ),
                actions::add
            )
        }
        compose.onNodeWithText("Gerar Pix").performClick()
        compose.onNodeWithText("Trocar pagamento").performClick()
        compose.onNodeWithText("Início").performClick()
        compose.runOnIdle {
            assertEquals(listOf(PurchaseDialogAction.NEW_PIX, PurchaseDialogAction.CHANGE_PAYMENT, PurchaseDialogAction.HOME), actions)
        }
    }

    @Test fun purchaseDialogWithNoActionsDoesNotRender() {
        show { PurchaseStatusDialogView(PurchaseStatusDialog("Compra ausente", "Mensagem", emptyList()), {}) }
        compose.onNodeWithText("Compra ausente").assertDoesNotExist()
    }

    @Test fun betCardUsesFallbackLabelsAndReportsOpen() {
        var opens = 0
        show { BetDataCard(bet().copy(modality = "", status = "", prize = BigDecimal.TEN)) { opens++ } }
        compose.onNodeWithText("Aposta • Concurso 123").performClick()
        compose.onNodeWithText("Em acompanhamento").assertExists()
        compose.onNodeWithText("01  02  03  04  05  06").assertExists()
        compose.runOnIdle { assertEquals(1, opens) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { Column { content() } } }
    }

    private fun detailActions(load: (Purchase) -> Unit = {}, clear: () -> Unit = {}, filters: () -> Unit = {}, help: (() -> Unit)? = null) =
        PurchaseDetailActions(filters, load, {}, { 0L }, {}, clear, help)

    private fun purchase() = Purchase(1, "22/10/2026", "Efetivada", "Pix", BigDecimal("12.50"), statusId = 6)
    private fun bet() = ConfirmedBet(1, "Mega-Sena", 123, "22/10/2026", "Efetivada", listOf(1, 2, 3, 4, 5, 6), modalityId = 2)
}
