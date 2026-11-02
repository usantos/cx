package br.gov.caixa.loterias.feature.retail.presentation.route

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeReceiptDelivery
import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText
import java.io.IOException

internal fun openPrizeReceiptPdf(context: Context, delivery: PrizeReceiptDelivery, uri: Uri) {
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/pdf")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
        delivery.dismissViewerFailure()
    } catch (_: ActivityNotFoundException) {
        delivery.viewerFailed(uri.toString())
    } catch (_: SecurityException) {
        delivery.viewerFailed(uri.toString())
    }
}

internal fun savePrizeReceiptPdf(resolver: ContentResolver, delivery: PrizeReceiptDelivery, uri: Uri) {
    // A operação retida captura a fronteira do provedor, nunca esta Activity ou sua interface.
    delivery.save(uri.toString()) { bytes ->
        val output = resolver.openOutputStream(uri)
            ?: throw IOException(messageText("nao_foi_possivel_abrir_o_comprov_c2df10"))
        output.use { it.write(bytes) }
    }
}
