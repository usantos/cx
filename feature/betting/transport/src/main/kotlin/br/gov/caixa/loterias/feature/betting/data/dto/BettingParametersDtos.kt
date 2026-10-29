package br.gov.caixa.loterias.feature.betting.data.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class LotteryModalityDto(
    @SerializedName("valor") val id: Int? = null,
    @SerializedName("descricao") val name: String? = null,
    @SerializedName("descricaoEspecial") val specialName: String? = null,
    @SerializedName("descricaoGestor") val managerName: String? = null,
)

data class BettingParametersDto(
    @SerializedName("valorLimiteDiario") val dailyLimit: BigDecimal? = null,
    @SerializedName("valorMinimoCarrinho") val minimumCartAmount: BigDecimal? = null,
    @SerializedName("quantidadeMaximaApostasCarrinho") val maximumCartBets: Int? = null,
    @SerializedName("parametros") val parameters: List<LotteryParameterDto>? = null,
    @SerializedName("modalidadesDisponiveisCotas") val availablePoolModalities: List<AvailablePoolModalityDto>? = null,
)

data class AvailablePoolModalityDto(
    @SerializedName("modalidade") val modalityCode: String? = null,
    @SerializedName("modalidadeDetalhada") val modality: LotteryModalityDto? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("concurso") val contestNumber: Int? = null,
    @SerializedName("dataEncerramentoRevenda") val resaleClosingDate: String? = null,
    @SerializedName("horaEncerramentoRevenda") val resaleClosingTime: String? = null,
    @SerializedName("estimativa") val estimatedPrize: BigDecimal? = null,
    @SerializedName("dataHoraSorteio") val drawDateTime: String? = null,
)

data class LotteryParameterDto(
    @SerializedName("parametroJogo") val game: LotteryGameParameterDto? = null,
    @SerializedName("proximoConcurso") val nextGame: LotteryGameParameterDto? = null,
)

data class LotteryGameParameterDto(
    @SerializedName("concurso") val contest: LotteryContestDto? = null,
    @SerializedName("prognosticoMaximo") val maximumNumber: Int? = null,
    @SerializedName("quantidadeMinima") val minimumSelection: Int? = null,
    @SerializedName("quantidadeMaxima") val maximumSelection: Int? = null,
    @SerializedName("quantidadeSurpresinhas") val maximumSurprises: Int? = null,
    @SerializedName("teimosinhas") val recurringOptions: List<Int>? = null,
    @SerializedName("valoresAposta") val prices: List<LotteryPriceDto>? = null,
    @SerializedName("concursoDisponivelSimulacao") val available: Boolean? = null,
    @SerializedName("concursoCanalAberto") val open: Boolean? = null,
    @SerializedName("trevos") val clovers: LotteryCloverParametersDto? = null,
    @SerializedName("equipes") val teams: List<SportsTeamParameterDto>? = null,
    @SerializedName("partidas") val matches: List<SportsMatchParameterDto>? = null,
    @SerializedName("meses") val luckyMonths: List<LuckyMonthParameterDto>? = null,
    @SerializedName("aceitaEspelho") val acceptsMirror: Boolean? = null,
    @SerializedName("valorApostaMinima") val minimumBetAmount: BigDecimal? = null,
)

data class LuckyMonthParameterDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("abreviacao") val abbreviation: String? = null,
)

data class SportsTeamParameterDto(
    @SerializedName("indicadorSelecao") val selectionIndicator: Boolean? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("numeroPais") val countryNumber: Int? = null,
    @SerializedName("descricaoCurta") val shortDescription: String? = null,
    @SerializedName("descricaoLonga") val longDescription: String? = null,
    @SerializedName("pais") val country: String? = null,
    @SerializedName("siglaPais") val countryCode: String? = null,
    @SerializedName("uf") val state: String? = null,
    @SerializedName("nomeClass") val className: String? = null,
)

data class SportsMatchParameterDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("equipe1") val home: SportsTeamParameterDto? = null,
    @SerializedName("equipe2") val away: SportsTeamParameterDto? = null,
    @SerializedName("descricaoLegenda") val legendDescription: String? = null,
    @SerializedName("letraLegenda") val legendLetter: String? = null,
)

data class SportsTeamShieldDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("modalidades") val modalities: List<String>? = null,
    @SerializedName("equipe") val team: SportsShieldTeamDto? = null,
    @SerializedName("imagem") val imageBase64: String? = null,
)

data class SportsShieldTeamDto(
    @SerializedName("id") val id: Long? = null,
)

data class LotteryContestDto(
    @SerializedName("modalidade") val modalityCode: String? = null,
    @SerializedName("modalidadeDetalhada") val modality: LotteryModalityDto? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("dataHoraSorteio") val drawDateTime: String? = null,
    @SerializedName("dataFechamento") val closingDateTime: String? = null,
    @SerializedName("estimativa") val estimatedPrize: BigDecimal? = null,
    @SerializedName("aberto") val open: Boolean? = null,
)

data class LotteryPriceDto(
    @SerializedName("valor") val amount: BigDecimal? = null,
    @SerializedName("numeroPrognosticos") val numberQuantity: Int? = null,
    @SerializedName("numeroTrevos") val cloverQuantity: Int? = null,
    @SerializedName("quantidadeDuplos") val doubleQuantity: Int? = null,
    @SerializedName("quantidadeTriplos") val tripleQuantity: Int? = null,
)

data class LotteryCloverParametersDto(
    @SerializedName("prognosticoMaximoTrevo") val maximumNumber: Int? = null,
    @SerializedName("quantidadeMinimaTrevo") val minimumSelection: Int? = null,
    @SerializedName("quantidadeMaximaTrevo") val maximumSelection: Int? = null,
)
