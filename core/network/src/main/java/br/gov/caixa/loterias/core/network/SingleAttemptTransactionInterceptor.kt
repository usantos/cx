package br.gov.caixa.loterias.core.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink

/** Also prevents HTTP follow-ups such as 503 Retry-After: 0 from resending a transaction. */
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
