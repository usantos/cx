package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment

/** O menu deve descrever a configuração efetiva da API, nunca uma seleção armazenada desatualizada. */
fun homeEnvironmentLabel(flavor: String, effectiveApiBaseUrl: String): String? {
    if (flavor != "tqs" && flavor != "automacao") return null
    return TestEnvironment.entries.firstOrNull {
        it.apiBaseUrl.trimEnd('/') == effectiveApiBaseUrl.trim().trimEnd('/')
    }?.name
}
