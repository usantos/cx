package br.gov.caixa.loterias.feature.results.data.dto

import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.LuckyMonthParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamParameterDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplaceCityDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplaceStateDto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

data class DrawResultDto(
    @SerializedName("municipioSorteio") val city: MarketplaceCityDto? = null,
    @SerializedName("ufSorteio") val state: MarketplaceStateDto? = null,
    @SerializedName("aviso") val notice: String? = null,
    @SerializedName("arrecadacaoTotal") val totalRevenue: String? = null,
    @SerializedName("acumuladoParaProximoConcurso") val accumulatedNextContest: String? = null,
    @SerializedName("acumulou") val accumulated: Boolean? = null,
    @SerializedName("numerosSorteadosPrimeiroSorteio") val firstDrawNumbers: JsonElement? = null,
    @SerializedName("numerosSorteadosSegundoSorteio") val secondDrawNumbers: List<Int>? = null,
    @SerializedName("trevosSorteadosPrimeiroSorteio") val clovers: List<Int>? = null,
    @SerializedName("concurso") val contest: DrawContestDto? = null,
    @SerializedName("premiacoesPrimeiroSorteio") val payouts: List<DrawPayoutDto>? = null,
    @SerializedName("premiacoesSegundoSorteio") val secondDrawPayouts: List<DrawPayoutDto>? = null,
    @SerializedName("partidasLoteca") val lotecaMatches: List<DrawSportsMatchDto>? = null,
    @SerializedName("partidasLotogol") val lotogolMatches: List<DrawSportsMatchDto>? = null,
    @SerializedName("premiacaoTimeDoCoracao") val favoriteTeamReward: DrawSelectionRewardDto? = null,
    @SerializedName("premiacaoMesDeSorte") val luckyMonthReward: DrawSelectionRewardDto? = null,
    @SerializedName("ganhadoresPrimeiroSorteio") val firstDrawRegionalWinners: List<DrawRegionalWinnerDto>? = null,
    @SerializedName("ganhadoresSegundoSorteio") val secondDrawRegionalWinners: List<DrawRegionalWinnerDto>? = null,
    @SerializedName("proximoConcurso") val nextContest: DrawContestDto? = null,
    @SerializedName("proximoConcursoFinalZero") val nextFinalZeroContest: DrawContestDto? = null,
    @SerializedName("proximoConcursoFinalCinco") val nextFinalFiveContest: DrawContestDto? = null,
    @SerializedName("proximoConcursoEspecial") val nextSpecialContest: DrawContestDto? = null,
)

data class DrawContestDto(
    @SerializedName("modalidade") val modality: String? = null,
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("estimativa") val estimatedPrize: String? = null,
    @SerializedName("modalidadeDetalhada") val modalityDetails: LotteryModalityDto? = null,
)

data class DrawPayoutDto(
    @SerializedName("descricao") val description: String? = null,
    @SerializedName("quantidadeGanhadores") val winners: Long? = null,
    @SerializedName("valor") val value: String? = null,
)

data class DrawSportsTeamDto(
    @SerializedName("parametroEquipe") val parameterTeam: SportsTeamParameterDto? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("descricaoCurta") val shortName: String? = null,
    @SerializedName("uf") val state: String? = null,
    @SerializedName("placar") val score: String? = null,
    @SerializedName("vitoria") val won: Boolean? = null,
    @SerializedName("siglaPais") val countryCode: String? = null,
    @SerializedName("indicadorSelecao") val nationalTeam: Boolean? = null,
)

data class DrawSportsMatchDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("equipe1") val home: DrawSportsTeamDto? = null,
    @SerializedName("equipe2") val away: DrawSportsTeamDto? = null,
    @SerializedName("empate") val draw: Boolean? = null,
    @SerializedName("indexPlacarEquipe1") val homeScoreIndex: Int? = null,
    @SerializedName("indexPlacarEquipe2") val awayScoreIndex: Int? = null,
    @SerializedName("letraLegenda") val legendLetter: String? = null,
    @SerializedName("descricaoLegenda") val legendDescription: String? = null,
)

data class DrawSelectionRewardDto(
    @SerializedName("equipe") val team: SportsTeamParameterDto? = null,
    @SerializedName("mesDeSorte") val month: LuckyMonthParameterDto? = null,
    @SerializedName("quantidadeGanhadores") val winners: Long? = null,
    @SerializedName("valor") val value: String? = null,
)

data class DrawRegionalWinnerDto(
    @SerializedName("municipioSorteio") val city: MarketplaceCityDto? = null,
    @SerializedName("ufSorteio") val state: MarketplaceStateDto? = null,
    @SerializedName("descricao") val description: String? = null,
)
