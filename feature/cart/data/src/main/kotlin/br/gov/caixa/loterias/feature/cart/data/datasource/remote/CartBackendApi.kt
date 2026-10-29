package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface CartBackendApi {
    @POST("logado/carrinhos-favoritos/validar")
    suspend fun validateFavoriteCartName(@Body body: JsonElement): Unit

    @GET("logado/carrinhos/validar")
    suspend fun validateCart(): ApiEnvelope<JsonElement>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BackendApi.featureCartApi(): CartBackendApi = CartBackendApiProjection(this)

private class CartBackendApiProjection(private val transport: BackendApi) : CartBackendApi {
    override suspend fun validateFavoriteCartName(@Body body: JsonElement): Unit = transport.validateFavoriteCartName(body = body)

    override suspend fun validateCart(): ApiEnvelope<JsonElement> = transport.validateCart()
}
