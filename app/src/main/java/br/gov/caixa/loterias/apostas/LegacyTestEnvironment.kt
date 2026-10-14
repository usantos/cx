package br.gov.caixa.loterias.apostas

/** Endpoint identities from the legacy TQS BuildConfig, not an editable URL allowlist. */
enum class LegacyTestEnvironment(val apiBaseUrl: String) {
    EXTERNO("https://simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/"),
    INTERNO("https://simlo.tqs.caixa/silce-servico-rest/rest/v1/"),
    INTERNO_ESTEIRA("https://silce-simlo.tqs.caixa/silce-servico-rest/rest/v1/"),
    EXTERNO_ESTEIRA("https://silce.simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/"),
    // AmbienteEnum used HMP; the corresponding legacy BuildConfig suffix was HML.
    HMP("https://nbcfvgbhnjmkasdef-lce.caixa.gov.br/"),
    PILOTO("https://pilotoloterias.caixa.gov.br/silce-servico-rest/rest/v1/"),
}

/** The menu must describe the effective API configuration, never a stale stored selection. */
internal fun homeEnvironmentLabel(flavor: String, effectiveApiBaseUrl: String): String? {
    if (flavor != "tqs" && flavor != "automacao") return null
    return LegacyTestEnvironment.entries.firstOrNull {
        it.apiBaseUrl.trimEnd('/') == effectiveApiBaseUrl.trim().trimEnd('/')
    }?.name
}
