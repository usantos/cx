package br.gov.caixa.loterias.feature.marketplace

import android.util.DisplayMetrics
import android.util.TypedValue
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.presentation.components.detailText
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailPoolInformation
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailHeader
import br.gov.caixa.loterias.feature.marketplace.presentation.components.marketplaceColors
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailXmlTextSizeUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun detail18spMatchesXmlAt420dpi() = checkDensity(420)
    @Test fun detail18spMatchesXmlAt450dpi() = checkDensity(450)
    @Test fun detail18spMatchesXmlAt480dpi() = checkDensity(480)

    @Test fun richDescriptionsKeepNativeAbsoluteSizeSpanWithoutXmlRounding() {
        val paint = TextPaint().apply { density = 450 / 160f }
        AbsoluteSizeSpan(18, true).updateMeasureState(paint)
        compose.setContent { CaixaDSTheme {
            CompositionLocalProvider(LocalDensity provides Density(450 / 160f, fontScale = 1f)) {
                DetailPoolInformation(detail())
            }
        } }
        assertEquals(paint.textSize, effectiveSize("1 aposta de 6 dezenas"), .01f)
        assertEquals(paint.textSize, effectiveSize("20 cotas disponíveis de 50"), .01f)
    }

    @Test fun specialLotofacilScalesAlreadyRoundedXmlTitleByEightyPercent() {
        val nativeSize = nativeXmlSize(18, 450) * .8f
        compose.setContent { CaixaDSTheme {
            CompositionLocalProvider(LocalDensity provides Density(450 / 160f, fontScale = 1f)) {
                DetailHeader(detail().copy(modality = "LOTOFACIL", contestType = 2), marketplaceColors("LOTOFACIL"), {})
            }
        } }
        assertEquals(nativeSize, effectiveSize("lotofácil da \nindependência"), .01f)
    }

    private fun checkDensity(dpi: Int) {
        val nativeSize = nativeXmlSize(18, dpi)
        compose.setContent { CaixaDSTheme {
            CompositionLocalProvider(LocalDensity provides Density(dpi / 160f, fontScale = 1f)) {
                Text("Lotérica da Praça Central", style = detailText(18))
            }
        } }
        assertEquals("XML TextView rounds textSize to pixels before shaping at ${dpi}dpi", nativeSize, effectiveSize("Lotérica da Praça Central"), .01f)
    }

    private fun effectiveSize(text: String): Float {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        return with(layout.layoutInput.density) { layout.layoutInput.style.fontSize.toPx() }
    }

    private fun nativeXmlSize(sp: Int, dpi: Int): Float {
        val metrics = DisplayMetrics().apply {
            setTo(InstrumentationRegistry.getInstrumentation().context.resources.displayMetrics)
            densityDpi = dpi
            density = dpi / 160f
            scaledDensity = density
        }
        // Codificação complexa do AAPT2 de uma dimensão SP inteira, decodificada pelo próprio Android.
        val dimension = (sp shl TypedValue.COMPLEX_MANTISSA_SHIFT) or
            (TypedValue.COMPLEX_RADIX_23p0 shl TypedValue.COMPLEX_RADIX_SHIFT) or TypedValue.COMPLEX_UNIT_SP
        return TypedValue.complexToDimensionPixelSize(dimension, metrics).toFloat()
    }

    private fun detail() = MarketplacePoolDetail("B1", "MEGA_SENA", 3070, 10, "Lotérica", "1", "Cidade - SP",
        "21/10/2026", "150000000", 20, 50, 1, 6, "10", "0.50", "10.50", emptyList())
}
