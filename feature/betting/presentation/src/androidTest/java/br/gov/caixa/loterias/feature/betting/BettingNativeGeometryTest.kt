package br.gov.caixa.loterias.feature.betting

import android.view.ContextThemeWrapper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Referência independente: limites da CX 1.24.0 instalada na densidade 3, XML real e
 * padrões do TextView de SuperSeteAdapter medidos sob o pai de Theme.SimulaMaterial.
 * Regressões detectadas: margens omitidas de fragment/cltGeral, altura de linha herdada do Compose,
 * dígitos grandes demais no adaptador e orientação complementar deslocada acima de textSelecionados. */
class BettingNativeGeometryTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val density get() = context.resources.displayMetrics.density
    private fun px(dp: Float) = dp * density
    private fun model(id: Int): BettingGameViewModel {
        val game = LotteryGameParameters(
            LotteryModality(
                id,
                if (id ==
                    7
                ) {
                    "Super Sete"
                } else if (id == 20) {
                    "Timemania"
                } else if (id == 2) {
                    "Mega-Sena"
                } else {
                    "Dia de Sorte"
                },
                "PUBLIC"
            ),
            1000, "06/10/2026",
            if (id ==
                7
            ) {
                9
            } else if (id ==
                20
            ) {
                80
            } else if (id ==
                2
            ) {
                60
            } else {
                31
            },
            if (id ==
                20
            ) {
                10
            } else if (id == 2) {
                6
            } else {
                7
            },
            if (id == 7) {
                21
            } else if (id == 20) {
                10
            } else if (id == 2) {
                7
            } else {
                15
            },
            100, listOf(0),
            if (id ==
                20
            ) {
                listOf(BetPrice(10, 0, BigDecimal("3.50")))
            } else if (id ==
                2
            ) {
                listOf(BetPrice(6, 0, BigDecimal("6.00")), BetPrice(7, 0, BigDecimal("42.00")))
            } else if (id ==
                7
            ) {
                listOf(BetPrice(7, 0, BigDecimal("3.00")), BetPrice(8, 0, BigDecimal("6.00")))
            } else {
                listOf(BetPrice(7, 0, BigDecimal("2.50")), BetPrice(8, 0, BigDecimal("20.00")))
            },
            teams = listOf(SportsTeam(1, "Flamengo", "RJ"), SportsTeam(2, "Corinthians", "SP")),
            luckyMonths = listOf(LuckyMonth(1, "Janeiro", "JAN"), LuckyMonth(2, "Fevereiro", "FEV"))
        )
        return BettingGameViewModel(
            id,
            GetBettingParametersUseCase { error("Prepared public snapshot") },
            AddBetToCartUseCase { error("Geometry never submits") },
            initialParameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game))
        )
    }
    private fun render(vm: BettingGameViewModel) {
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
    }
    private fun nativeTextHeight(text: String, futura: Boolean = false): Float {
        var height = 0
        compose.runOnIdle {
            val native = TextView(
                ContextThemeWrapper(
                    context,
                    com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar
                )
            )
            native.text = text
            native.typeface = ResourcesCompat.getFont(
                context,
                if (futura) {
                    br.gov.caixa.loterias.core.designsystem.R.font.futura_std_bold
                } else {
                    br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular
                }
            )
            if (!futura) native.textSize = 16f // simula_toolbar.xml: 16sp explícitos
            native.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            height = native.measuredHeight
        }
        return height.toFloat()
    }

    @Test fun sevenColumnRemainderDistributionMatchesMeasuredAndroidLinearLayout() {
        render(model(7))
        val headers = (0..6).map { compose.onNodeWithTag("super-sete-header-$it").fetchSemanticsNode().boundsInRoot }
        val margin = with(compose.density) { 1.dp.roundToPx() }
        val rowLeft = headers.first().left - margin
        val rowWidth = (headers.last().right + margin - rowLeft).toInt()
        val nativeBounds = mutableListOf<Pair<Int, Int>>()
        compose.runOnIdle {
            // ll_numeros_colunas_super_sete de fragment_cartela_novo: largura 0, peso 1,
            // as quatro margens de 1dp. O Android distribui as parcelas dos pesos sequencialmente.
            val row = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
            repeat(7) {
                row.addView(
                    TextView(context),
                    LinearLayout.LayoutParams(0, with(compose.density) { 35.dp.roundToPx() }, 1f).apply {
                        setMargins(margin, margin, margin, margin)
                    }
                )
            }
            row.measure(
                View.MeasureSpec.makeMeasureSpec(rowWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            row.layout(0, 0, row.measuredWidth, row.measuredHeight)
            repeat(7) {
                val child = row.getChildAt(it)
                nativeBounds.add(child.left to child.width)
            }
        }
        nativeBounds.forEachIndexed { index, (left, width) ->
            assertEquals("Native weighted header $index width", width.toFloat(), headers[index].width, .1f)
            assertEquals("Native weighted header $index left", rowLeft + left, headers[index].left, .1f)
            val ball = compose.onNodeWithTag("super-sete-$index-0").fetchSemanticsNode().boundsInRoot
            assertEquals("GridView floors ball center in column $index", rowLeft + left + (width - px(44f).toInt()) / 2, ball.left, .1f)
        }
    }

    @Test fun superSevenFirstBallRetainsNativeFragmentAndGridMargins() {
        render(model(7))
        val prompt = compose.onNodeWithText("Selecione os números:").fetchSemanticsNode().boundsInRoot
        // CX: base da orientação em 816, topo da bola em 1002 => 62dp. O cabeçalho mede 35dp,
        // com margens de 1dp, intervalo de fragment de 8dp, intervalo de cltGeral de 16dp e margem da grade de 1dp.
        (0..6).forEach { column ->
            val ball = compose.onNodeWithTag("super-sete-$column-0").fetchSemanticsNode().boundsInRoot
            assertEquals("Column $column first ball vs installed CX", px(62f), ball.top - prompt.bottom, 1f)
            assertEquals(px(44f), ball.height, 1f)
            val header = compose.onNodeWithTag("super-sete-header-$column").fetchSemanticsNode().boundsInRoot
            assertEquals("Compare the header view rather than its glyph", px(35f), header.height, 1f)
        }
    }
    private fun capture(name: String) {
        compose.waitForIdle()
        val command = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("screencap -p /sdcard/Download/task8-$name.png")
        android.os.ParcelFileDescriptor.AutoCloseInputStream(command).use { it.readBytes() }
    }

    @Test fun toolbarLabelsUseNativeTextViewHeightAndSelectorPosition() {
        render(model(7))
        val label = compose.onNodeWithText("Surpresinha", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("Native Caixa16sp includeFontPadding height", nativeTextHeight("Surpresinha"), label.height, 1f)
        var nativeSwitchWidth = 0
        var nativeRowHeight = 0
        compose.runOnIdle {
            val themed = ContextThemeWrapper(context, com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar)
            val nativeSwitch = SwitchMaterial(themed).apply {
                thumbDrawable = themed.getDrawable(R.drawable.surpresinha_thumb)
                trackDrawable = themed.getDrawable(R.drawable.surpresinha_track)
                showText = false
                text = ""
            }
            val row = LinearLayout(themed).apply {
                val padding = px(4f).toInt()
                setPadding(padding, padding, padding, padding)
                addView(nativeSwitch)
            }
            row.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            nativeSwitchWidth = nativeSwitch.measuredWidth
            nativeRowHeight = row.measuredHeight
        }
        val toolbar = compose.onNodeWithTag("betting-mode-toolbar").fetchSemanticsNode().boundsInRoot
        assertEquals("Native switch plus container padding determines toolbar height", nativeRowHeight.toFloat(), toolbar.height, 1f)
        val track = compose.onNodeWithTag("betting-surprise-switch-control", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("Native SwitchMaterial width plus XML8dp label margin", nativeSwitchWidth + px(8f), label.left - track.left, 1f)
        val prompt = compose.onNodeWithText("Selecione os números:").fetchSemanticsNode().boundsInRoot
        val selector = compose.onNodeWithTag("betting-button-Teimosinha", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("CX selector bottom702/prompt top750", px(16f), prompt.top - selector.bottom, 1f)
        assertEquals("Manual prompt remains at its installed position relative to toolbar", px(133f), prompt.top - toolbar.top, 1f)
        capture("super7")
    }

    @Test fun adapterDigitsUseNativeDefaultTextSize() {
        render(model(7))
        val digit = compose.onAllNodesWithText("0", useUnmergedTree = true)[0].fetchSemanticsNode().boundsInRoot
        assertEquals("SuperSeteAdapter leaves TextView textSize at material default", nativeTextHeight("0", true), digit.height, 1f)
    }

    @Test fun monthPromptMatchesNativeAndRealInputChangesTheSingleSelection() {
        val vm = model(11)
        render(vm)
        (1..7).forEach { compose.onNodeWithText("%02d".format(it)).performClick() }
        compose.onNodeWithText("Escolher Mês de Sorte").performClick()
        val prompt = compose.onNodeWithText("Escolha o mês de sorte:").fetchSemanticsNode().boundsInRoot
        val selector = compose.onNodeWithTag("betting-button-Teimosinha", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("CX prompt750 / selectors bottom702", px(16f), prompt.top - selector.bottom, 1f)
        val january = compose.onNodeWithTag("lucky-month-Janeiro").fetchSemanticsNode().boundsInRoot
        assertEquals("CX Jan864 / prompt bottom816", px(16f), january.top - prompt.bottom, 1f)
        compose.onNodeWithTag("lucky-month-Janeiro").performClick().assertIsSelected()
        compose.onNodeWithTag("lucky-month-Fevereiro").performClick().assertIsSelected()
        compose.onNodeWithTag("lucky-month-Janeiro").assertIsNotSelected()
        capture("month")
    }

    @Test fun teamPromptUsesTheSameNativeMarginAndRealInputSelectsATeam() {
        val vm = model(20)
        render(vm)
        (1..10).forEach { compose.onNodeWithTag("betting-number-%02d".format(it)).performClick() }
        compose.onNodeWithText("Escolher Time do Coração").performClick()
        val prompt = compose.onNodeWithText("Escolha o time do coração:").fetchSemanticsNode().boundsInRoot
        val selector = compose.onNodeWithTag("betting-button-Teimosinha", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("Common textSelecionados native16dp margin", px(16f), prompt.top - selector.bottom, 1f)
        compose.onNodeWithContentDescription("Flamengo").performClick().assertIsSelected()
        assertEquals(1, vm.state.value.selectedTeam?.number)
        capture("team")
    }

    @Test fun realSwipeScrollsOnlySuperSevenDigitsAndKeepsPromptAndHeadersFixed() {
        val vm = model(7)
        render(vm)
        val prompt = compose.onNodeWithText("Selecione os números:")
        val beforePrompt = prompt.fetchSemanticsNode().boundsInRoot
        val beforeHeaders = (0..6).map { compose.onNodeWithTag("super-sete-header-$it").fetchSemanticsNode().boundsInRoot }
        val first = compose.onNodeWithTag("super-sete-0-0").fetchSemanticsNode().boundsInRoot
        compose.onRoot().performTouchInput {
            swipe(Offset(center.x, first.top + px(250f)), Offset(center.x, first.top + px(10f)), 500)
        }
        (0..6).forEach { column ->
            val header = compose.onNodeWithTag("super-sete-header-$column")
            assertEquals(
                "fragment_cartela_novo header $column above NestedScrollView",
                beforeHeaders[column],
                header.fetchSemanticsNode().boundsInRoot
            )
            header.assertIsDisplayed()
        }
        assertEquals(
            "activity_simula_aposta textSelecionados remains outside scroll",
            beforePrompt,
            prompt.fetchSemanticsNode().boundsInRoot
        )
        prompt.assertIsDisplayed()
        compose.onNodeWithTag("super-sete-6-9").assertIsDisplayed().performClick().assertIsSelected()
        assertEquals(setOf(9), vm.state.value.superSevenColumns[6])
        capture("super7-scrolled")
    }

    @Test fun realSwipeKeepsTheCommonManualPromptFixedWhileNumbersScroll() {
        val vm = model(2)
        render(vm)
        val prompt = compose.onNodeWithText("Selecione os números:")
        val before = prompt.fetchSemanticsNode().boundsInRoot
        val first = compose.onNodeWithTag("betting-number-01").fetchSemanticsNode().boundsInRoot
        compose.onRoot().performTouchInput {
            swipe(Offset(center.x, first.top + px(250f)), Offset(center.x, first.top + px(10f)), 500)
        }
        assertEquals("Common activity prompt stays above fragment scroll", before, prompt.fetchSemanticsNode().boundsInRoot)
        prompt.assertIsDisplayed()
        compose.onNodeWithTag("betting-number-60").assertIsDisplayed().performClick().assertIsSelected()
        assertEquals(setOf(60), vm.state.value.selectedNumbers)
        capture("manual-scrolled")
    }

    @Test fun sevenColumnsRemainSelectableAndDigitNineCanBeScrolledIntoView() {
        val vm = model(7)
        render(vm)
        (0..6).forEach { compose.onNodeWithTag("super-sete-$it-0").performClick().assertIsSelected() }
        compose.onNodeWithTag("super-sete-6-9").performScrollTo().assertIsDisplayed().performClick().assertIsSelected()
        assertEquals(setOf(0, 9), vm.state.value.superSevenColumns[6])
        assertEquals(7, vm.state.value.superSevenColumns.size)
    }
}
