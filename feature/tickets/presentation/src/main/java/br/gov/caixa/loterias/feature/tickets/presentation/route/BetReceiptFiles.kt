package br.gov.caixa.loterias.feature.tickets.presentation.route

import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetReceipt

/** Reproduz a abertura direta nativa sem exigir permissão de armazenamento ou diálogo para salvar. */
fun writeBetReceipt(context: Context, receipt: BetReceipt): Uri {
    require(receipt.bytes.take(5).toByteArray().contentEquals("%PDF-".toByteArray())) { messageText("comprovante_pdf_invalido_bede87") }
    val directory = File(context.cacheDir, "receipts").apply { check(isDirectory || mkdirs()) }
    val file = File(directory, File(receipt.fileName).name)
    file.outputStream().use { it.write(receipt.bytes) }
    return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
}

fun betReceiptViewIntent(uri: Uri): Intent = Intent(Intent.ACTION_VIEW)
    .setDataAndType(uri, "application/pdf")
    .apply { clipData = ClipData.newRawUri("Comprovante", uri) }
    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)

fun openBetReceipt(context: Context, uri: Uri): Boolean = runCatching {
    context.startActivity(betReceiptViewIntent(uri))
}.isSuccess
