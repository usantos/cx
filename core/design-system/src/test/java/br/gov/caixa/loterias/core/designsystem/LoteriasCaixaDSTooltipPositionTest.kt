package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTooltipPositionProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoteriasCaixaDSTooltipPositionTest {
    @Test fun bubbleStaysBelowIconAndArrowTracksItAtBothWindowEdges() {
        for (direction in LayoutDirection.entries) {
            for (left in listOf(0, TooltipPositionTestExpectations.CENTER_ANCHOR_LEFT, TooltipPositionTestExpectations.RIGHT_ANCHOR_LEFT)) {
                val anchor =
                    IntRect(
                        left,
                        TooltipPositionTestExpectations.WIDE_ANCHOR_TOP,
                        left + TooltipPositionTestExpectations.ANCHOR_WIDTH,
                        TooltipPositionTestExpectations.WIDE_ANCHOR_BOTTOM
                    )
                var arrow = -1
                val provider =
                    LoteriasCaixaDSTooltipPositionProvider(
                        TooltipPositionTestExpectations.ANCHOR_GAP,
                        TooltipPositionTestExpectations.WINDOW_MARGIN
                    ) {
                        arrow =
                            it
                    }
                val position = provider.calculatePosition(
                    anchor,
                    IntSize(TooltipPositionTestExpectations.WIDE_WINDOW_WIDTH, TooltipPositionTestExpectations.WIDE_WINDOW_HEIGHT),
                    direction,
                    IntSize(TooltipPositionTestExpectations.BUBBLE_WIDTH, TooltipPositionTestExpectations.BUBBLE_HEIGHT)
                )
                assertEquals(TooltipPositionTestExpectations.EXPECTED_WIDE_BUBBLE_TOP, position.y)
                assertEquals(anchor.center.x, position.x + arrow)
                assertTrue(position.x >= TooltipPositionTestExpectations.WINDOW_MARGIN)
                assertTrue(
                    position.x + TooltipPositionTestExpectations.BUBBLE_WIDTH <= TooltipPositionTestExpectations.EXPECTED_RIGHT_LIMIT
                )
            }
        }
    }

    @Test fun fullWidthBubbleDoesNotOverflowNarrowWindow() {
        var arrow = -1
        val provider =
            LoteriasCaixaDSTooltipPositionProvider(
                TooltipPositionTestExpectations.ANCHOR_GAP,
                TooltipPositionTestExpectations.WINDOW_MARGIN
            ) {
                arrow =
                    it
            }
        val position = provider.calculatePosition(
            IntRect(
                TooltipPositionTestExpectations.NARROW_ANCHOR_LEFT,
                TooltipPositionTestExpectations.NARROW_ANCHOR_TOP,
                TooltipPositionTestExpectations.NARROW_ANCHOR_RIGHT,
                TooltipPositionTestExpectations.NARROW_ANCHOR_BOTTOM
            ),
            IntSize(TooltipPositionTestExpectations.NARROW_WINDOW_WIDTH, TooltipPositionTestExpectations.NARROW_WINDOW_HEIGHT),
            LayoutDirection.Ltr,
            IntSize(TooltipPositionTestExpectations.NARROW_WINDOW_WIDTH, TooltipPositionTestExpectations.BUBBLE_HEIGHT)
        )
        assertEquals(IntOffset(0, TooltipPositionTestExpectations.EXPECTED_NARROW_BUBBLE_TOP), position)
        assertEquals(TooltipPositionTestExpectations.EXPECTED_NARROW_ARROW_OFFSET, arrow)
    }
}

private object TooltipPositionTestExpectations {
    const val CENTER_ANCHOR_LEFT = 130
    const val RIGHT_ANCHOR_LEFT = 310
    const val WIDE_ANCHOR_TOP = 200
    const val ANCHOR_WIDTH = 48
    const val WIDE_ANCHOR_BOTTOM = 248
    const val ANCHOR_GAP = 4
    const val WINDOW_MARGIN = 12
    const val WIDE_WINDOW_WIDTH = 360
    const val WIDE_WINDOW_HEIGHT = 800
    const val BUBBLE_WIDTH = 280
    const val BUBBLE_HEIGHT = 70
    const val EXPECTED_WIDE_BUBBLE_TOP = 252
    const val EXPECTED_RIGHT_LIMIT = 348
    const val NARROW_ANCHOR_LEFT = 20
    const val NARROW_ANCHOR_TOP = 100
    const val NARROW_ANCHOR_RIGHT = 68
    const val NARROW_ANCHOR_BOTTOM = 148
    const val NARROW_WINDOW_WIDTH = 240
    const val NARROW_WINDOW_HEIGHT = 600
    const val EXPECTED_NARROW_BUBBLE_TOP = 152
    const val EXPECTED_NARROW_ARROW_OFFSET = 44
}
