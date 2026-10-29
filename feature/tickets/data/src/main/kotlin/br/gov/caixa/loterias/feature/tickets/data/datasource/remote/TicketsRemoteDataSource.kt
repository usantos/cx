package br.gov.caixa.loterias.feature.tickets.data.datasource.remote

import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource
import br.gov.caixa.loterias.core.data.mapper.checkedPayload

interface TicketsRemoteDataSource {
    suspend fun confirmedBets(filters: Map<String, String>): JsonElement?
    fun confirmedBetsBackend(): br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend =
        br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend.SILCE
    suspend fun recoverConfirmedBet(betId: Long): JsonElement? = null
    suspend fun checkConfirmedBet(betId: Long): JsonElement? = null
    suspend fun confirmedBetPrizeDetails(betId: Long): JsonElement? = null
    suspend fun confirmedBetReceipt(betId: Long, paid: Boolean, historical: Boolean): JsonElement? = null
    suspend fun purchases(filters: Map<String, String>): JsonElement?
    suspend fun purchaseBets(purchaseId: Long): JsonElement?
    suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) = Unit
    suspend fun checkTicket(code: String, qrCode: Boolean): JsonElement?
    suspend fun confirmedBetsConfiguration(): JsonElement?
    suspend fun purchaseMonths(): JsonElement?
    suspend fun purchasePaymentMethods(): JsonElement?
    suspend fun purchaseStatuses(paymentMethodId: Long?): JsonElement?
}

class TicketsRemoteDataSourceImpl(
    private val apiProvider: BackendApiProvider,
    private val betsHistoryApiProvider: BetsHistoryApiProvider? = null,
    private val configurationSource: AppConfigurationRemoteDataSource? = null,
    private val currentUserCpfProvider: CurrentUserCpfProvider = CurrentUserCpfProvider { "" },
    private val production: Boolean = false,
) : TicketsRemoteDataSource {
    @Volatile private var cachedBetHistoryParameters: AppParametersDto? = null
    @Volatile private var activeBetHistoryRoute = BetHistoryRoute(BetHistoryBackend.SILCE)

    override suspend fun confirmedBets(filters: Map<String, String>): JsonElement? {
        val parameters = cachedBetHistoryParameters ?: configurationSource?.appParameters()?.values
            ?.also { cachedBetHistoryParameters = it }
        val route = BetHistoryRoutingPolicy.resolve(
            parameters = parameters,
            cpf = currentUserCpfProvider.currentCpf(),
            production = production,
        )
        val dynamicApi = route.baseUrl?.let { betsHistoryApiProvider?.current(it) }
        return when {
            dynamicApi == null -> {
                activeBetHistoryRoute = BetHistoryRoute(BetHistoryBackend.SILCE)
                apiProvider.current().featureTicketsApi().confirmedBets(filters).payload
            }
            route.backend == BetHistoryBackend.NOVA_API ->
                dynamicApi.featureTicketsApi().novaApiConfirmedBets(filters.forDynamicHistory()).payload.also {
                    activeBetHistoryRoute = route
                }
            else -> dynamicApi.featureTicketsApi().confirmedBets(filters.forDynamicHistory()).payload.also {
                activeBetHistoryRoute = route
            }
        }
    }

    override fun confirmedBetsBackend() =
        br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend.valueOf(activeBetHistoryRoute.backend.name)

    override suspend fun recoverConfirmedBet(betId: Long): JsonElement? {
        val route = activeBetHistoryRoute.takeIf { it.backend == BetHistoryBackend.NOVA_API } ?: return null
        return route.baseUrl?.let { betsHistoryApiProvider?.current(it) }?.featureTicketsApi()?.recoverBet(betId)?.checkedPayload()
    }

    override suspend fun checkConfirmedBet(betId: Long) = apiProvider.current().featureTicketsApi().checkBet(betId).checkedPayload()
    override suspend fun confirmedBetPrizeDetails(betId: Long) = apiProvider.current().featureTicketsApi().prizeDetails(betId).checkedPayload()

    override suspend fun confirmedBetReceipt(betId: Long, paid: Boolean, historical: Boolean) = when {
        historical -> apiProvider.current().featureTicketsApi().historicalPrizeReceipt(betId)
        paid -> apiProvider.current().featureTicketsApi().prizeReceipt(betId)
        else -> apiProvider.current().featureTicketsApi().betReceipt(betId)
    }.checkedPayload()

    override suspend fun purchases(filters: Map<String, String>) =
        apiProvider.current().featureTicketsApi().purchases(filters).payload

    override suspend fun purchaseBets(purchaseId: Long): JsonElement? {
        val response = apiProvider.current().featureTicketsApi().purchaseBets(purchaseId)
        val serverDateTime = response.serverDateTime ?: return response.payload
        return JsonObject().apply {
            add("payload", response.payload)
            addProperty("dataHoraServidor", serverDateTime)
        }
    }

    override suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) {
        val body = JsonObject().apply {
            addProperty("nome", name)
            addProperty("mantemSurpresinhas", keepSurprises)
        }
        apiProvider.current().featureTicketsApi().savePurchaseAsFavoriteCart(purchaseId, body)
    }

    override suspend fun checkTicket(code: String, qrCode: Boolean): JsonElement? {
        val body = JsonParser.parseString("{\"codigo\":${code.toJsonString()}}")
        return if (qrCode) apiProvider.current().featureTicketsApi().checkTicketQrCode(body).payload
        else apiProvider.current().featureTicketsApi().checkTicket(body).payload
    }

    override suspend fun confirmedBetsConfiguration() = apiProvider.current().featureTicketsApi().confirmedBetsConfiguration().payload
    override suspend fun purchaseMonths() = apiProvider.current().featureTicketsApi().purchaseMonths().payload
    override suspend fun purchasePaymentMethods() = apiProvider.current().featureTicketsApi().paymentMethods().payload
    override suspend fun purchaseStatuses(paymentMethodId: Long?) =
        apiProvider.current().featureTicketsApi().purchaseStatuses(paymentMethodId).payload
}

fun interface CurrentUserCpfProvider {
    fun currentCpf(): String
}

private fun Map<String, String>.forDynamicHistory(): Map<String, String> = buildMap {
    putAll(this@forDynamicHistory)
    remove("combo")?.let { put("comboApostas", it) }
}

private fun String.toJsonString(): String = JsonParser.parseString("{}")
    .asJsonObject.apply { addProperty("value", this@toJsonString) }.get("value").toString()
