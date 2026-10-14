package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.io.File
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Geometry oracle: installed CX XML at 1280x2856, 480dpi, fontScale=1; no service calls. */
class BettingLotecaParityTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var vm: BettingGameViewModel
    private var sends = 0

    @Test fun quantityCancelConfirmCountersReductionClearAndContinueThroughRealTouches() {
        launch()
        compose.onNodeWithText("15 palpites").assertIsDisplayed().performClick()
        compose.onNodeWithText("Quantidade de Palpites").assertIsDisplayed()
        compose.onNodeWithText("Escolha em quantos palpites você quer jogar:").assertIsDisplayed()
        compose.onNodeWithTag("betting-option-confirm").assertIsNotEnabled()
        compose.onNodeWithTag("betting-option-confirm").assertNativeDisabledConfirmPixels()
        compose.onNodeWithTag("betting-option-item-1").assertNativeUnchosenRowPixels()
        compose.onNodeWithTag("betting-option-item-0").assertTextEquals("15 palpites")
        compose.onNodeWithTag("betting-option-item-1").assertTextEquals("16 palpites")
        compose.onNodeWithTag("betting-option-item-2").assertTextEquals("17 palpites")
        capture("quantity")
        compose.onNodeWithTag("betting-option-item-1").performClick()
        compose.onNodeWithTag("betting-option-cancel").performClick()
        compose.onNodeWithText("Selecionados: 0/15").assertIsDisplayed()
        compose.onNodeWithText("15 palpites").performClick()
        compose.onNodeWithTag("betting-option-item-1").performClick()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.onNodeWithText("Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta.").assertIsDisplayed()
        capture("16-confirmed")
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithTag("loteca-1-HOME").performClick()
        compose.onNodeWithTag("loteca-1-DRAW").performClick()
        compose.onNodeWithText("Selecionados: 2/16").assertIsDisplayed()
        compose.onNodeWithTag("loteca-count-partidas").assertTextEquals("1")
        compose.onNodeWithTag("loteca-count-duplas").assertTextEquals("1")
        capture("first-double")
        compose.onNodeWithTag("loteca-1-AWAY").performClick()
        compose.onNodeWithText("Selecionados: 3/16").assertIsDisplayed()
        compose.onNodeWithTag("loteca-count-triplas").assertTextEquals("1")
        compose.onNodeWithText("16 palpites").performClick()
        compose.onNodeWithTag("betting-option-item-2").performClick()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.onNodeWithText("Selecionados: 3/17").assertIsDisplayed()
        compose.onNodeWithText("17 palpites").performClick()
        compose.onNodeWithTag("betting-option-item-0").performClick()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.onNodeWithText("Selecionados: 0/15").assertIsDisplayed()
        capture("reduction-clears")
        compose.onNodeWithTag("loteca-1-HOME").performClick()
        compose.onNodeWithText("Limpar").performClick()
        compose.onNodeWithText("Selecionados: 0/15").assertIsDisplayed()
        compose.onNodeWithTag("loteca-count-partidas").assertTextEquals("0")
        compose.onNodeWithTag("loteca-2-AWAY").performClick()
        compose.onNodeWithText("Selecionados: 1/15").assertIsDisplayed()
        capture("clear-continue")
        compose.runOnIdle { assertEquals(0,sends); assertEquals(setOf(LotecaOutcome.AWAY),vm.state.value.lotecaOutcomes[2]) }
    }

    @Test fun installedToolbarSelectorFirstMatchAndEqualCardGeometry() {
        launch()
        capture("manual")
        val title = compose.onNodeWithText("Suas apostas").fetchSemanticsNode().boundsInRoot
        val button = compose.onNodeWithTag("loteca-quantity").fetchSemanticsNode().boundsInRoot
        val match = compose.onNodeWithText("Jogo 01").fetchSemanticsNode().boundsInRoot
        val home = compose.onNodeWithTag("loteca-1-HOME").fetchSemanticsNode().boundsInRoot
        val draw = compose.onNodeWithTag("loteca-1-DRAW").fetchSemanticsNode().boundsInRoot
        val away = compose.onNodeWithTag("loteca-1-AWAY").fetchSemanticsNode().boundsInRoot
        // Differences measured from the native hierarchy, independent of Compose implementation.
        assertEquals(with(compose.density) { (271f/3).dp.toPx() }, button.top-title.top, 3f)
        assertEquals(with(compose.density) { (478f/3).dp.toPx() }, match.top-title.top, 3f)
        assertEquals(with(compose.density) { 8.dp.toPx() }, home.top-match.bottom, 2f)
        assertEquals(with(compose.density) { (303f/3).dp.toPx() }, home.height, 3f)
        assertEquals(home.height,draw.height,1f); assertEquals(home.height,away.height,1f)
        assertEquals(home.right,draw.left,1f); assertEquals(draw.right,away.left,1f)
        compose.onAllNodesWithText("/BA").onFirst().assertIsDisplayed(); compose.onAllNodesWithText("/RJ").onFirst().assertIsDisplayed()
        val nativeWidths = android.widget.LinearLayout(InstrumentationRegistry.getInstrumentation().targetContext).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            repeat(3) { addView(android.view.View(context), android.widget.LinearLayout.LayoutParams(0, 20, 1f)) }
            measure(android.view.View.MeasureSpec.makeMeasureSpec((home.width + draw.width + away.width).toInt(), android.view.View.MeasureSpec.EXACTLY), 0)
        }
        listOf(home, draw, away).forEachIndexed { index, actual ->
            assertEquals("Native LinearLayout remaining-pixel share $index", nativeWidths.getChildAt(index).measuredWidth.toFloat(), actual.width, 0f)
        }
        val name = compose.onAllNodesWithText("BAHIA", useUnmergedTree=true).onFirst().fetchSemanticsNode().boundsInRoot
        assertEquals("Native name match_parent width minus two 8dp pads", home.width - with(compose.density) { 16.dp.toPx() }, name.width, 0f)
        assertEquals("Native shield top margin and name below shield", with(compose.density) { 55.dp.toPx() }, name.top-home.top, 1f)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nameReference = android.widget.TextView(context).apply {
            text = "BAHIA"; textSize = 14f
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.futura_std_book)
            measure(0, 0)
        }
        assertEquals("Inherited native Futura name metrics", nameReference.measuredHeight.toFloat(),
            compose.onAllNodesWithText("BAHIA", useUnmergedTree = true).onFirst().fetchSemanticsNode().boundsInRoot.height, 1f)
        capture("manual")
    }

    @Test fun lotogolFooterClearAndContinueThroughRealTouches() {
        launch(LOTOGOL_ID)
        compose.onAllNodesWithText("2").onFirst().performClick()
        compose.onNodeWithText("Limpar").performClick()
        compose.runOnIdle { assertTrue(vm.state.value.lotogolScores.isEmpty()); assertEquals(1275,vm.state.value.game?.contestNumber) }
        compose.onAllNodesWithText("3").onFirst().performClick()
        compose.runOnIdle { assertEquals(3 to null,vm.state.value.lotogolScores[1]); assertEquals(0,sends) }
    }

    private fun launch(id:Int=LOTECA_ID) {
        val home=SportsTeam(1,"BAHIA","BA",countryCode="BRA")
        val away=SportsTeam(2,"FLAMENGO","RJ",countryCode="BRA")
        val game=LotteryGameParameters(LotteryModality(id,if(id==LOTECA_ID)"Loteca" else "Lotogol","LOTECA"),1275,"18/10/2026",0,15,26,1,listOf(0),
            (1..12).map { BetPrice(14+it,0,BigDecimal(4L*it),it,0) } + BetPrice(16,0,BigDecimal("12"),0,1),
            matches=(1..14).map { SportsMatch(it,home,away) })
        vm=BettingGameViewModel(id,GetBettingParametersUseCase { AppResult.Success(BettingParameters(BigDecimal.ZERO,BigDecimal.ZERO,50,listOf(game))) },
            AddBetToCartUseCase { sends++; AppResult.Success(ShoppingCart(1,BigDecimal.ZERO,emptyList())) })
        compose.setContent { CaixaDSTheme(darkTheme=false) { BettingGameRoute(vm,{}, {},cartTotal=BigDecimal("24"),cartBetCount=2) } }
        compose.waitUntil(10_000) { vm.state.value.game!=null }
        compose.waitForIdle()
    }

    private fun capture(state:String) {
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        compose.waitForIdle(); instrumentation.waitForIdleSync(); android.os.SystemClock.sleep(300)
        val dir=File(requireNotNull(instrumentation.targetContext.externalCacheDir),"codex-parity").apply { mkdirs() }
        val name="compose-task9-loteca-$state"
        val bitmap=requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(dir,"$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) }; bitmap.recycle()
        // Copy the synchronous bitmap; wait for shell EOF before the next UI touch.
        android.os.ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation
            .executeShellCommand("cp ${File(dir,"$name.png").absolutePath} /sdcard/Download/$name.png"))
            .use { it.readBytes() }
        File(dir,"$name.txt").writeText("fixture=true\npackage=${instrumentation.targetContext.packageName}\nnetworkSends=$sends\ncontest=${vm.state.value.game?.contestNumber}\nquantity=${vm.state.value.numberQuantity}\noutcomes=${vm.state.value.lotecaOutcomes}\ndensityDpi=${instrumentation.targetContext.resources.displayMetrics.densityDpi}\nfontScale=${instrumentation.targetContext.resources.configuration.fontScale}\n")
        android.os.ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation
            .executeShellCommand("cp ${File(dir,"$name.txt").absolutePath} /sdcard/Download/$name.txt"))
            .use { it.readBytes() }
    }
}
