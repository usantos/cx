package br.gov.caixa.loterias.core.domain.environment.model

/** Identidades dos endpoints do BuildConfig TQS legado, não uma lista editável de URLs permitidas. */
enum class TestEnvironment(val apiBaseUrl: String) {
    EXTERNO("https://simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/"),
    INTERNO("https://simlo.tqs.caixa/silce-servico-rest/rest/v1/"),
    INTERNO_ESTEIRA("https://silce-simlo.tqs.caixa/silce-servico-rest/rest/v1/"),
    EXTERNO_ESTEIRA("https://silce.simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/"),
    // AmbienteEnum usava HMP; o sufixo correspondente no BuildConfig legado era HML.
    HMP("https://nbcfvgbhnjmkasdef-lce.caixa.gov.br/"),
    PILOTO("https://pilotoloterias.caixa.gov.br/silce-servico-rest/rest/v1/"),
}
