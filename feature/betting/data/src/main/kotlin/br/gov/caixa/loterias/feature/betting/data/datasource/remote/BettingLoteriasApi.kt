package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.feature.betting.data.dto.AddBetRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddComboRequestDto
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboDto
import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface BettingLoteriasApi {
    @GET("publico/modalidades")
    suspend fun lotteryModalities(): ApiEnvelope<List<LotteryModalityDto>>

    @GET("publico/parametros-simulacao")
    suspend fun bettingParameters(): ApiEnvelope<BettingParametersDto>

    @GET("logado/combos")
    suspend fun betCombos(): ApiEnvelope<List<BetComboDto>>

    @POST("logado/carrinhos/incluir-aposta")
    suspend fun addBetToCart(@Body request: AddBetRequestDto): ApiEnvelope<CartDto>

    @POST("logado/carrinhos/incluir-combo")
    suspend fun addComboToCart(@Body request: AddComboRequestDto): ApiEnvelope<CartDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featureBettingApi(): BettingLoteriasApi = BettingLoteriasApiProjection(this)

private class BettingLoteriasApiProjection(private val transport: LoteriasApi) : BettingLoteriasApi {
    override suspend fun lotteryModalities(): ApiEnvelope<List<LotteryModalityDto>> = transport.lotteryModalities()

    override suspend fun bettingParameters(): ApiEnvelope<BettingParametersDto> = transport.bettingParameters()

    override suspend fun betCombos(): ApiEnvelope<List<BetComboDto>> = transport.betCombos()

    override suspend fun addBetToCart(@Body request: AddBetRequestDto): ApiEnvelope<CartDto> = transport.addBetToCart(request = request)

    override suspend fun addComboToCart(@Body request: AddComboRequestDto): ApiEnvelope<CartDto> = transport.addComboToCart(request = request)
}
