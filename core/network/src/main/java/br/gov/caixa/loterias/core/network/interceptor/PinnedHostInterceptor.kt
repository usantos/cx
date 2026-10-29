package br.gov.caixa.loterias.core.network.interceptor

import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import javax.net.ssl.SSLPeerUnverifiedException
import java.util.concurrent.atomic.AtomicBoolean
import okhttp3.Interceptor
import okhttp3.Response

/**
 * CertificatePinner aceita hosts não listados sem verificar pins. Exige cobertura explícita
 * antes de enviar dados do app. Instala nos níveis de aplicação e rede para impedir que redirecionamentos
 * saiam do conjunto assinado/local de hosts. TLS continua validando certificados e nomes de hosts.
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
            // Não expõe URL, parâmetros, payload ou credenciais nesta exceção.
            throw SSLPeerUnverifiedException("O destino não possui pinagem TLS configurada.")
        }
        return chain.proceed(request)
    }
}
