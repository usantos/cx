package br.gov.caixa.loterias.feature.marketplace.data.datasource.remote

import br.gov.caixa.loterias.feature.betting.data.dto.AddPoolShareRequestDto
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePoolDetailDto
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface MarketplaceLoteriasApi {
    @POST("logado/carrinhos/incluir-aposta")
    suspend fun addPoolShareToCart(@Body request: AddPoolShareRequestDto): ApiEnvelope<CartDto>

    @GET("boloes/recuperar-boloes-disponiveis")
    suspend fun marketplacePools(@QueryMap filters: Map<String, String>): ApiEnvelope<MarketplacePageDto>

    @GET("boloes/detalhar-bolao")
    suspend fun marketplacePoolDetail(@Query("idBolao") poolCode: String): ApiEnvelope<MarketplacePoolDetailDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featureMarketplaceApi(): MarketplaceLoteriasApi = MarketplaceLoteriasApiProjection(this)

private class MarketplaceLoteriasApiProjection(private val transport: LoteriasApi) : MarketplaceLoteriasApi {
    override suspend fun addPoolShareToCart(@Body request: AddPoolShareRequestDto): ApiEnvelope<CartDto> = transport.addPoolShareToCart(request = request)

    override suspend fun marketplacePools(@QueryMap filters: Map<String, String>): ApiEnvelope<MarketplacePageDto> = transport.marketplacePools(filters = filters)

    override suspend fun marketplacePoolDetail(@Query("idBolao") poolCode: String): ApiEnvelope<MarketplacePoolDetailDto> = transport.marketplacePoolDetail(poolCode = poolCode)
}
