package br.gov.caixa.loterias.feature.cart

import androidx.compose.ui.test.height
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRule
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import kotlin.math.roundToInt
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CartEmptyMessageStyleUiTest {
    @get:Rule val compose = createFeatureComposeRule()

    @Test fun emptyMessageUsesNativeCaixaSemiboldTwelveSpAndIconSize() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContentDelivered(
                    br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState(),
                    androidx.compose.foundation.layout.PaddingValues(),
                    emptyList(),
                    0L,
                    {},
                    {},
                    {}
                )
            }
        }
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        compose.onNodeWithText("Seu carrinho está vazio").performSemanticsAction(
            androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult
        ) { it(layouts) }
        val style = layouts.single().layoutInput.style
        assertEquals(br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily, style.fontFamily)
        val nativeTextPixels = with(compose.density) { 12.sp.toPx().roundToInt().toFloat() }
        assertEquals(nativeTextPixels, with(compose.density) { style.fontSize.toPx() }, TEXT_PIXEL_TOLERANCE)
        assertEquals(androidx.compose.ui.text.font.FontWeight.SemiBold, style.fontWeight)
        val icon = compose.onNodeWithTag("cart-empty-icon").fetchSemanticsNode().boundsInRoot
        assertEquals(EMPTY_ICON_SIZE_DP * compose.density.density, icon.width, 1f)
        assertEquals(EMPTY_ICON_SIZE_DP * compose.density.density, icon.height, 1f)
    }
}

private const val TEXT_PIXEL_TOLERANCE = 0.01f
private const val EMPTY_ICON_SIZE_DP = 140f
