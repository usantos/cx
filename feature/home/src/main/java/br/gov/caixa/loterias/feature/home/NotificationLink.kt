package br.gov.caixa.loterias.feature.home

/** Only explicit browser links; never launch intent/javascript/file/data schemes from server text. */
internal fun notificationBrowserLink(value: String?): String? = try {
    val normalized = value?.trim().orEmpty()
    val uri = java.net.URI(normalized)
    normalized.takeIf { uri.scheme?.lowercase() in setOf("https", "http") &&
        !uri.host.isNullOrBlank() && uri.rawUserInfo == null }
} catch (_: java.net.URISyntaxException) { null }
