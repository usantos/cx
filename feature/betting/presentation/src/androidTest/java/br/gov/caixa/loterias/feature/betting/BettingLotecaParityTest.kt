package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.io.File
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Referência de geometria: XML da CX instalado em 1280x2856, 480dpi, fontScale=1; sem chamadas de serviço. */
class BettingLotecaParityTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var vm: BettingGameViewModel
    private var sends = 0
    private var backCalls = 0

    @Test fun footerRandomCompletesFifteenAndSixteenLocallyAndWorksAgainAfterClear() {
        launch(includeTriple = false)
        compose.onNodeWithText("Aposta aleatória").assertIsEnabled().performClick()
        assertCompleted(15, 13, 1, "R$ 4,00")
        capture("random15-completed")
        compose.onNodeWithText("Limpar").performClick()
        compose.onNodeWithText("Aposta aleatória").assertIsEnabled()
        compose.onNodeWithTag("loteca-2-AWAY").performClick()
        compose.onNodeWithText("Completar jogo").assertIsEnabled().performClick()
        assertCompleted(15, 13, 1, "R$ 4,00")
        compose.runOnIdle { assertEquals(setOf(LotecaOutcome.AWAY), vm.state.value.lotecaOutcomes[2]) }
        compose.onNodeWithText("Limpar").performClick()
        compose.onNodeWithText("15 palpites").performClick()
        compose.onNodeWithTag("betting-option-item-1").performClick()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Aposta aleatória").performClick()
        assertCompleted(16, 12, 2, "R$ 8,00")
        capture("random16-completed")
    }

    private fun assertCompleted(target: Int, singles: Int, doubles: Int, amount: String) {
        compose.onNodeWithText("Selecionados: $target/$target").assertIsDisplayed()
        compose.onNodeWithTag("loteca-count-partidas").assertTextEquals("14")
        compose.onNodeWithTag("loteca-count-simples").assertTextEquals(singles.toString())
        compose.onNodeWithTag("loteca-count-duplas").assertTextEquals(doubles.toString())
        compose.onNodeWithTag("loteca-count-triplas").assertTextEquals("0")
        compose.onNodeWithText("Completar jogo").assertIsNotEnabled()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled()
        compose.onNodeWithText(amount).assertIsDisplayed()
        compose.onNodeWithText("R$ 24,00").assertIsDisplayed()
        compose.onNodeWithContentDescription("Carrinho de apostas, 2 itens").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, sends)
            assertTrue(vm.state.value.canAddToCart)
        }
    }

    @Test fun counterAndSelectedTextMetricsMatchActualNativeTextViewsAndFlowCenter() {
        launch()
        assertLotecaCounterMetrics(compose)
        capture("counter-metrics")
    }

    @Test fun mountedDescriptionsKeepNativeHeightAndColorAcrossManualRandomAndClear() {
        launch(includeTriple = false)
        lateinit var native: LotecaNativeOracle.Row
        compose.runOnIdle { native = LotecaNativeOracle(InstrumentationRegistry.getInstrumentation().targetContext).Row() }
        fun bounds(tag: String) = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val first = bounds("loteca-1-HOME")
        val secondTitle = compose.onNodeWithText("Jogo 02").fetchSemanticsNode().boundsInRoot
        val nativeOracle = LotecaNativeOracle(InstrumentationRegistry.getInstrumentation().targetContext)
        val toolbarWidth = (
            listOf("HOME", "DRAW", "AWAY").sumOf { bounds("loteca-1-$it").width.toDouble() } +
                with(compose.density) { 2 * 8.dp.roundToPx() }
            ).toInt()
        var initialToolbarHeight = 0
        compose.runOnIdle { initialToolbarHeight = nativeOracle.toolbarMetrics(toolbarWidth).first }
        fun verifyMounted() {
            val current = bounds("loteca-1-HOME")
            var nativeHeight = 0
            compose.runOnIdle { nativeHeight = native.height((current.width * 3).toInt()) }
            assertEquals("Mounted native card height", nativeHeight.toFloat(), current.height, 0f)
            assertEquals(first.height, current.height, 0f)
            var toolbarHeight = 0
            compose.runOnIdle {
                val outcomes = vm.state.value.lotecaOutcomes.values
                toolbarHeight = nativeOracle.toolbarMetrics(
                    toolbarWidth,
                    listOf(
                        outcomes.count { it.isNotEmpty() },
                        outcomes.count { it.size == 1 },
                        outcomes.count { it.size == 2 },
                        outcomes.count { it.size == 3 }
                    )
                ).first
            }
            assertEquals(
                "Apenas a quebra de linha do cabeçalho nativo desloca a próxima partida",
                secondTitle.top + toolbarHeight - initialToolbarHeight,
                compose.onNodeWithText("Jogo 02").fetchSemanticsNode().boundsInRoot.top,
                0f
            )
            listOf(true to "/BA", false to "/RJ").forEach { (home, text) ->
                val node = compose.onAllNodesWithText(text, useUnmergedTree = true).onFirst().assertIsDisplayed()
                var color = 0
                compose.runOnIdle {
                    assertEquals(android.view.View.VISIBLE, native.description(home).visibility)
                    color =
                        native.description(home).currentTextColor
                }
                val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
                node.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) { it(layouts) }
                assertEquals(
                    "Native mounted $text selection color",
                    androidx.compose.ui.graphics.Color(color),
                    layouts.single().layoutInput.style.color
                )
            }
        }
        verifyMounted()
        compose.onNodeWithTag("loteca-1-HOME").performClick()
        compose.onNodeWithTag("loteca-1-AWAY").performClick()
        compose.runOnIdle { native.select(true, true) }
        verifyMounted()
        capture("mounted-manual-selected")
        compose.onNodeWithText("Completar jogo").performClick()
        verifyMounted()
        capture("mounted-random-selected")
        compose.onNodeWithText("Limpar").performClick()
        compose.runOnIdle {
            native.select(false, false)
            native.bindDescriptions()
        }
        verifyMounted()
        compose.runOnIdle { assertEquals(0, sends) }
    }

    @Test fun initialBindingHidesSelectedOrIndicatorDescriptionsAndClearRebindsUnselectedTeams() {
        launch(initialHomeSelected = true, awayIndicator = true)
        lateinit var native: LotecaNativeOracle.Row
        compose.runOnIdle { native = LotecaNativeOracle(InstrumentationRegistry.getInstrumentation().targetContext).Row(true, true) }
        compose.onAllNodesWithText("/BA").onFirst().assertDoesNotExist()
        compose.onAllNodesWithText("/RJ").onFirst().assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(android.view.View.GONE, native.description(true).visibility)
            assertEquals(android.view.View.GONE, native.description(false).visibility)
        }
        compose.onNodeWithTag("loteca-1-HOME").performClick()
        compose.runOnIdle { native.select(false, false) }
        compose.onAllNodesWithText("/BA").onFirst().assertDoesNotExist()
        compose.onNodeWithTag("loteca-1-DRAW").performClick()
        compose.onNodeWithText("Limpar").performClick()
        compose.runOnIdle {
            native.select(false, false)
            native.bindDescriptions()
        }
        compose.onAllNodesWithText("/BA").onFirst().assertIsDisplayed()
        compose.onAllNodesWithText("/RJ").onFirst().assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(android.view.View.VISIBLE, native.description(true).visibility)
            assertEquals(android.view.View.GONE, native.description(false).visibility)
            assertEquals(0, sends)
        }
    }

    @Test fun reducingQuantityRebindsInitiallySelectedDescriptionsAfterKeepingThemOnIncrease() {
        launch(initialHomeSelected = true, awayIndicator = true)
        compose.onNodeWithText("15 palpites").performClick()
        compose.onNodeWithTag("betting-option-item-1").performClick()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.onAllNodesWithText("/BA").onFirst().assertDoesNotExist()
        compose.onNodeWithText("16 palpites").performClick()
        compose.onNodeWithTag("betting-option-item-0").performClick()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.onNodeWithText("Selecionados: 0/15").assertIsDisplayed()
        compose.onAllNodesWithText("/BA").onFirst().assertIsDisplayed()
        compose.onAllNodesWithText("/RJ").onFirst().assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, sends) }
    }

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
        compose.onNodeWithText(
            "Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta."
        ).assertIsDisplayed()
        val warning = compose.onNodeWithText(
            "Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta."
        ).fetchSemanticsNode().boundsInRoot
        val screenWidth = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.widthPixels
        assertEquals(
            "DS quantity warning: bounded surface minus two 24dp text insets",
            minOf(screenWidth - with(compose.density) { 2 * 24.dp.roundToPx() }, with(compose.density) { 560.dp.roundToPx() }).toFloat() -
                with(compose.density) { 2 * 24.dp.roundToPx() },
            warning.width,
            1f
        )
        assertEquals(
            "Native warning action retains 48dp touch height",
            with(compose.density) {
                48.dp.toPx()
            },
            compose.onNodeWithText("Entendi").fetchSemanticsNode().boundsInRoot.height,
            1f
        )
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
        compose.runOnIdle {
            assertEquals(0, sends)
            assertEquals(setOf(LotecaOutcome.AWAY), vm.state.value.lotecaOutcomes[2])
        }
    }

    // Cenário existente de geometria ponta a ponta; mantenha suas asserções contíguas após a formatação.
    @Suppress("LongMethod")
    @Test
    fun installedToolbarSelectorFirstMatchAndEqualCardGeometry() {
        launch()
        capture("manual")
        val title = compose.onNodeWithText("Suas apostas").fetchSemanticsNode().boundsInRoot
        val button = compose.onNodeWithTag("loteca-quantity").fetchSemanticsNode().boundsInRoot
        val match = compose.onNodeWithText("Jogo 01").fetchSemanticsNode().boundsInRoot
        val home = compose.onNodeWithTag("loteca-1-HOME").fetchSemanticsNode().boundsInRoot
        val draw = compose.onNodeWithTag("loteca-1-DRAW").fetchSemanticsNode().boundsInRoot
        val away = compose.onNodeWithTag("loteca-1-AWAY").fetchSemanticsNode().boundsInRoot
        // O XML nativo é medido na densidade atual, preservando os arredondamentos de cada margem.
        var nativeToolbar: Pair<Int, Int> = 0 to 0
        var nativeCardHeight = 0
        val width = (home.width + draw.width + away.width).toInt() + with(compose.density) { 2 * 8.dp.roundToPx() }
        compose.runOnIdle {
            val oracle = LotecaNativeOracle(InstrumentationRegistry.getInstrumentation().targetContext)
            nativeToolbar = oracle.toolbarMetrics(width)
            nativeCardHeight = oracle.Row().height((home.width + draw.width + away.width).toInt())
        }
        val selectorOffset = nativeToolbar.first - nativeToolbar.second + with(compose.density) { 16.dp.roundToPx() }
        assertEquals(selectorOffset.toFloat(), button.top - title.top, 1f)
        assertEquals(
            selectorOffset + with(compose.density) { (45.dp.roundToPx() + 8.dp.roundToPx() + 16.dp.roundToPx()).toFloat() },
            match.top - title.top,
            1f
        )
        assertEquals(with(compose.density) { 8.dp.toPx() }, home.top - match.bottom, 1f)
        assertEquals(nativeCardHeight.toFloat(), home.height, 1f)
        assertEquals(home.height, draw.height, 1f)
        assertEquals(home.height, away.height, 1f)
        assertEquals(home.right, draw.left, 1f)
        assertEquals(draw.right, away.left, 1f)
        compose.onAllNodesWithText("/BA").onFirst().assertIsDisplayed()
        compose.onAllNodesWithText("/RJ").onFirst().assertIsDisplayed()
        val nativeWidths = android.widget.LinearLayout(InstrumentationRegistry.getInstrumentation().targetContext).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            repeat(3) { addView(android.view.View(context), android.widget.LinearLayout.LayoutParams(0, 20, 1f)) }
            measure(
                android.view.View.MeasureSpec.makeMeasureSpec(
                    (home.width + draw.width + away.width).toInt(),
                    android.view.View.MeasureSpec.EXACTLY
                ),
                0
            )
        }
        listOf(home, draw, away).forEachIndexed { index, actual ->
            assertEquals(
                "Native LinearLayout remaining-pixel share $index",
                nativeWidths.getChildAt(index).measuredWidth.toFloat(),
                actual.width,
                0f
            )
        }
        val name = compose.onAllNodesWithText("BAHIA", useUnmergedTree = true).onFirst().fetchSemanticsNode().boundsInRoot
        assertEquals(
            "Native name match_parent width minus two 8dp pads",
            home.width - with(compose.density) {
                // Cada padding XML de 8dp é arredondado separadamente em densidades fracionárias.
                2 * 8.dp.roundToPx()
            },
            name.width,
            0f
        )
        assertEquals("Native shield top margin and name below shield", with(compose.density) { 55.dp.toPx() }, name.top - home.top, 1f)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nameReference = android.widget.TextView(
            android.view.ContextThemeWrapper(
                context,
                com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar
            )
        ).apply {
            text = "BAHIA"
            textSize = 14f
            measure(0, 0)
        }
        assertEquals(
            "Activity Material theme default name metrics",
            nameReference.measuredHeight.toFloat(),
            compose.onAllNodesWithText("BAHIA", useUnmergedTree = true).onFirst().fetchSemanticsNode().boundsInRoot.height,
            1f
        )
        val matchReference = android.widget.TextView(context).apply {
            text = "Jogo 01"
            textSize = 16f
            typeface =
                androidx.core.content.res.ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular)
            measure(0, 0)
        }
        val ufReference = android.widget.TextView(
            android.view.ContextThemeWrapper(
                context,
                com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar
            )
        ).apply {
            text = "/BA"
            textSize = 12f
            measure(0, 0)
        }
        assertEquals("Native title height", matchReference.measuredHeight.toFloat(), match.height, 1f)
        assertEquals(
            "Native left UF height",
            ufReference.measuredHeight.toFloat(),
            compose.onAllNodesWithText("/BA", useUnmergedTree = true).onFirst().fetchSemanticsNode().boundsInRoot.height,
            1f
        )
        // XML nativo: título +8dp, próximo item +16dp; cartão com padding de 16dp, escudo com margem de 12dp e altura de 35dp,
        // e dois TextViews de 14sp do tema Material determinam o cartão mais alto (visitante).
        val nativePitch = matchReference.measuredHeight + nameReference.measuredHeight * 2 +
            with(compose.density) { (8 + 16 + 16 + 12 + 35).dp.toPx() }
        (2..3).forEach { number ->
            val row = compose.onNodeWithText("Jogo ${number.toString().padStart(2,'0')}").fetchSemanticsNode().boundsInRoot
            assertEquals("Native row $number accumulates the measured XML pitch", nativePitch * (number - 1), row.top - match.top, 1f)
        }
        capture("manual")
    }

    @Test fun narrowScaledToolbarReservesHelpWidthAndEllipsizesTitle() {
        launch(width = 300, fontScale = 1.35f)
        val title = compose.onNodeWithText("Suas apostas").fetchSemanticsNode().boundsInRoot
        val help = compose.onNodeWithText("Como jogar").fetchSemanticsNode().boundsInRoot
        assertTrue("Native title must end before the help action", title.right <= help.left)
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        compose.onNodeWithText("Suas apostas").performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) {
            it(layouts)
        }
        assertTrue("Native constrained title must ellipsize at this narrow scaled width", layouts.single().isLineEllipsized(0))
        compose.onNodeWithText("Como jogar").performClick()
        compose.onNodeWithText("Como Jogar: loteca").assertIsDisplayed()
    }

    @Test fun lotecaShieldKeepsNativeThirtyFiveDpDrawableArea() {
        launch(solidShield = true)
        val pixels = compose.onNodeWithTag("loteca-1-HOME").captureToImage().toPixelMap()
        val colored = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until pixels.height) {
            for (x in 0 until pixels.width) {
                val color = pixels[x, y]
                if (color.red > .99f && color.blue > .99f && color.green < .01f) colored += x to y
            }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val size = with(compose.density) { 35.dp.roundToPx() }
        val native = android.widget.ImageView(context).apply {
            setImageBitmap(solidBadge())
            measure(
                android.view.View.MeasureSpec.makeMeasureSpec(size, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(size, android.view.View.MeasureSpec.EXACTLY)
            )
            layout(0, 0, measuredWidth, measuredHeight)
        }
        val nativePixels = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        native.draw(android.graphics.Canvas(nativePixels))
        val nativeColored = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until size) {
            for (x in 0 until size) {
                if (nativePixels.getPixel(x, y) == android.graphics.Color.MAGENTA) nativeColored += x to y
            }
        }
        assertEquals(
            "Native 35dp ImageView drawable width",
            nativeColored.maxOf { it.first } - nativeColored.minOf { it.first } + 1,
            colored.maxOf { it.first } - colored.minOf { it.first } + 1
        )
        assertEquals(
            "Native 35dp ImageView drawable height",
            nativeColored.maxOf { it.second } - nativeColored.minOf { it.second } + 1,
            colored.maxOf { it.second } - colored.minOf { it.second } + 1
        )
    }

    private fun solidBadge() = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).apply { eraseColor(android.graphics.Color.MAGENTA) }

    @Test fun editedLotecaExitUsesBoundedDialogWidthAndNativeActionHeights() {
        launch()
        compose.onNodeWithTag("loteca-1-HOME").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        val message = "Ao voltar para a página inicial, sua aposta em edição será descartada. Deseja continuar?"
        val screenWidth = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.widthPixels
        val bounds = compose.onNodeWithText(message).fetchSemanticsNode().boundsInRoot
        assertEquals(
            "DS discard prompt text width",
            minOf(screenWidth - with(compose.density) { 2 * 24.dp.roundToPx() }, with(compose.density) { 560.dp.roundToPx() }).toFloat() -
                with(compose.density) { 2 * 24.dp.roundToPx() },
            bounds.width,
            1f
        )
        listOf("Confirmar", "Cancelar").forEach { label ->
            assertEquals(
                "Native discard prompt $label action height",
                with(compose.density) {
                    48.dp.toPx()
                },
                compose.onNodeWithText(label).fetchSemanticsNode().boundsInRoot.height,
                1f
            )
        }
        capture("exit-confirmation")
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(0, backCalls)
            assertEquals(setOf(LotecaOutcome.HOME), vm.state.value.lotecaOutcomes[1])
        }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertEquals(1, backCalls)
            assertEquals(0, sends)
        }
    }

    @Test fun lotogolFooterClearAndContinueThroughRealTouches() {
        launch(LOTOGOL_ID)
        compose.onAllNodesWithText("2").onFirst().performClick()
        compose.onNodeWithText("Limpar").performClick()
        compose.runOnIdle {
            assertTrue(vm.state.value.lotogolScores.isEmpty())
            assertEquals(1275, vm.state.value.game?.contestNumber)
        }
        compose.onAllNodesWithText("3").onFirst().performClick()
        compose.runOnIdle {
            assertEquals(3 to null, vm.state.value.lotogolScores[1])
            assertEquals(0, sends)
        }
    }

    private fun launch(
        id: Int = LOTECA_ID,
        width: Int? = null,
        fontScale: Float = 1f,
        solidShield: Boolean = false,
        includeTriple: Boolean = true,
        initialHomeSelected: Boolean = false,
        awayIndicator: Boolean = false,
    ) {
        val badge = if (solidShield) {
            java.io.ByteArrayOutputStream().use { stream ->
                solidBadge().compress(Bitmap.CompressFormat.PNG, 100, stream)
                android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
            }
        } else {
            null
        }
        val home = SportsTeam(1, "BAHIA", "BA", countryCode = "BRA", shieldBase64 = badge)
        val away = SportsTeam(2, "FLAMENGO", "RJ", countryCode = "BRA", selectionIndicator = awayIndicator)
        val game = LotteryGameParameters(
            LotteryModality(
                id,
                if (id ==
                    LOTECA_ID
                ) {
                    "Loteca"
                } else {
                    "Lotogol"
                },
                "LOTECA"
            ),
            1275, "18/10/2026", 0, 15, 26, 1, listOf(0),
            (1..12).map { BetPrice(14 + it, 0, BigDecimal(4L * it), it, 0) } +
                if (includeTriple) listOf(BetPrice(16, 0, BigDecimal("12"), 0, 1)) else emptyList(),
            matches = (1..14).map { SportsMatch(it, home, away) }
        )
        vm =
            BettingGameViewModel(
                id,
                GetBettingParametersUseCase {
                    AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 50, listOf(game)))
                },
                AddBetToCartUseCase {
                    sends++
                    AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
                },
                initialLotecaSelections = if (initialHomeSelected) {
                    game.matches.map {
                        LotecaMatchSelection(it, true, false, false)
                    }
                } else {
                    emptyList()
                }
            )
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                CaixaDSTheme(darkTheme = false) {
                    Box(if (width == null) Modifier else Modifier.width(width.dp)) {
                        BettingGameRoute(vm, { backCalls++ }, {}, cartTotal = BigDecimal("24"), cartBetCount = 2)
                    }
                }
            }
        }
        compose.waitUntil(10_000) { vm.state.value.game != null }
        compose.waitForIdle()
    }

    private fun capture(state: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        compose.waitForIdle()
        instrumentation.waitForIdleSync()
        android.os.SystemClock.sleep(300)
        val dir = File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-parity").apply { mkdirs() }
        val name = if (state.startsWith("random") || state.startsWith("mounted") ||
            state == "counter-metrics"
        ) {
            "compose-task11-loteca-$state"
        } else {
            "compose-task9-loteca-$state"
        }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        // Copie o bitmap síncrono; aguarde o EOF do shell antes do próximo toque na interface.
        android.os.ParcelFileDescriptor.AutoCloseInputStream(
            instrumentation.uiAutomation
                .executeShellCommand("cp ${File(dir,"$name.png").absolutePath} /sdcard/Download/$name.png")
        )
            .use { it.readBytes() }
        File(
            dir,
            "$name.txt"
        ).writeText(
            "fixture=true\n" +
                "package=${instrumentation.targetContext.packageName}\n" +
                "networkSends=$sends\n" +
                "contest=${vm.state.value.game?.contestNumber}\n" +
                "quantity=${vm.state.value.numberQuantity}\n" +
                "outcomes=${vm.state.value.lotecaOutcomes}\n" +
                "densityDpi=${instrumentation.targetContext.resources.displayMetrics.densityDpi}\n" +
                "fontScale=${instrumentation.targetContext.resources.configuration.fontScale}\n"
        )
        android.os.ParcelFileDescriptor.AutoCloseInputStream(
            instrumentation.uiAutomation
                .executeShellCommand("cp ${File(dir,"$name.txt").absolutePath} /sdcard/Download/$name.txt")
        )
            .use { it.readBytes() }
    }
}
