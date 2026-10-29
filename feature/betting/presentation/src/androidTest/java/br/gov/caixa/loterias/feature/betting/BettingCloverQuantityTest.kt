package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

class BettingCloverQuantityTest {
    @get:Rule val compose=createComposeRule()

    @Test fun plusSurpriseNumberCounterChangesNumbersWithoutChangingGameQuantity() {
        val game=LotteryGameParameters(LotteryModality(9,"+Milionária","MAIS_MILIONARIA"),396,"07/10/2026",50,6,7,100,listOf(0),
            listOf(BetPrice(6,2,BigDecimal("6")),BetPrice(7,2,BigDecimal("42"))),minimumClovers=2,maximumClovers=6,maximumCloverNumber=6)
        val parameters=BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game))
        val vm=BettingGameViewModel(9,GetBettingParametersUseCase {error("Prepared snapshot must avoid fetch")},
            AddBetToCartUseCase {error("No cart submission permitted")},initialParameters=parameters)
        vm.selectMode(BetEntryMode.SURPRISE)
        compose.setContent {CaixaDSTheme(darkTheme=false) {BettingGameRoute(vm,{}, {})}}
        compose.onNodeWithText("Escolha a quantidade de números").assertIsDisplayed()
        val plus=compose.onAllNodesWithText("+", useUnmergedTree=true)
        plus.assertCountEquals(2)
        plus[0].performClick()
        compose.runOnIdle {
            assertEquals(7,vm.state.value.numberQuantity)
            assertEquals(1,vm.state.value.surpriseQuantity)
            assertEquals(BigDecimal("42"),vm.state.value.unitPrice)
        }
    }

    @Test fun cloverSelectorUsesTwoThroughSixWithPriceAndRequiresChosenCount() {
        val prices=listOf(2 to "6",3 to "18",4 to "36",5 to "60",6 to "90").map {BetPrice(6,it.first,BigDecimal(it.second))}
        val game=LotteryGameParameters(LotteryModality(9,"+Milionária","MAIS_MILIONARIA"),396,"07/10/2026",50,6,6,100,listOf(0),prices,minimumClovers=2,maximumClovers=6,maximumCloverNumber=6)
        val vm=BettingGameViewModel(9,GetBettingParametersUseCase {AppResult.Success(BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game)))},object:AddBetToCartUseCase {
            override suspend fun invoke(draft:BetDraft):AppResult<ShoppingCart> = error("No cart submission permitted")
        })
        compose.setContent {CaixaDSTheme(darkTheme=false) {BettingGameRoute(vm,{}, {})}}
        compose.waitUntil(10_000) {vm.state.value.game!=null}
        compose.runOnIdle {(1..6).forEach(vm::toggleNumber)}
        compose.onNodeWithText("Completar jogo").assertIsNotEnabled()
        vm.primaryAction()
        compose.onNodeWithText("2 trevos").performClick()
        compose.onNodeWithText("4 trevos por R$ 36,00").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Você aumentou a quantidade de trevos, o valor será alterado. Fique atento ao valor final da sua aposta.").assertIsDisplayed().performClick()
        assertEquals(BigDecimal("36"),vm.state.value.unitPrice)
        (1..3).forEach { compose.onNodeWithTag("betting-clover-$it").performClick() }
        assertTrue(!vm.state.value.canAddToCart)
        compose.onNodeWithTag("betting-clover-4").performClick()
        assertTrue(vm.state.value.canAddToCart)
        compose.onNodeWithTag("betting-clover-5").performClick()
        assertEquals(setOf(1,2,3,4),vm.state.value.selectedClovers)
        compose.onNodeWithTag("betting-clover-5").assertIsNotSelected()
        (1..4).forEach { compose.onNodeWithTag("betting-clover-$it").assertIsSelected() }
    }
}
