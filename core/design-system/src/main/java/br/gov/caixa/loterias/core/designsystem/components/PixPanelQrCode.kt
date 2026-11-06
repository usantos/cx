package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/** Renderiza o mesmo conteúdo copiado, com margem branca e módulos alinhados aos pixels. */
@Composable
internal fun PixPanelQrCode(copyPaste: String) {
    val matrix = remember(copyPaste) { pixQrMatrix(copyPaste) } ?: return
    val description = stringResource(R.string.pix_qr_code_description)
    Canvas(
        Modifier.size(QR_SIZE_DP.dp).uiId("ds.0147")
            .semantics { contentDescription = description }
    ) {
        drawRect(Color.White)
        val scale = (size.minDimension / matrix.width).toInt().coerceAtLeast(1)
        val left = ((size.width - matrix.width * scale) / 2).toInt()
        val top = ((size.height - matrix.height * scale) / 2).toInt()
        for (row in 0 until matrix.height) {
            for (column in 0 until matrix.width) {
                if (matrix[column, row]) {
                    drawRect(
                        Color.Black,
                        Offset((left + column * scale).toFloat(), (top + row * scale).toFloat()),
                        Size(scale.toFloat(), scale.toFloat())
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(QR_SPACING_DP.dp))
}

internal fun pixQrMatrix(copyPaste: String): BitMatrix? {
    if (copyPaste.isBlank()) return null
    return try {
        QRCodeWriter().encode(
            copyPaste.trim(),
            BarcodeFormat.QR_CODE,
            0,
            0,
            mapOf(
                EncodeHintType.MARGIN to QUIET_ZONE_MODULES,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
        )
    } catch (_: WriterException) {
        null
    }
}

private const val QR_SIZE_DP = 240
private const val QR_SPACING_DP = 16
private const val QUIET_ZONE_MODULES = 4
