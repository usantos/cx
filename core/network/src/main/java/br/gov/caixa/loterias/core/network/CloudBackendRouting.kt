package br.gov.caixa.loterias.core.network

import java.io.IOException
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/** Routes the migrated publico/logado contracts using the authenticated SILCE configuration. */
class CloudBackendRouting(private val source: HttpUrl, private val production: Boolean) : Interceptor {
    private class Configuration(val baseUrl: HttpUrl, val subscriptionKey: String)
    @Volatile private var configuration: Configuration? = null

    fun update(parameters: AppParametersDto) {
        val value = if (production) parameters.productionCartBaseUrl else parameters.pilotCartBaseUrl
        val url = value?.trim()?.trimEnd('/')?.plus('/')?.toHttpUrlOrNull() ?: return
        if (url.scheme != "https" || url.username.isNotEmpty() || url.password.isNotEmpty() ||
            url.query != null || url.fragment != null
        ) return
        // The network client's PinnedHostInterceptor and CertificatePinner also validate the target.
        val key = parameters.cloudSubscriptionKey?.takeIf(String::isNotBlank) ?: return
        configuration = Configuration(url, key)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        if (url.scheme != source.scheme || url.host != source.host || url.port != source.port ||
            !url.encodedPath.startsWith(source.encodedPath)
        ) return chain.proceed(request)
        val relative = url.encodedPath.removePrefix(source.encodedPath)
        if (!relative.startsWith("publico/") && !relative.startsWith("logado/")) return chain.proceed(request)
        val target = configuration ?: throw IOException("Configuração do serviço de apostas indisponível")
        val destination = target.baseUrl.newBuilder()
            .addEncodedPathSegments(relative).encodedQuery(url.encodedQuery).build()
        return chain.proceed(request.newBuilder().url(destination)
            .header("Subcanal", "3")
            .header("Cookie", "security=false")
            .header("Content-Type", "application/json")
            .header("ocp-apim-subscription-key", target.subscriptionKey)
            .build())
    }
}
