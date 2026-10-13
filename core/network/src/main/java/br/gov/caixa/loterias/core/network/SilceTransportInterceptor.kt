package br.gov.caixa.loterias.core.network

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer

/** Wire compatibility with ServiceConnection/Bin/SilceCrypto in the legacy client. */
internal class SilceTransportInterceptor(private val baseUrl: HttpUrl) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        if (url.scheme != baseUrl.scheme || url.host != baseUrl.host || url.port != baseUrl.port ||
            !baseUrl.encodedPath.startsWith("/silce-servico-rest/rest/") ||
            !url.encodedPath.startsWith(baseUrl.encodedPath)
        ) return chain.proceed(request)

        val relative = url.encodedPath.removePrefix(baseUrl.encodedPath)
        val builder = request.newBuilder()
            .header("Cookie", "security=false")
            .header("Subcanal", "3")
            .header("Content-Type", "application/json")
        // The cloud contracts use ordinary paths, queries and JSON bodies.
        if (relative.startsWith("publico/") || relative.startsWith("logado/")) {
            return chain.proceed(builder.build())
        }
        val path = if (baseUrl.encodedPath.endsWith("/v1/")) {
            Base64.getEncoder().encodeToString(relative.toByteArray(Charsets.UTF_8))
        } else relative
        val wireUrl = url.newBuilder().encodedPath(baseUrl.encodedPath + path).query(null)
        if (url.querySize > 0) {
            val parameters = buildString {
                repeat(url.querySize) { index ->
                    url.queryParameterValue(index)?.let { value ->
                        append(url.queryParameterName(index)).append("==").append(value).append("&&")
                    }
                }
            }
            wireUrl.addPathSegment("").addQueryParameter("q", envelope(parameters.toByteArray(Charsets.UTF_8)).toString(Charsets.ISO_8859_1))
        }
        builder.url(wireUrl.build())
        request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            val json = buffer.readUtf8().ifEmpty { "{}" }.toByteArray(Charsets.ISO_8859_1)
            val encoded = Base64.getEncoder().encodeToString(envelope(json))
            builder.method(request.method, encoded.toRequestBody(body.contentType()))
        }
        return chain.proceed(builder.build())
    }

    private fun envelope(value: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(KEY, "AES"), IvParameterSpec(IV))
        return cipher.doFinal(value) + SEPARATOR + KEY + SEPARATOR + IV
    }

    private companion object {
        // Protocol framing constants from SilceCrypto; TLS remains responsible for transport security.
        val KEY = Base64.getDecoder().decode("QEm06ZAThg1E314zziahsg==")
        val IV = Base64.getDecoder().decode("8hQLtNV44Y1bIeONmlr+Dg==")
        val SEPARATOR = "!#!#!".toByteArray(Charsets.ISO_8859_1)
    }
}
