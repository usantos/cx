package br.gov.caixa.loterias.feature.retail.presentation.mapper

import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** Geração de QR somente na CPU. O formato dos pixels e as opções do codificador reproduzem a imagem legada. */
internal class PrizeQrCodeGenerator(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val encode: (String, Int) -> BitMatrix = { value, size ->
        QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, size, size)
    },
) {
    suspend fun generate(value: String): IntArray = withContext(dispatcher) {
        val matrix = encode(value, SIZE)
        val pixels = IntArray(SIZE * SIZE)
        for (y in 0 until SIZE) {
            ensureActive()
            for (x in 0 until SIZE) {
                pixels[y * SIZE + x] = if (matrix[x, y]) BLACK else WHITE
            }
        }
        pixels
    }

    companion object {
        const val SIZE = 640
        private const val BLACK = -0x1000000
        private const val WHITE = -0x1
    }
}
