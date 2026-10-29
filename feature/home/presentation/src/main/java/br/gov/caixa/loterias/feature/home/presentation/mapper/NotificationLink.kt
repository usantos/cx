package br.gov.caixa.loterias.feature.home.presentation.mapper

/** Somente links explícitos de navegador; nunca inicia esquemas intent/javascript/file/data vindos de textos do servidor. */
internal fun notificationBrowserLink(value: String?): String? = try {
    val normalized = value?.trim().orEmpty()
    val uri = java.net.URI(normalized)
    normalized.takeIf {
        uri.scheme?.lowercase() in setOf("https", "http") &&
            !uri.host.isNullOrBlank() && uri.rawUserInfo == null
    }
} catch (_: java.net.URISyntaxException) {
    null
}
