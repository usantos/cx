package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailBetGrid
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailLotecaMatch
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailGridNativeGeometryUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun superSevenHeadersUseNativeWrapContentAnd24dpGaps() {
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.width(426.dp)) {
                    DetailBetGrid(
                        0,
                        MarketplaceBet(
                            emptyList(),
                            emptyList(),
                            false,
                            numberColumns = (1..7).map { listOf(it) }
                        ),
                        Color(0xFF839F38),
                        "SUPER_7"
                    )
                }
            }
        }
        val first = compose.onAllNodesWithText("1", useUnmergedTree = true)[0]
        val seventh = compose.onAllNodesWithText("7", useUnmergedTree = true)[0]
        val layouts = mutableListOf<TextLayoutResult>()
        first.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        // item_super_sete da CX: cada rótulo envolve seu glifo + padding de 16dp; o próximo começa 24dp depois.
        val nativeStride = layouts.single().size.width + with(compose.density) {
            // O XML arredonda cada padding e margem; 8dp + 8dp + 24dp não equivale a arredondar 40dp.
            2 * 8.dp.roundToPx() + 24.dp.roundToPx()
        }
        assertEquals(
            nativeStride * headerIntervals,
            seventh.fetchSemanticsNode().boundsInRoot.center.x - first.fetchSemanticsNode().boundsInRoot.center.x,
            1f
        )
    }

    @Test fun millionaireCloverStarts24dpBelowNativeTrevosLabelTop() {
        compose.setContent {
            CaixaDSTheme {
                DetailBetGrid(0, MarketplaceBet((1..6).toList(), listOf(1, 2), false), Color(0xFF2A3580), "MAIS_MILIONARIA")
            }
        }
        val label = compose.onNodeWithText("Trevos").fetchSemanticsNode().boundsInRoot
        val clover = compose.onNodeWithTag("pool-detail-clover-0-0").fetchSemanticsNode().boundsInRoot
        // APK ativo: topo do rótulo em 1922px, topo do trevo em 1994px na densidade 3 (24dp).
        assertEquals(px(24), clover.top - label.top, 1f)
    }

    @Test fun superSevenSectionLabelsUseNativeDarkInk() {
        val detail = MarketplacePoolDetail(
            "B1", "SUPER_7", 3070, 10, "Lotérica", "1", "Cidade - SP",
            "21/10/2026", "1000000", 20, 50, 1, 7, "10", "0.50", "10.50",
            listOf(MarketplaceBet(emptyList(), emptyList(), false, numberColumns = (1..7).map { listOf(it) }))
        )
        compose.setContent {
            CaixaDSTheme {
                MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail, isLoading = false), {}, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("Colunas").performScrollTo()
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Colunas").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(Color(0xFF839F38), layouts.single().layoutInput.style.color)
    }

    @Test fun lotecaUncheckedIndicatorKeepsNative27dpHeight() {
        compose.setContent { CaixaDSTheme { DetailLotecaMatch(0, lotecaSelection()) } }
        val indicator = compose.onNodeWithTag("pool-detail-loteca-0-draw").fetchSemanticsNode().boundsInRoot
        assertEquals(px(27), indicator.height, 1f)
    }

    @Test fun lotecaLegendKeepsNativeSpacingAfterTeamName() {
        compose.setContent { CaixaDSTheme { DetailLotecaMatch(0, lotecaSelection()) } }
        compose.onNodeWithText("SANTOS/SP  (PRINCIPAL)").assertIsDisplayed()
    }

    @Test fun consecutiveLotecaMatchesKeepNativeStrideAtDeviceDensity() {
        compose.setContent {
            CaixaDSTheme {
                Column(Modifier.width(426.dp)) {
                    DetailLotecaMatch(0, lotecaSelection())
                    DetailLotecaMatch(1, lotecaSelection())
                }
            }
        }
        val first = compose.onNodeWithTag("pool-detail-loteca-0-home").fetchSemanticsNode().boundsInRoot
        val next = compose.onNodeWithTag("pool-detail-loteca-1-home").fetchSemanticsNode().boundsInRoot
        // Recria item_partida_loteca.xml com Views nativas; cada dimensão XML arredonda separadamente.
        // A mesma referência roda no aparelho e no Robolectric sem depender de um pacote instalado.
        val nativeWidth = compose.onRoot().fetchSemanticsNode().boundsInRoot.width.toInt()
        val nativeStride = compose.runOnIdle { nativeLotecaStride(nativeWidth) }
        val draw = compose.onNodeWithTag("pool-detail-loteca-0-draw").fetchSemanticsNode().boundsInRoot
        val away = compose.onNodeWithTag("pool-detail-loteca-0-away").fetchSemanticsNode().boundsInRoot
        val label = compose.onAllNodesWithText("PALMEIRAS/SP  (PRINCIPAL)", useUnmergedTree = true)[0]
            .fetchSemanticsNode().boundsInRoot
        assertEquals(
            "Native stride; home=$first draw=$draw away=$away awayLabel=$label next=$next",
            nativeStride.toFloat(),
            next.top - first.top,
            1f
        )
    }

    private val nativeMarginDp = 16f
    private val headerIntervals = 6f
    private val nativeGapDp = 8f
    private val nativeDividerDp = .8f

    private fun nativeLotecaStride(nativeWidth: Int): Int {
        val target = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val themed = android.view.ContextThemeWrapper(target, android.R.style.Theme_Material_Light_NoActionBar)
        val row = androidx.constraintlayout.widget.ConstraintLayout(themed)
        fun dp(value: Float) = with(compose.density) { value.dp.roundToPx() }
        val checks = listOf(true, false, false).mapIndexed { index, checked ->
            android.widget.ImageView(themed).apply {
                id = android.view.View.generateViewId()
                val drawable = if (checked) {
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_marketplace_ic_radio_checked
                } else {
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_marketplace_ic_radio_unchecked
                }
                setBackgroundResource(drawable)
                setImageResource(drawable)
                row.addView(
                    this,
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        startToStart = 0
                        marginStart = dp(nativeMarginDp)
                        topMargin = dp(if (index == 0) nativeMarginDp else nativeGapDp)
                        if (index == 0) topToTop = 0
                    }
                )
            }
        }
        checks.drop(1).forEachIndexed { index, view ->
            (view.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams).topToBottom = checks[index].id
        }
        val labels = listOf("SANTOS/SP  (PRINCIPAL)", "X", "PALMEIRAS/SP  (PRINCIPAL)").mapIndexed { index, label ->
            android.widget.TextView(themed).apply {
                id = android.view.View.generateViewId()
                text = label
                typeface = androidx.core.content.res.ResourcesCompat.getFont(
                    target,
                    br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold
                )
                row.addView(
                    this,
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        0,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        startToEnd = checks[index].id
                        endToEnd = 0
                        topToTop = checks[index].id
                        bottomToBottom = checks[index].id
                        marginStart = dp(nativeGapDp)
                    }
                )
            }
        }
        row.addView(
            android.view.View(themed),
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                dp(nativeDividerDp)
            ).apply {
                startToStart = 0
                topToBottom = labels.last().id
                topMargin = dp(nativeMarginDp)
            }
        )
        row.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(nativeWidth, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED)
        )
        return row.measuredHeight
    }

    private fun lotecaSelection() = LotecaMatchSelection(
        SportsMatch(
            1,
            SportsTeam(1, "SANTOS", "SP", countryCode = "BRA"),
            SportsTeam(2, "PALMEIRAS", "SP", countryCode = "BRA"),
            "ABC PRINCIPAL"
        ),
        true,
        false,
        false
    )

    private fun px(value: Int) = with(compose.density) { value.dp.toPx() }
}
