package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.io.File
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Same public contest/prices as the installed screenshot; all services are local fakes. */
class InstalledMegaPairTest {
    @get:Rule val compose = createComposeRule()

    @Test fun publicContest3067ManualQuantityRecurrenceAndHelpWithoutSubmission() {
        val sends = AtomicInteger(0)
        val prices = (6..20).map { n -> BetPrice(n,0,BigDecimal.valueOf(combinations(n,6)*6)) }
        assertEquals(BigDecimal("6"),prices[0].amount)
        assertEquals(BigDecimal("42"),prices[1].amount)
        assertEquals(BigDecimal("168"),prices[2].amount)
        assertEquals(BigDecimal("504"),prices[3].amount)
        assertEquals(BigDecimal("1260"),prices[4].amount)
        assertEquals(BigDecimal("2772"),prices[5].amount)
        val game = LotteryGameParameters(
            modality = LotteryModality(2,"Mega-Sena","MEGA_SENA"),contestNumber=3067,drawDate="06/10/2026",
            maximumNumber=60,minimumSelection=6,maximumSelection=20,maximumSurprises=100,
            recurringOptions=listOf(0,2,3,4,6,8,9,12),prices=prices,
        )
        val vm = BettingGameViewModel(2,GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game)))
        },object:AddBetToCartUseCase {
            override suspend fun invoke(draft:BetDraft):AppResult<ShoppingCart> { sends.incrementAndGet();return AppResult.Success(ShoppingCart(1,BigDecimal.ZERO,emptyList())) }
        })
        compose.setContent { CaixaDSTheme(darkTheme=false) { BettingGameRoute(vm,{}, {},cartTotal=BigDecimal.ZERO,cartBetCount=0) } }
        compose.waitUntil(10_000) { vm.state.value.game != null }
        compose.onNodeWithText("3067").assertIsDisplayed()
        compose.onNodeWithText("06/10").assertIsDisplayed()
        capture("manual",vm)
        compose.onNodeWithText("6 números").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("betting-option-list").assertIsDisplayed()
        compose.runOnIdle { assertEquals(15,vm.state.value.numberQuantityOptions.size) }
        capture("quantity",vm)
        compose.onNodeWithTag("betting-option-cancel").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(6,vm.state.value.numberQuantity) }
        compose.onNodeWithText("Teimosinha").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("betting-option-wheel").assertIsDisplayed()
        compose.onNodeWithTag("betting-option-item-0").assertIsDisplayed()
        capture("recurrence",vm,"recurrenceOptions=${game.recurringOptions}\n")
        compose.onNodeWithTag("betting-option-cancel").performClick()
        compose.waitForIdle()
        assertTrue(legacyBettingHelp(2).isNotBlank())
        compose.onNodeWithText("Como jogar").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Como Jogar: mega-sena").assertIsDisplayed()
        capture("help",vm)
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(0,sends.get());assertEquals(0,vm.state.value.recurringDraws);assertEquals(6,vm.state.value.numberQuantity) }
    }

    private fun capture(state:String,vm:BettingGameViewModel,additional:String="") {
        compose.waitForIdle()
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        // Compose idle does not include the native Window enter/fade animation.
        instrumentation.waitForIdleSync()
        android.os.SystemClock.sleep(300)
        instrumentation.waitForIdleSync()
        val folder=File(requireNotNull(instrumentation.targetContext.externalCacheDir),"codex-parity").apply { mkdirs() }
        val name="compose-mega-$state"
        val bitmap=requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(folder,"$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) };bitmap.recycle()
        val resources=instrumentation.targetContext.resources
        compose.onNodeWithContentDescription("Dúvidas").assertIsDisplayed()
        val headerBounds = compose.onNodeWithContentDescription("Dúvidas").fetchSemanticsNode().boundsInRoot
        val headerEvidence="headerBounds=$headerBounds\n"
        File(folder,"$name.txt").writeText("fixture=true\nnetworkSends=0\ncontest=3067\ndrawDate=06/10/2026\nnumberQuantity=${vm.state.value.numberQuantity}\nrecurringDraws=${vm.state.value.recurringDraws}\ncartTotal=0\ndensityDpi=${resources.displayMetrics.densityDpi}\nfontScale=${resources.configuration.fontScale}\n$headerEvidence$additional")
        assertTrue("PNG artifact missing",File(folder,"$name.png").length()>0)
        assertTrue("Metadata artifact missing",File(folder,"$name.txt").length()>0)

    }
    private fun combinations(n:Int,k:Int):Long { var result=1L;(1..k).forEach { result=result*(n-k+it)/it };return result }
}
