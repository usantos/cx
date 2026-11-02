package br.gov.caixa.loterias.core.designsystem.testing

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import java.io.File

/** Exportação opcional de fixtures visuais; não lê dados do app nem grava imagens no repositório. */
internal fun capturePreviewEvidence(rule: ComposeContentTestRule, name: String) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return
    val directory = System.getProperty("simlo.preview.screenshots.dir") ?: return
    val destination = File(directory).apply { mkdirs() }
    File(destination, "$name.png").outputStream().use {
        check(rule.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, it))
    }
}
private const val PNG_QUALITY = 100
