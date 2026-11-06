package br.gov.caixa.loterias.core.network.provider

import br.gov.caixa.loterias.core.network.api.BackendApi

/** Cliente dedicado a transações, substituído quando a configuração verificada de pins muda. */
class PrizeBackendApiProvider {
    @Volatile private var api: BackendApi? = null

    fun current(): BackendApi = checkNotNull(api) { "API de resgate ainda não foi configurada" }

    fun replace(value: BackendApi) { api = value }
}
