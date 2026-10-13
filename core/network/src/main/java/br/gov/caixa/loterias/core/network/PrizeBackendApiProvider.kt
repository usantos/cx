package br.gov.caixa.loterias.core.network

/** Dedicated transaction client, replaced when the verified pin configuration changes. */
class PrizeBackendApiProvider {
    @Volatile private var api: LegacyBackendApi? = null

    fun current(): LegacyBackendApi = checkNotNull(api) { "API de resgate ainda não foi configurada" }

    fun replace(value: LegacyBackendApi) { api = value }
}
