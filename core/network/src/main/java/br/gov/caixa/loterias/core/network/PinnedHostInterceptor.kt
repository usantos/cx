package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.keymanagement.PublicKeySet
import javax.net.ssl.SSLPeerUnverifiedException
import java.util.concurrent.atomic.AtomicBoolean
import okhttp3.Interceptor
import okhttp3.Response

/**
 * CertificatePinner accepts an unlisted host without checking pins. Require explicit coverage
 * before application data is sent. Install at both application and network levels so redirects
 * cannot leave the signed/local host set. Certificate/hostname validation still belongs to TLS.
 */
internal class PinnedHostInterceptor(
    keySet: PublicKeySet,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1_000 },
) : Interceptor {
    private val coveredHosts = keySet.hosts.filter { it.pins.isNotEmpty() }.map { it.hostname }.toSet()
    private val expiresAtEpochSeconds = keySet.expiresAtEpochSeconds
    private val expired = AtomicBoolean(false)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (expiresAtEpochSeconds <= nowEpochSeconds()) expired.set(true)
        if (expired.get()) {
            throw SSLPeerUnverifiedException("A configuração de pinagem TLS expirou. Atualize a configuração para continuar.")
        }
        if (!request.url.isHttps || request.url.host !in coveredHosts) {
            // Do not expose URL, query parameters, payload, or credentials in this exception.
            throw SSLPeerUnverifiedException("O destino não possui pinagem TLS configurada.")
        }
        return chain.proceed(request)
    }
}
