package br.gov.caixa.loterias.feature.tickets

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.bottom
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.height
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.top
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRuleV2
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketAward
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailRouteDelivered
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetSelectionDelivered
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BetDetailNativeLayoutTest {
    @get:Rule val compose = createFeatureComposeRuleV2()
    private val bet = ConfirmedBet(
        41, "LOTOFACIL", 3804, "14/10/2026", "Não Premiada",
        listOf(2, 4, 6, 8, 9, 10, 11, 12, 13, 14, 15, 17, 20, 21, 24), modalityId = 8, statusId = 9,
        time = "19:00:16", surprise = true
    )

    @Test fun footerMatchesNativeHeightAndReceiptRemainsReachable() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                BetDetailRouteDelivered(
                    bet, {}, {}, onEdit = {}, onAddToCart = {},
                    onDownloadReceipt = {}, cartTotal = BigDecimal("35.00"), cartBetCount = 1,
                    drawResults = listOf(
                        draw(listOf(1, 2, 4, 6, 8, 9, 10, 11, 12, 13, 14, 15, 17, 20, 24)).copy(modality = "LOTOFACIL", contest = 3804)
                    )
                )
            }
        }
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
        compose.setFeatureContent {
            LoteriasCaixaDSTheme { BetDetailRouteDelivered(current.value, {}, {}, onDownloadReceipt = { downloads++ }) }
        }
        val modalities = listOf(
            2 to "MEGA_SENA", 1 to "QUINA", 8 to "LOTOFACIL", 16 to "LOTOMANIA",
            18 to "DUPLA_SENA", 20 to "TIMEMANIA", 11 to "DIA_DE_SORTE", 7 to "SUPER_7",
            9 to "MAIS_MILIONARIA", 19 to "LOTECA", 14 to "LOTOGOL"
        )
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

    private fun draw(numbers: List<Int>) = DrawResult(
        "SUPER_7", 1, "", "", numbers,
        emptyList(), emptyList(), false, "", "", "", emptyList()
    )

    @Test fun superSevenKeepsRepeatedDigitsInSevenSeparateColumns() {
        val selected = bet.copy(
            modalityId = 7,
            modality = "SUPER_7",
            numbers = List(7) { 1 },
            numberColumns = List(7) { listOf(1) }
        )
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                ConfirmedBetSelectionDelivered(selected, Color.White, draw(List(7) { 1 }), Color.Black)
            }
        }
        compose.onNodeWithText("7 números").assertExists()
        // Cada grupo contém sete dígitos e o cabeçalho da coluna 1; a árvore mesclada reúne esses dois textos.
        compose.onAllNodesWithText("1", useUnmergedTree = true).assertCountEquals(16)
        for (index in 1..7) {
            for (description in listOf("Coluna $index: 1", "Coluna $index: 1 acertou")) {
                compose.onNodeWithContentDescription(description).assertExists()
                val column = hasAnyAncestor(hasContentDescription(description))
                compose.onAllNodes(hasText("1") and column, useUnmergedTree = true)
                    .assertCountEquals(if (index == 1) 2 else 1)
                compose.onAllNodes(hasText(index.toString()) and column, useUnmergedTree = true)
                    .assertCountEquals(if (index == 1) 2 else 1)
            }
        }
        compose.onNodeWithText("Suas colunas").assertExists()
    }

    @Test fun recurringContestShowsItsOwnPrizeStatus() {
        val selected = bet.copy(
            recurringDraws = 2,
            status = "Premiada",
            receipt = ConfirmedBetReceipt(awards = listOf(CheckedTicketAward(1, "Premiado", value = BigDecimal.TEN)))
        )
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                BetDetailRouteDelivered(
                    selected,
                    {},
                    {},
                    drawResults = listOf(draw(listOf(1)), draw(listOf(2)).copy(contest = 2))
                )
            }
        }
        compose.onNodeWithText("Situação: Premiado").assertExists()
        compose.onNodeWithText("Situação: Não premiado").assertExists()
    }

    @Test fun lotogolShowsActualMatchScores() {
        val result = draw(emptyList()).copy(
            lotogolMatches = listOf(
                DrawSportsMatch(1, DrawSportsTeam("Casa", score = "2"), DrawSportsTeam("Fora", score = "3"))
            )
        )
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                ConfirmedBetSelectionDelivered(bet.copy(modalityId = 14, modality = "LOTOGOL"), Color.White, result, Color.Black)
            }
        }
        compose.onNodeWithText("1. Casa 2 × 3 Fora").assertExists()
    }
}
