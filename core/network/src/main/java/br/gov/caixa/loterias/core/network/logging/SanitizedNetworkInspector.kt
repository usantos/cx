package br.gov.caixa.loterias.core.network.logging

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer

/** Fornece uma cópia sanitizada ao inspetor; a chamada de rede sempre usa o original. */
class SanitizedNetworkInspector(private val inspector: Interceptor) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val safe = original.newBuilder().url(NetworkTrafficSanitizer.url(original.url)).apply {
            original.headers.names().filter(NetworkTrafficSanitizer::sensitive).forEach {
                header(it, "[REDACTED]")
            }
            original.body?.let { body ->
                val text = runCatching {
                    if (body.isOneShot() || body.isDuplex() || body.contentLength() !in 0..64_000L) {
                        NetworkTrafficSanitizer.OMITTED_BODY
                    } else {
                        val buffer = Buffer()
                        body.writeTo(buffer)
                        NetworkTrafficSanitizer.body(buffer.readUtf8())
                    }
                }.getOrDefault(NetworkTrafficSanitizer.OMITTED_BODY)
                method(original.method, text.toRequestBody(body.contentType()))
            }
        }.build()
        return inspector.intercept(object : Interceptor.Chain by chain {
            override fun request(): Request = safe
            override fun proceed(request: Request): Response =
                chain.proceed(original).newBuilder().request(safe).build()
        }).newBuilder().request(original).build()
    }
}
