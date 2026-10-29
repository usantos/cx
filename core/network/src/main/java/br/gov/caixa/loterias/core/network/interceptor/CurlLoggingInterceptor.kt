package br.gov.caixa.loterias.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import okio.Buffer
import br.gov.caixa.loterias.core.network.logging.NetworkTrafficSanitizer

fun interface NetworkLogSink {
    fun log(message: String)
}

data class NetworkDebugOptions(
    val enabled: Boolean = false,
    val sink: NetworkLogSink = NetworkLogSink {},
    val includeBodies: Boolean = false,
    val inspector: Interceptor? = null,
)

/** Registra a estrutura da requisição e JSON sanitizado opcional sem consumir a resposta do chamador. */
class CurlLoggingInterceptor(
    private val options: NetworkDebugOptions,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!options.enabled) return chain.proceed(chain.request())

        val request = chain.request()
        val started = System.nanoTime()
        options.sink.log(toCurl(request))
        if (options.includeBodies) request.body?.let { body ->
            val safe = if (body.isOneShot() || body.isDuplex() || body.contentLength() !in 0..MAX_BODY_BYTES) {
                "[streaming, one-shot or oversized request body omitted]"
            } else runCatching {
                val buffer = Buffer()
                body.writeTo(buffer)
                NetworkTrafficSanitizer.body(buffer.readUtf8())
            }.getOrDefault(NetworkTrafficSanitizer.OMITTED_BODY)
            options.sink.log("REQUEST BODY $safe")
        }
        return try {
            chain.proceed(request).also { response ->
                val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started)
                options.sink.log("HTTP ${request.method} ${request.url.host} -> ${response.code} (${elapsed}ms)")
                if (options.includeBodies) {
                    val safe = runCatching {
                        NetworkTrafficSanitizer.body(response.peekBody(MAX_BODY_BYTES).string())
                    }.getOrDefault(NetworkTrafficSanitizer.OMITTED_BODY)
                    options.sink.log("RESPONSE BODY $safe")
                }
            }
        } catch (failure: Exception) {
            options.sink.log("HTTP ${request.method} ${request.url.host} -> falha ${failure.javaClass.simpleName}")
            throw failure
        }
    }

    internal fun toCurl(request: okhttp3.Request): String {
        val safeUrl = NetworkTrafficSanitizer.url(request.url)
        val command = mutableListOf("curl", "--request", shell(request.method), shell(safeUrl.toString()))
        request.headers.forEach { (name, value) ->
            val safeValue = if (NetworkTrafficSanitizer.sensitive(name)) NetworkTrafficSanitizer.REDACTED else value
            command += listOf("--header", shell("$name: $safeValue"))
        }
        if (request.body != null) {
            command += listOf("--data-binary", shell(REDACTED_BODY))
        }
        return command.joinToString(" ")
    }

    private fun shell(value: String) = "'${value.replace("'", "'\"'\"'")}'"

    private companion object {
        const val MAX_BODY_BYTES = 64_000L
        const val REDACTED_BODY = "[BODY REDACTED]"
    }
}
