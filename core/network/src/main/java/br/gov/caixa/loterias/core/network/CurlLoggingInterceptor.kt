package br.gov.caixa.loterias.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import java.util.concurrent.TimeUnit

fun interface NetworkLogSink {
    fun log(message: String)
}

data class NetworkDebugOptions(
    val enabled: Boolean = false,
    val sink: NetworkLogSink = NetworkLogSink {},
)

/** Logs a reproducible request shape without credentials, sensitive query values or bodies. */
class CurlLoggingInterceptor(
    private val options: NetworkDebugOptions,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!options.enabled) return chain.proceed(chain.request())

        val request = chain.request()
        val started = System.nanoTime()
        options.sink.log(toCurl(request))
        return try {
            chain.proceed(request).also { response ->
                val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started)
                options.sink.log("HTTP ${request.method} ${request.url.host} -> ${response.code} (${elapsed}ms)")
            }
        } catch (failure: Exception) {
            options.sink.log("HTTP ${request.method} ${request.url.host} -> falha ${failure.javaClass.simpleName}")
            throw failure
        }
    }

    internal fun toCurl(request: okhttp3.Request): String {
        val safeUrl = request.url.newBuilder().apply {
            request.url.queryParameterNames.forEach { name ->
                if (isSensitive(name)) setQueryParameter(name, REDACTED)
            }
        }.build()
        val command = mutableListOf("curl", "--request", shell(request.method), shell(safeUrl.toString()))
        request.headers.forEach { (name, value) ->
            val safeValue = if (isSensitive(name)) REDACTED else value
            command += listOf("--header", shell("$name: $safeValue"))
        }
        if (request.body != null) {
            command += listOf("--data-binary", shell(REDACTED_BODY))
        }
        return command.joinToString(" ")
    }

    private fun isSensitive(name: String): Boolean {
        val normalized = name.lowercase(Locale.US)
        return SENSITIVE_NAMES.any { marker -> marker in normalized }
    }

    private fun shell(value: String) = "'${value.replace("'", "'\"'\"'")}'"

    private companion object {
        const val REDACTED = "██REDACTED██"
        const val REDACTED_BODY = "[BODY REDACTED]"
        val SENSITIVE_NAMES = setOf(
            "authorization", "cookie", "token", "secret", "password", "senha", "cpf",
            "account", "conta", "card", "cartao", "cartão", "cvv", "x-api-key", "signature",
            "email", "document", "bin", "public_key", "subscription-key",
        )
    }
}
