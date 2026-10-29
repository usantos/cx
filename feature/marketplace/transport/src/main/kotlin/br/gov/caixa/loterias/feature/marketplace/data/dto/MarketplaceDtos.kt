package br.gov.caixa.loterias.feature.marketplace.data.dto

import br.gov.caixa.loterias.feature.betting.data.dto.LotecaBetMatchDto
import br.gov.caixa.loterias.feature.betting.data.dto.LuckyMonthParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsBetTeamDto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement
import java.math.BigDecimal

data class MarketplacePageDto(
    @SerializedName("totalRegistros") val totalRecords: Int? = null,
    @SerializedName("paginaAtual") val currentPage: Int? = null,
    @SerializedName("ultimaPagina") val lastPage: Int? = null,
    @SerializedName("cotas") val pools: List<MarketplacePoolDto>? = null,
)

data class MarketplacePoolDto(
    @SerializedName("codigoBolao") val code: String? = null,
    @SerializedName("modalidade") val modality: String? = null,
    @SerializedName("concurso") val contest: Int? = null,
    @SerializedName("nomeFantasia") val lotteryName: String? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("valorCota") val sharePrice: String? = null,
    @SerializedName("qtdCotaDisponivel") val availableShares: Int? = null,
    @SerializedName("qtdApostas") val betQuantity: Int? = null,
    @SerializedName("qtdNumeros") val numberQuantity: Int? = null,
    @SerializedName("lotericaFavorita") val favoriteLottery: Boolean? = null,
    @SerializedName("loterica") val lotteryId: Long? = null,
    @SerializedName(value = "municipio", alternate = ["municipioCota"]) val city: MarketplaceCityDto? = null,
    @SerializedName("uf") val state: MarketplaceStateDto? = null,
    @SerializedName("qtdCotaTotal") val totalShares: Int? = null,
    @SerializedName("tipoConcurso") val contestType: Int? = null,
    @SerializedName("vrPremioEstimado") val estimatedPrize: String? = null,
    @SerializedName("vrCotaComTarifa") val regularSharePriceWithFee: BigDecimal? = null,
    @SerializedName("valorTarifaServico") val serviceFeePerShare: BigDecimal? = null,
    @SerializedName("vrUltimaCotaComTarifa") val lastSharePriceWithFee: BigDecimal? = null,
)

data class MarketplacePoolDetailDto(
    @SerializedName("qtdCotaTotal") val totalShares: Int? = null,
    @SerializedName("qtdCotaDisponivel") val availableShares: Int? = null,
    @SerializedName("loterica") val lotteryId: Long? = null,
    @SerializedName("nomeFantasia") val lotteryName: String? = null,
    @SerializedName("municipioCota") val city: MarketplaceCityDto? = null,
    @SerializedName("concurso") val contest: Int? = null,
    @SerializedName("tipoConcurso") val contestType: Int? = null,
    @SerializedName("modalidade") val modality: String? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("diaSorteio") val drawDay: String? = null,
    @SerializedName("horaSorteio") val drawTime: String? = null,
    @SerializedName("vrPremioEstimado") val estimatedPrize: String? = null,
    @SerializedName("qtdApostas") val betQuantity: Int? = null,
    @SerializedName("qtdNumeros") val numberQuantity: Int? = null,
    @SerializedName("vrCotaSemTarifa") val sharePriceWithoutFee: String? = null,
    @SerializedName("vrTarifaServicoCota") val serviceFeePerShare: BigDecimal? = null,
    @SerializedName("vrUltimaCotaComTarifa") val lastSharePriceWithFee: String? = null,
    @SerializedName("vrTarifaServicoUltimaCota") val lastShareFee: String? = null,
    @SerializedName("lotericaFormatada") val formattedLottery: MarketplaceLotteryDto? = null,
    @SerializedName("apostas") val bets: List<MarketplaceBetDto>? = null,
    @SerializedName("vrCotaComTarifa") val regularSharePriceWithFee: BigDecimal? = null,
    @SerializedName("vrUltimaCotaSemTarifa") val lastSharePriceWithoutFee: String? = null,
)

data class MarketplaceCityDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: MarketplaceStateDto? = null,
)

data class MarketplaceStateDto(
    @SerializedName("sigla") val abbreviation: String? = null,
)

data class MarketplaceLotteryDto(
    @SerializedName("numeroFormatado") val formattedNumber: String? = null,
)

data class MarketplaceBetDto(
    @SerializedName("indicadorSurpresinha") val surprise: Boolean? = null,
    // Algumas modalidades retornam uma lista simples, enquanto Super Sete e Loteca podem retornar matrizes.
    @SerializedName("dezenas") val numbers: JsonElement? = null,
    @SerializedName("trevos") val clovers: List<Int>? = null,
    @SerializedName("timeDoCoracao") val favoriteTeam: SportsBetTeamDto? = null,
    @SerializedName("mesDeSorte") val luckyMonth: LuckyMonthParameterDto? = null,
    @SerializedName("partidasLoteca") val lotecaMatches: List<LotecaBetMatchDto>? = null,
)
