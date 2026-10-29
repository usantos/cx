package br.gov.caixa.loterias.feature.marketplace

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailPurchasePanel
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private val quinaBlue = Color(0xFF2E3192)
private val lotofacilPurple = Color(0xFF91278F)

class MarketplacePurchaseModalityColorTest {
    @get:Rule val compose = createComposeRule()

    @Test fun quinaAddActionUsesQuinaBlue() = checkColor("QUINA", quinaBlue)

    @Test fun lotofacilAddActionUsesLotofacilPurple() = checkColor("LOTOFACIL", lotofacilPurple)

    private fun checkColor(modality: String, expected: Color) {
        val detail = MarketplacePoolDetail(
            "B1", modality, 1, 1, "Lotérica", "1", "Cidade - SP",
            "26/10/2026", "1000000", AVAILABLE_SHARES, TOTAL_SHARES, 1, NUMBER_COUNT, "7", "2.61", "9.61", emptyList()
        )
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                DetailPurchasePanel(
                    MarketplaceDetailUiState(detail = detail, isLoading = false),
                    {},
                    {},
                    {},
                    BigDecimal.ZERO,
                    0,
                    null
                )
            }
        }
        val pixels = compose.onNodeWithTag("pool-detail-add-to-cart").captureToImage().toPixelMap()
        capturePreview("detail-$modality")
        assertEquals(
            "Purchase action follows $modality",
            expected.toArgb(),
            pixels[pixels.width / 2, pixels.height / SAMPLE_DIVISOR].toArgb()
        )
    }
    private fun capturePreview(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, output)
            val chunks = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP).chunked(LOG_CHUNK_SIZE)
            chunks.forEachIndexed { index, chunk -> Log.i("MarketplacePreview", "$name:$index/${chunks.size}:$chunk") }
        } finally {
            bitmap.recycle()
        }
    }

    private companion object {
        const val AVAILABLE_SHARES = 10
        const val TOTAL_SHARES = 20
        const val NUMBER_COUNT = 6
        const val SAMPLE_DIVISOR = 4
        const val PNG_QUALITY = 100
        const val LOG_CHUNK_SIZE = 3000
    }
}
