package br.gov.caixa.loterias.feature.cart

import android.content.res.Configuration
import android.view.ContextThemeWrapper
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import androidx.core.content.res.ResourcesCompat
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContent
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CurrentCartDisplayDensityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun emptyMessageMatchesNativeXmlAtPhone450Dpi() = compareNativeText(450)
    @Test fun emptyMessageMatchesNativeXmlAt420Dpi() = compareNativeText(420)
    @Test fun emptyMessageMatchesNativeXmlAt480Dpi() = compareNativeText(480)

    private fun compareNativeText(dpi: Int) {
        val density = Density(dpi / 160f, 1f)
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides density) {
                CaixaDSTheme {
                    CurrentCartContent(CartUiState(), PaddingValues(), emptyList(), 0L, {}, {}, {})
                }
            }
        }
        var expectedWidth = 0
        var expectedHeight = 0
        compose.runOnIdle {
            val base = InstrumentationRegistry.getInstrumentation().targetContext
            val configuration = Configuration(base.resources.configuration).apply {
                densityDpi = dpi
                fontScale = 1f
            }
            val context = ContextThemeWrapper(base.createConfigurationContext(configuration),
                android.R.style.Theme_Material_Light_NoActionBar)
            // Dimensão compilada de 12sp, convertida pela mesma API Android usada na inflação do XML.
            // Mantém a referência nativa sem adicionar leiautes XML ao projeto Compose.
            val compiledSize = (12 shl TypedValue.COMPLEX_MANTISSA_SHIFT) or TypedValue.COMPLEX_UNIT_SP
            val pixels = TypedValue.complexToDimensionPixelSize(compiledSize, context.resources.displayMetrics)
            val reference = TextView(context).apply {
                text = "Seu carrinho está vazio"
                typeface = ResourcesCompat.getFont(context,
                    br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels.toFloat())
                includeFontPadding = true
            }
            reference.measure(View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            expectedWidth = reference.measuredWidth
            expectedHeight = reference.measuredHeight
        }
        val actual = compose.onNodeWithText("Seu carrinho está vazio").fetchSemanticsNode().boundsInRoot
        assertEquals("Native XML text width at $dpi dpi", expectedWidth.toFloat(), actual.width, 1f)
        assertEquals("Native XML text height at $dpi dpi", expectedHeight.toFloat(), actual.height, 1f)
    }
}
