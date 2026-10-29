package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTooltipPositionProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaixaDSTooltipPositionTest {
    @Test fun bubbleStaysBelowIconAndArrowTracksItAtBothWindowEdges() {
        for (direction in LayoutDirection.entries) {
            for (left in listOf(0, 130, 310)) {
                val anchor = IntRect(left, 200, left + 48, 248)
                var arrow = -1
                val provider = CaixaDSTooltipPositionProvider(4, 12) { arrow = it }
                val position = provider.calculatePosition(anchor, IntSize(360, 800), direction, IntSize(280, 70))
                assertEquals(252, position.y)
                assertEquals(anchor.center.x, position.x + arrow)
                assertTrue(position.x >= 12)
                assertTrue(position.x + 280 <= 348)
            }
        }
    }

    @Test fun fullWidthBubbleDoesNotOverflowNarrowWindow() {
        var arrow = -1
        val provider = CaixaDSTooltipPositionProvider(4, 12) { arrow = it }
        val position = provider.calculatePosition(
            IntRect(20, 100, 68, 148),
            IntSize(240, 600),
            LayoutDirection.Ltr,
            IntSize(240, 70)
        )
        assertEquals(IntOffset(0, 152), position)
        assertEquals(44, arrow)
    }
}
