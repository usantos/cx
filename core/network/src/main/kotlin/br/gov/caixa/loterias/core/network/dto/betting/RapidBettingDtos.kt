package br.gov.caixa.loterias.core.network.dto.betting

import br.gov.caixa.loterias.core.network.dto.cart.FavoriteCartBetDto
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RapidConfigurationDto(
    @SerializedName("valorMinimo") val minimumAmount: BigDecimal? = null,
    @SerializedName("valorMaximo") val maximumAmount: BigDecimal? = null,
    @SerializedName("valorMinimoPremioPrincipal") val minimumPrize: BigDecimal? = null,
    @SerializedName("prognosticosObrigatorios") val requiredNumbers: List<Int>? = null,
    @SerializedName("prognosticosProibidos") val excludedNumbers: List<Int>? = null,
    @SerializedName("modalidades") val modalities: List<LotteryModalityDto>? = null,
    @SerializedName("apostasFavoritas") val favoriteBets: List<FavoriteCartBetDto>? = null,
)
