package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals

private val counterTextSize = 14f
private val counterHostInset = 34.dp

internal fun assertLotecaCounterMetrics(compose: ComposeContentTestRule) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    fun reference(textValue: String, bold: Boolean = false) = android.widget.TextView(context).apply {
        text = textValue
        textSize = counterTextSize
        typeface = androidx.core.content.res.ResourcesCompat.getFont(
            context,
            if (bold) {
                br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold
            } else {
                br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular
            }
        )
        measure(0, 0)
    }
    val labels = listOf("Partidas: ", "Simples: ", "Duplas: ", "Triplas: ")
    val bounds = labels.map { compose.onNodeWithText(it, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot }
    labels.forEachIndexed { index, label ->
        assertEquals("Native $label TextView integer line metrics", reference(label).measuredHeight.toFloat(), bounds[index].height, 0f)
    }
    listOf("partidas", "simples", "duplas", "triplas").forEach { tag ->
        assertEquals(
            "Native semibold counter TextView integer metrics",
            reference("0", true).measuredHeight.toFloat(),
            compose.onNodeWithTag("loteca-count-$tag").fetchSemanticsNode().boundsInRoot.height,
            0f
        )
    }
    val title = compose.onNodeWithText("Suas apostas").fetchSemanticsNode().boundsInRoot
    var nativeOffsets: List<Int> = emptyList()
    compose.runOnIdle {
        nativeOffsets =
            LotecaNativeOracle(context).counterOffsetsFromTitleBottom(
                context.resources.displayMetrics.widthPixels - with(compose.density) {
                    counterHostInset.roundToPx()
                }
            )
    }
    bounds.forEachIndexed { index, actual ->
        assertEquals(
            "Native Flow outer center and LinearLayout inner integer gravity group $index",
            nativeOffsets[index].toFloat(),
            actual.top - title.bottom,
            0f
        )
    }
    assertEquals(
        "Selected TextView glyph height inside native 40dp box",
        reference("Selecionados: 0/15").measuredHeight.toFloat(),
        compose.onNodeWithText("Selecionados: 0/15").fetchSemanticsNode().boundsInRoot.height,
        0f
    )
}
