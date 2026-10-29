package br.gov.caixa.loterias.feature.retail.presentation.components

import br.gov.caixa.loterias.feature.retail.R as LocalizedR

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import br.gov.caixa.loterias.feature.retail.presentation.mapper.PrizeQrCodeGenerator

@Composable
internal fun PrizeQrCodeImage(value: String, render: suspend (String) -> ImageBitmap = { renderPrizeQrCode(it) }) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    key(value) {
        // Um novo valor possui um novo produtor; assim, um QR antigo cancelado não pode substituir o atual.
        val bitmap by produceState<ImageBitmap?>(null, value) {
            this.value = null
            this.value = render(value)
        }
        val image = bitmap
        if (image == null) {
            Box(Modifier.size(200.dp))
        } else {
            Image(image, uiStrings.getString(LocalizedR.string.ui_qr_code_de_resgate_81b8fb), Modifier.size(200.dp))
        }
    }
}

internal suspend fun renderPrizeQrCode(value: String, generator: PrizeQrCodeGenerator = PrizeQrCodeGenerator()): ImageBitmap =
    withContext(Dispatchers.Default) {
        val pixels = generator.generate(value)
        ensureActive()
        Bitmap.createBitmap(pixels, PrizeQrCodeGenerator.SIZE, PrizeQrCodeGenerator.SIZE, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
