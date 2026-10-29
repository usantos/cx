package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

/** Toda simulação suportada renderiza recursos nativos com dados determinísticos e nenhum envio ao carrinho. */
@RunWith(Parameterized::class)
class BettingModalityRenderingTest(private val id: Int, private val name: String, private val minimum: Int, private val maximum: Int) {
    @get:Rule val compose = createComposeRule()

    @Test fun manualSurpriseAndComplementRenderWithoutSending() {
        var sends = 0
        val team = SportsTeam(1, "Time controlado", "SP")
        val game = LotteryGameParameters(
            modality = LotteryModality(id, name, name), contestNumber = 2900, drawDate = "10/09/2026 20:00:00",
            maximumNumber = maximum, minimumSelection = minimum, maximumSelection = minimum,
            maximumSurprises = 3, recurringOptions = listOf(0,2,4),
            prices = listOf(BetPrice(minimum, if (id == 9) 2 else 0, BigDecimal("5.00"))),
            minimumClovers = if (id == 9) 2 else 0, maximumClovers = if (id == 9) 6 else 0,
            maximumCloverNumber = if (id == 9) 6 else 0,
            teams = if (id == 20) listOf(team) else emptyList(),
            matches = if (id == 19 || id == 14) (1..if(id == 14)5 else 14).map { SportsMatch(it, team, team.copy(number = 2, name = "Outro time")) } else emptyList(),
            luckyMonths = if (id == 11) listOf(LuckyMonth(1, "Janeiro", "JAN")) else emptyList(),
        )
        val vm = BettingGameViewModel(id, GetBettingParametersUseCase { AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game))) }, object : AddBetToCartUseCase {
            override suspend fun invoke(draft: BetDraft): AppResult<ShoppingCart> { sends++; return AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) }
        })
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.waitUntil(10_000) { vm.state.value.game != null }
        compose.onNodeWithText(name.lowercase()).assertIsDisplayed()
        compose.onNodeWithContentDescription("Dúvidas").assertIsDisplayed()
        screenshot("manual")
        if (id != 19 && id != 14) {
            compose.runOnIdle { vm.selectMode(BetEntryMode.SURPRISE) }
            compose.waitForIdle()
            compose.onNodeWithText("Na Surpresinha os números ficam\nmascarados até a efetivação da compra.").assertIsDisplayed()
            screenshot("surprise")
            compose.runOnIdle { vm.selectMode(BetEntryMode.MANUAL) }
        }
        compose.runOnIdle {
            when(id) {
                7 -> repeat(7) { vm.toggleSuperSevenDigit(it,it) }
                19 -> game.matches.forEach { vm.toggleLotecaOutcome(it.number,LotecaOutcome.HOME) }
                14 -> game.matches.forEach { vm.selectLotogolScore(it.number,true,1);vm.selectLotogolScore(it.number,false,0) }
                else -> (1..minimum).forEach(vm::toggleNumber)
            }
        }
        screenshot("selected")
        if (id in listOf(9,11,20)) {
            compose.runOnIdle {
                vm.primaryAction()
            }
            compose.waitForIdle()
            if (id == 11) compose.onNodeWithContentDescription("Janeiro").assertIsDisplayed()
            else if(id == 20) compose.onNodeWithContentDescription("Nome do time").assertIsDisplayed()
            else compose.onNodeWithText(when(id) { 9 -> "Escolha de 2 a 6 trevos \npara marcar nesta aposta"; 11 -> "Escolha o mês da sorte"; else -> "Nome do time" }).assertIsDisplayed()
        }
        if(id in listOf(9,11,20)) screenshot("complement")
        compose.runOnIdle { assertEquals(0, sends) }
    }

    private fun screenshot(state: String) {
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val folder = java.io.File(requireNotNull(instrumentation.targetContext.externalCacheDir),"codex-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        java.io.File(folder,"betting-$id-$state.png").outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }
        bitmap.recycle()
        val resources=instrumentation.targetContext.resources
        java.io.File(folder,"betting-$id-$state.txt").writeText("fixture=true\nnetworkSends=0\nmodalityId=$id\nstate=$state\ndensityDpi=${resources.displayMetrics.densityDpi}\nfontScale=${resources.configuration.fontScale}\n")
        org.junit.Assert.assertTrue("PNG artifact missing",java.io.File(folder,"betting-$id-$state.png").length()>0)
        org.junit.Assert.assertTrue("Metadata artifact missing",java.io.File(folder,"betting-$id-$state.txt").length()>0)

    }

    companion object {
        @JvmStatic @Parameterized.Parameters(name = "{1}") fun modalities(): List<Array<Any>> = listOf(
            arrayOf(1,"Quina",5,80), arrayOf(2,"Mega-Sena",6,60), arrayOf(5,"Lotomania",50,100),
            arrayOf(8,"Lotofácil",15,25), arrayOf(18,"Dupla Sena",6,50), arrayOf(20,"Timemania",10,80),
            arrayOf(11,"Dia de Sorte",7,31), arrayOf(7,"Super Sete",7,9), arrayOf(9,"+Milionária",6,50),
            arrayOf(19,"Loteca",14,14), arrayOf(14,"Lotogol",5,5),
        )
    }
}
