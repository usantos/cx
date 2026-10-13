package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Dia1316 public Jan selection, observed on installed1.24.0.
 * Catches native labels drawn at the cell's left edge despite declared textAlignmentCENTER.
 * Installed item_retanglulo_texto is match_parent + textAlignment4; no explicit gravity.
 */
class BettingLuckyMonthAlignmentTest {
    @get:Rule val compose = createComposeRule()

    @Test fun selectedAndUnselectedMonthLabelsDrawAtTheirCellCenters() {
        val names = listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
        val short = listOf("JAN","FEV","MAR","ABR","MAI","JUN","JUL","AGO","SET","OUT","NOV","DEZ")
        val months = names.mapIndexed { index, name -> LuckyMonth(index+1,name,short[index]) }
        val amounts = listOf("2.5","20","90","300","825","1980","4290","8580","16087.5")
        val game = LotteryGameParameters(LotteryModality(11,"Dia de Sorte","DIA_DE_SORTE"),1316,"06/10/2026",31,7,15,100,listOf(0),
            amounts.mapIndexed { index, amount -> BetPrice(index+7,0,BigDecimal(amount)) },luckyMonths=months)
        val vm = BettingGameViewModel(11,GetBettingParametersUseCase { error("Prepared snapshot must avoid network") },
            AddBetToCartUseCase { error("Month alignment must never submit") },
            initialParameters=BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game)))
        vm.state.value.game ?: error("Prepared public game must render immediately")
        (1..7).forEach(vm::toggleNumber)
        vm.primaryAction()
        vm.selectLuckyMonth(months.first())
        compose.setContent { CaixaDSTheme(darkTheme=false) { BettingGameRoute(vm,{}, {}) } }
        compose.onNodeWithTag("lucky-month-Janeiro").assertIsSelected()
        names.forEach { label ->
            val cell = compose.onNodeWithTag("lucky-month-$label").fetchSemanticsNode().boundsInRoot
            val text = compose.onNodeWithText(label, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertEquals("Compose text stays centered in the $label cell", cell.center.x, text.center.x, 1f)
        }
        compose.onNodeWithText("Fevereiro").performClick()
        compose.onNodeWithTag("lucky-month-Fevereiro").assertIsSelected()
        compose.onNodeWithTag("lucky-month-Janeiro").assertIsNotSelected()
        assertEquals(2,vm.state.value.selectedLuckyMonth?.number)
        assertEquals("2.5",vm.state.value.unitPrice.stripTrailingZeros().toPlainString())
    }

    @Test fun diaHelpUsesTheInstalledLowercaseModalityTitle() {
        val game = LotteryGameParameters(LotteryModality(11,"Dia de Sorte","DIA_DE_SORTE"),1316,"06/10/2026",31,7,15,100,listOf(0),
            listOf(BetPrice(7,0,BigDecimal("2.5"))))
        val vm = BettingGameViewModel(11,GetBettingParametersUseCase { error("Prepared snapshot must avoid network") },
            AddBetToCartUseCase { error("Help title must never submit") },
            initialParameters=BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game)))
        compose.setContent { CaixaDSTheme(darkTheme=false) { BettingGameRoute(vm,{}, {}) } }
        compose.onNodeWithText("Como jogar").performClick()
        compose.onNodeWithText("Como Jogar: dia de sorte").assertIsDisplayed()
    }
}
