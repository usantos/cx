package br.gov.caixa.loterias.feature.tickets.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface TicketsBetsHistoryApi {
    @GET("apostas")
    suspend fun confirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("consultar")
    suspend fun novaApiConfirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("{id}/recuperar")
    suspend fun recoverBet(@Path("id") betId: Long): ApiEnvelope<JsonElement>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BetsHistoryApi.featureTicketsApi(): TicketsBetsHistoryApi = TicketsBetsHistoryApiProjection(this)

private class TicketsBetsHistoryApiProjection(private val transport: BetsHistoryApi) : TicketsBetsHistoryApi {
    override suspend fun confirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement> = transport.confirmedBets(filters = filters)

    override suspend fun novaApiConfirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement> = transport.novaApiConfirmedBets(filters = filters)

    override suspend fun recoverBet(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.recoverBet(betId = betId)
}
