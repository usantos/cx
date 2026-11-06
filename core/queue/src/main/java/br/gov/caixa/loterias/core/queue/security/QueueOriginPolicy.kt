package br.gov.caixa.loterias.core.queue.security

import java.net.URI

/** As páginas da fila são hospedadas pelo FilaBRA; verificar apenas o esquema não autoriza uma origem. */
object QueueOriginPolicy {
    fun origin(url: String): String? = runCatching {
        val uri = URI(url)
        val host = uri.host?.lowercase() ?: return null
        if (!uri.scheme.equals("https", ignoreCase = true) || uri.rawUserInfo != null ||
            uri.port !in listOf(-1, 443) || host.endsWith('.') || uri.rawAuthority?.contains('\\') == true) return null
        "https://$host"
    }.getOrNull()

    fun isQueuePage(url: String): Boolean = origin(url)?.removePrefix("https://")
        ?.let { it.endsWith(".filabra.com") } == true

    fun isSameQueueOrigin(initialUrl: String, candidate: String): Boolean =
        isQueuePage(initialUrl) && origin(initialUrl) == origin(candidate)

    fun isPassTarget(url: String): Boolean {
        val host = origin(url)?.removePrefix("https://") ?: return false
        return host.endsWith(".filabra.com") || host.endsWith(".caixa.gov.br")
    }
}
