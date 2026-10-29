package br.gov.caixa.loterias.core.security.inspection

import android.content.Context
import br.gov.caixa.loterias.core.network.logging.NetworkTrafficSanitizer
import br.gov.caixa.loterias.core.network.logging.SanitizedNetworkInspector
import com.chuckerteam.chucker.api.BodyDecoder
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.ByteString

fun createNetworkInspector(context: Context): Interceptor = SanitizedNetworkInspector(
    ChuckerInterceptor.Builder(context)
        .collector(ChuckerCollector(context, showNotification = true, retentionPeriod = RetentionManager.Period.ONE_HOUR))
        .maxContentLength(64_000L)
        .redactHeaders("Authorization", "Cookie", "Set-Cookie", "X-Api-Key", "ocp-apim-subscription-key")
        .addBodyDecoder(object : BodyDecoder {
            override fun decodeRequest(request: Request, body: ByteString) = NetworkTrafficSanitizer.body(body.utf8())
            override fun decodeResponse(response: Response, body: ByteString) = NetworkTrafficSanitizer.body(body.utf8())
        })
        .createShortcut(false)
        .build(),
)
