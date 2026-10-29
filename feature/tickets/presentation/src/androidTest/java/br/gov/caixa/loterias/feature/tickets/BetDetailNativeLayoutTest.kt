package br.gov.caixa.loterias.feature.tickets

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetReceipt
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketAward
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import androidx.compose.ui.graphics.Color
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailRoute
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetSelection

class BetDetailNativeLayoutTest {
    @get:Rule val compose = createComposeRule()
    private val bet = ConfirmedBet(41, "LOTOFACIL", 3804, "14/10/2026", "Não Premiada",
        listOf(2,4,6,8,9,10,11,12,13,14,15,17,20,21,24), modalityId = 8, statusId = 9,
        time = "19:00:16", surprise = true)

    @Test fun footerMatchesNativeHeightAndReceiptRemainsReachable() {
        compose.setContent { CaixaDSTheme { BetDetailRoute(bet, {}, {}, onEdit = {}, onAddToCart = {},
            onDownloadReceipt = {}, cartTotal = BigDecimal("35.00"), cartBetCount = 1,
            drawResults = listOf(draw(listOf(1,2,4,6,8,9,10,11,12,13,14,15,17,20,24)).copy(modality = "LOTOFACIL", contest = 3804))) } }
        compose.onNodeWithText("Favoritar aposta").assertIsDisplayed()
        compose.onNodeWithText("Editar aposta").assertIsDisplayed()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsDisplayed()
        compose.onNodeWithText("Total do carrinho").assertIsDisplayed()
        compose.waitForIdle()
        saveScreenshot("compose-detail-initial.png")
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        val footer = compose.onNodeWithTag("bet-detail-footer").fetchSemanticsNode().boundsInRoot
        assertEquals(172f * density, footer.height, 1f)
        compose.onNodeWithTag("bet-detail-scroll").performScrollToNode(hasText("Gerar comprovante"))
        val receipt = compose.onNodeWithContentDescription("Gerar comprovante").fetchSemanticsNode().boundsInRoot
        assertEquals(246f * density, receipt.width, 1f)
        assertTrue("Receipt never sits behind fixed actions", receipt.bottom <= footer.top)
        saveScreenshot("compose-detail-receipt.png")
    }

    @Test fun receiptActionRemainsReachableForEveryModality() {
        val current = mutableStateOf(bet)
        var downloads = 0
        compose.setContent { CaixaDSTheme { BetDetailRoute(current.value, {}, {}, onDownloadReceipt = { downloads++ }) } }
        val modalities = listOf(2 to "MEGA_SENA", 1 to "QUINA", 8 to "LOTOFACIL", 16 to "LOTOMANIA",
            18 to "DUPLA_SENA", 20 to "TIMEMANIA", 11 to "DIA_DE_SORTE", 7 to "SUPER_7",
            9 to "MAIS_MILIONARIA", 19 to "LOTECA", 14 to "LOTOGOL")
        for ((id, name) in modalities) {
            compose.runOnIdle { current.value = bet.copy(id = id.toLong(), modalityId = id, modality = name) }
            compose.onNodeWithTag("bet-detail-scroll").performScrollToNode(hasText("Gerar comprovante"))
            compose.onNodeWithContentDescription("Gerar comprovante").performClick()
        }
        compose.runOnIdle { assertEquals(modalities.size, downloads) }
    }
    private fun saveScreenshot(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        java.io.File(instrumentation.targetContext.filesDir, name).outputStream().use {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }

    private fun draw(numbers: List<Int>) = DrawResult("SUPER_7", 1, "", "", numbers,
        emptyList(), emptyList(), false, "", "", "", emptyList())

    @Test fun superSevenKeepsRepeatedDigitsInSevenSeparateColumns() {
        val selected = bet.copy(modalityId = 7, modality = "SUPER_7", numbers = List(7) { 1 },
            numberColumns = List(7) { listOf(1) })
        compose.setContent { CaixaDSTheme {
            ConfirmedBetSelection(selected, Color.White, draw(List(7) { 1 }), Color.Black)
        } }
        compose.onNodeWithText("7 números").assertExists()
        compose.onAllNodesWithText("1").assertCountEquals(16)
        compose.onNodeWithText("Suas colunas").assertExists()
    }

    @Test fun recurringContestShowsItsOwnPrizeStatus() {
        val selected = bet.copy(recurringDraws = 2, status = "Premiada",
            receipt = ConfirmedBetReceipt(awards = listOf(CheckedTicketAward(1, "Premiado", value = BigDecimal.TEN))))
        compose.setContent { CaixaDSTheme { BetDetailRoute(selected, {}, {},
            drawResults = listOf(draw(listOf(1)), draw(listOf(2)).copy(contest = 2))) } }
        compose.onNodeWithText("Situação: Premiado").assertExists()
        compose.onNodeWithText("Situação: Não premiado").assertExists()
    }

    @Test fun lotogolShowsActualMatchScores() {
        val result = draw(emptyList()).copy(lotogolMatches = listOf(
            DrawSportsMatch(1, DrawSportsTeam("Casa", score = "2"), DrawSportsTeam("Fora", score = "3"))))
        compose.setContent { CaixaDSTheme {
            ConfirmedBetSelection(bet.copy(modalityId = 14, modality = "LOTOGOL"), Color.White, result, Color.Black)
        } }
        compose.onNodeWithText("1. Casa 2 × 3 Fora").assertExists()
    }

}
