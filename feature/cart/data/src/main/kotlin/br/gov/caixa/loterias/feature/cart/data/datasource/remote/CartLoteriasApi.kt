package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.feature.cart.data.dto.AddFavoriteCartRequestDto
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartBetDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartDto
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.feature.cart.data.dto.SaveFavoriteCartRequestDto
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface CartLoteriasApi {
    @GET("logado/carrinhos")
    suspend fun currentCart(): ApiEnvelope<CartDto>

    @DELETE("logado/carrinhos/apostas/{id}")
    suspend fun removeCartBet(@Path("id") id: Long): ApiEnvelope<CartDto>

    @DELETE("logado/carrinhos/apostas/{id}")
    suspend fun removeBoundCartBet(
        @Path("id") id: Long,
        @Tag session: AccountVerificationSession,
    ): ApiEnvelope<CartDto>

    @DELETE("logado/carrinhos/combos/{id}")
    suspend fun removeCartCombo(@Path("id") id: Long): ApiEnvelope<CartDto>

    @POST("logado/carrinhos/limpar")
    suspend fun clearCart(): ApiEnvelope<CartDto>

    @GET("logado/carrinhos/validar")
    suspend fun validateCart(): ApiEnvelope<JsonElement>

    @POST("logado/carrinhos-favoritos/validar")
    suspend fun validateFavoriteCartName(
        @Body request: SaveFavoriteCartRequestDto,
    ): Unit

    @POST("logado/carrinhos/salvar-carrinho-favorito")
    suspend fun saveCurrentCartFavorite(
        @Body request: SaveFavoriteCartRequestDto,
    ): Unit

    @GET("logado/carrinhos-favoritos")
    suspend fun favoriteCarts(): ApiEnvelope<List<FavoriteCartDto>>

    @GET("logado/carrinhos-favoritos/{cartId}/apostas")
    suspend fun favoriteCartBets(
        @Path("cartId") cartId: Long,
    ): ApiEnvelope<List<FavoriteCartBetDto>>

    @DELETE("logado/carrinhos-favoritos/{cartId}")
    suspend fun deleteFavoriteCart(
        @Path("cartId") cartId: Long,
    ): ApiEnvelope<List<FavoriteCartDto>>

    @DELETE("logado/carrinhos-favoritos/{cartId}/apostas/{betId}")
    suspend fun deleteFavoriteCartBet(
        @Path("cartId") cartId: Long,
        @Path("betId") betId: Long,
    ): ApiEnvelope<List<FavoriteCartBetDto>>

    @POST("logado/carrinhos/incluir-carrinho-favorito/{cartId}")
    suspend fun addFavoriteCartToCurrent(
        @Path("cartId") cartId: Long,
        @Body request: AddFavoriteCartRequestDto,
    ): ApiEnvelope<CartDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featureCartApi(): CartLoteriasApi = CartLoteriasApiProjection(this)

private class CartLoteriasApiProjection(private val transport: LoteriasApi) : CartLoteriasApi {
    override suspend fun currentCart(): ApiEnvelope<CartDto> = transport.currentCart()

    override suspend fun removeCartBet(@Path("id") id: Long): ApiEnvelope<CartDto> = transport.removeCartBet(id = id)

    override suspend fun removeBoundCartBet(
        @Path("id") id: Long,
        @Tag session: AccountVerificationSession,
    ): ApiEnvelope<CartDto> = transport.removeBoundCartBet(id = id, session = session)

    override suspend fun removeCartCombo(@Path("id") id: Long): ApiEnvelope<CartDto> = transport.removeCartCombo(id = id)

    override suspend fun clearCart(): ApiEnvelope<CartDto> = transport.clearCart()

    override suspend fun validateCart(): ApiEnvelope<JsonElement> = transport.validateCart()

    override suspend fun validateFavoriteCartName(
        @Body request: SaveFavoriteCartRequestDto,
    ): Unit = transport.validateFavoriteCartName(request = request)

    override suspend fun saveCurrentCartFavorite(
        @Body request: SaveFavoriteCartRequestDto,
    ): Unit = transport.saveCurrentCartFavorite(request = request)

    override suspend fun favoriteCarts(): ApiEnvelope<List<FavoriteCartDto>> = transport.favoriteCarts()

    override suspend fun favoriteCartBets(
        @Path("cartId") cartId: Long,
    ): ApiEnvelope<List<FavoriteCartBetDto>> = transport.favoriteCartBets(cartId = cartId)

    override suspend fun deleteFavoriteCart(
        @Path("cartId") cartId: Long,
    ): ApiEnvelope<List<FavoriteCartDto>> = transport.deleteFavoriteCart(cartId = cartId)

    override suspend fun deleteFavoriteCartBet(
        @Path("cartId") cartId: Long,
        @Path("betId") betId: Long,
    ): ApiEnvelope<List<FavoriteCartBetDto>> = transport.deleteFavoriteCartBet(cartId = cartId, betId = betId)

    override suspend fun addFavoriteCartToCurrent(
        @Path("cartId") cartId: Long,
        @Body request: AddFavoriteCartRequestDto,
    ): ApiEnvelope<CartDto> = transport.addFavoriteCartToCurrent(cartId = cartId, request = request)
}
