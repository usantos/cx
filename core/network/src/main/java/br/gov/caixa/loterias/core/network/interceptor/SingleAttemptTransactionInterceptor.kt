package br.gov.caixa.loterias.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink

/** Também impede que continuações HTTP, como 503 Retry-After: 0, reenviem uma transação. */
internal class SingleAttemptTransactionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(chain.request().singleAttempt())
}

internal fun Request.singleAttempt(): Request {
    val original = body ?: return this
    if (original.isOneShot()) return this
    return newBuilder().method(method, object : RequestBody() {
        override fun isOneShot() = true
        override fun isDuplex() = original.isDuplex()
        override fun contentType() = original.contentType()
        override fun contentLength() = original.contentLength()
        override fun writeTo(sink: BufferedSink) = original.writeTo(sink)
    }).build()
}
