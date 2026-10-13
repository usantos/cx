package br.gov.caixa.loterias.core.network

import java.net.URI

/** Stable indirection that lets TLS pins rotate without recreating repositories or use cases. */
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

/** Stable indirection for the explicit compatibility contract during incremental migration. */
class LegacyBackendApiProvider {
    @Volatile private var api: LegacyBackendApi? = null

    fun current(): LegacyBackendApi = checkNotNull(api) { "LegacyBackendApi ainda não foi configurada" }

    fun replace(value: LegacyBackendApi) {
        api = value
    }
}

/** Allows the signed server configuration to switch bet-history backends at runtime. */
class BetsHistoryApiProvider {
    @Volatile private var api: BetsHistoryApi? = null
    @Volatile private var dynamicFactory: ((String) -> BetsHistoryApi)? = null
    @Volatile private var pinnedHosts: Set<String> = emptySet()
    private val dynamicApis = mutableMapOf<String, BetsHistoryApi>()

    fun current(): BetsHistoryApi = checkNotNull(api) { "BetsHistoryApi ainda não foi configurada" }

    /**
     * Returns a client only when the remotely supplied URL resolves to a host present in the
     * currently verified SPKI key set. Invalid/unpinned URLs are deliberately rejected so the
     * caller can fall back to the statically configured SILCE service.
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
