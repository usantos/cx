package br.gov.caixa.loterias.core.network.provider

import java.net.URI
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import br.gov.caixa.loterias.core.network.api.RecargaPayApi
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.model.AppParametersDto

/** Indireção estável que permite rotacionar pins TLS sem recriar repositories ou casos de uso. */
class LoteriasApiProvider {
    @Volatile private var parametersConsumer: (AppParametersDto) -> Unit = {}

    fun onParametersLoaded(consumer: (AppParametersDto) -> Unit) {
        parametersConsumer = consumer
    }

    fun updateParameters(parameters: AppParametersDto) {
        parametersConsumer(parameters)
    }

    @Volatile private var api: LoteriasApi? = null

    fun current(): LoteriasApi = checkNotNull(api) { "LoteriasApi ainda não foi configurada" }

    fun replace(value: LoteriasApi) {
        api = value
    }
}

/** Indireção estável para o contrato explícito de compatibilidade durante a migração incremental. */
class BackendApiProvider {
    @Volatile private var api: BackendApi? = null

    fun current(): BackendApi = checkNotNull(api) { "BackendApi ainda não foi configurada" }

    fun replace(value: BackendApi) {
        api = value
    }
}

/** Permite que a configuração assinada do servidor troque o backend do histórico em execução. */
class BetsHistoryApiProvider {
    @Volatile private var api: BetsHistoryApi? = null
    @Volatile private var dynamicFactory: ((String) -> BetsHistoryApi)? = null
    @Volatile private var pinnedHosts: Set<String> = emptySet()
    private val dynamicApis = mutableMapOf<String, BetsHistoryApi>()

    fun current(): BetsHistoryApi = checkNotNull(api) { "BetsHistoryApi ainda não foi configurada" }

    /**
     * Retorna um cliente somente quando a URL remota aponta para um host presente no
     * conjunto de chaves SPKI verificado. Rejeita URLs inválidas ou sem pins para que o
     * chamador possa usar o serviço SILCE configurado estaticamente.
     */
    fun current(baseUrl: String): BetsHistoryApi? {
        val normalized = runCatching { NetworkClientFactory.normalizeBaseUrl(baseUrl) }.getOrNull() ?: return null
        val host = runCatching { URI(normalized).host?.lowercase() }.getOrNull() ?: return null
        if (host !in pinnedHosts) return null
        return synchronized(dynamicApis) {
            dynamicApis.getOrPut(normalized) {
                dynamicFactory?.invoke(normalized) ?: return@synchronized null
            }
        }
    }

    fun replace(value: BetsHistoryApi) {
        api = value
    }

    fun configureDynamicClients(
        verifiedPinnedHosts: Set<String>,
        factory: (String) -> BetsHistoryApi,
    ) {
        synchronized(dynamicApis) { dynamicApis.clear() }
        pinnedHosts = verifiedPinnedHosts.map(String::lowercase).toSet()
        dynamicFactory = factory
    }
}

data class PaymentApis(
    val mercadoPago: MercadoPagoApi,
    val recargaPay: RecargaPayApi,
)

class PaymentApiProvider {
    @Volatile private var apis: PaymentApis? = null

    fun current(): PaymentApis = checkNotNull(apis) { "APIs de pagamento ainda não foram configuradas" }

    fun replace(value: PaymentApis) {
        apis = value
    }
}
