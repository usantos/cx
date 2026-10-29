package br.gov.caixa.loterias.feature.betting.data.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement
import java.math.BigDecimal

data class BetComboDto(
    @SerializedName("tipoCombo") val type: BetComboTypeDto? = null,
    @SerializedName("modalidadesCombo") val entries: List<BetComboEntryDto>? = null,
    @SerializedName("valor") val amount: BigDecimal? = null,
)

data class BetComboTypeDto(
    @SerializedName("codigo") val id: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("descricao") val description: String? = null,
    @SerializedName("novo") val isNew: Boolean? = null,
)

data class BetComboEntryDto(
    @SerializedName("modalidade") val modality: LotteryModalityDto? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("quantidadeApostas") val betQuantity: Int? = null,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int? = null,
    @SerializedName("quantidadeDezenas") val numberQuantity: Int? = null,
    @SerializedName("quantidadeDezenasTrevos") val cloverQuantity: Int? = null,
    @SerializedName("numero") val contestNumber: Int? = null,
)

data class DomainCharacterDto(
    @SerializedName("valor") val value: String,
    @SerializedName("descricao") val description: String,
)

data class DomainIntegerDto(
    @SerializedName("valor") val value: Int,
    @SerializedName("descricao") val description: String,
)

data class AddBetRequestDto(
    @SerializedName("modalidade") val modality: String,
    @SerializedName("tipoConcurso") val contestType: DomainCharacterDto,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement,
    @SerializedName("numerosSelecionadosMaisMilionariaTrevo") val selectedClovers: List<Int>,
    @SerializedName("indicadorSurpresinha") val surpriseIndicator: DomainIntegerDto,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int,
    @SerializedName("quantidadeApostas") val quantity: Int,
    @SerializedName("quantidadeNumeros") val numberQuantity: Int,
    @SerializedName("quantidadeNumerosMaisMilionariaTrevo") val cloverQuantity: Int,
    @SerializedName("surpresinha") val surprise: Boolean,
    @SerializedName("timeDoCoracao") val favoriteTeam: SportsBetTeamDto? = null,
    @SerializedName("partidasLoteca") val lotecaMatches: List<LotecaBetMatchDto>? = null,
    @SerializedName("partidasLotogol") val lotogolMatches: List<LotogolBetMatchDto>? = null,
    @SerializedName("mesDeSorte") val luckyMonth: LuckyMonthParameterDto? = null,
    @SerializedName("geraEspelho") val generateMirror: Boolean? = null,
    @SerializedName("concursoAlvo") val targetContest: Int? = null,
    @SerializedName("valor") val amount: BigDecimal? = null,
)

data class SportsBetTeamDto(
    @SerializedName("indicadorSelecao") val selectionIndicator: Boolean,
    @SerializedName("nome") val name: String,
    @SerializedName("numero") val number: Int,
    @SerializedName("numeroPais") val countryNumber: Int,
    @SerializedName("descricaoCurta") val shortDescription: String,
    @SerializedName("descricaoLonga") val longDescription: String,
    @SerializedName("pais") val country: String,
    @SerializedName("siglaPais") val countryCode: String,
    @SerializedName("uf") val state: String,
    @SerializedName("nomeClass") val className: String,
    @SerializedName("vitoria") val win: Boolean? = null,
    @SerializedName("placar") val score: String? = null,
    @SerializedName("parametroEquipe") val parameterTeam: SportsTeamParameterDto? = null,
)

data class LotecaBetMatchDto(
    @SerializedName("equipe1") val home: SportsBetTeamDto,
    @SerializedName("equipe2") val away: SportsBetTeamDto,
    @SerializedName("empate") val draw: Boolean,
    @SerializedName("numero") val number: Int,
    @SerializedName("descricaoLegenda") val legendDescription: String? = null,
    @SerializedName("letraLegenda") val legendLetter: String? = null,
)

data class LotogolBetMatchDto(
    @SerializedName("equipe1") val home: SportsBetTeamDto,
    @SerializedName("equipe2") val away: SportsBetTeamDto,
)

/**
 * Corpo usado pelo servidor legado para reservar cotas do Marketplace no carrinho. Espelha intencionalmente
 * IdentificaoDeUmBolao sem sobrecarregar o modelo de aposta regular com campos exclusivos de bolões.
 */

data class AddPoolShareRequestDto(
    @SerializedName("modalidade") val modality: String,
    @SerializedName("tipoConcurso") val contestType: DomainCharacterDto,
    @SerializedName("valor") val amount: BigDecimal,
    @SerializedName("concursoAlvo") val targetContest: Int,
    @SerializedName("dataSorteio") val drawDate: String,
    @SerializedName("indicadorCotaBolao") val poolShare: Boolean = true,
    @SerializedName("subcanalCarrinhoAposta") val cartSubchannel: Int = 1,
    @SerializedName("idBolao") val poolCode: String,
    @SerializedName("qtdCotasBolao") val shareQuantity: Int,
    @SerializedName("valorTarifaServico") val serviceFee: BigDecimal,
)

data class AddComboRequestDto(
    @SerializedName("id") val id: Int,
    @SerializedName("qtdApostas") val quantity: Int,
    @SerializedName("isSurpresinha") val surprise: Boolean,
    @SerializedName("limparCarrinho") val clearCart: Boolean = false,
)

/** IncluirSurpresinhaDTO possui um contrato de transporte separado de uma aposta manual preenchida. */
data class SurpriseBetRequestDto(
    @SerializedName("modalidade") val modality: String,
    @SerializedName("tipoConcurso") val contestType: DomainCharacterDto,
    @SerializedName("indicadorSurpresinha") val surpriseIndicator: DomainIntegerDto,
    @SerializedName("surpresinha") val surprise: Boolean = true,
    @SerializedName("concursoAlvo") val targetContest: Int?,
    @SerializedName("quantidadeNumeros") val numberQuantity: Int,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int,
    @SerializedName("quantidadeSurpresinhas") val surpriseQuantity: Int,
    @SerializedName("quantidadeNumerosMaisMilionariaTrevo") val cloverQuantity: Int,
    @SerializedName("gerarEspelho") val generateMirror: Boolean?,
    @SerializedName("quantidadeApostas") val quantity: Int = 1,
    @SerializedName("timeDoCoracao") val favoriteTeam: SportsBetTeamDto?,
)
