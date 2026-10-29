package br.gov.caixa.loterias.feature.cart.data.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement
import java.math.BigDecimal

data class CartDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("valorTotal") val total: BigDecimal? = null,
    @SerializedName("apostasIndividuais") val individualBets: List<CartBetDto>? = null,
    @SerializedName("cotas") val poolShares: List<CartBetDto>? = null,
    @SerializedName("combos") val combos: List<CartComboDto>? = null,
    @SerializedName("msgCotaExcluida") val expiredPoolShareRemoved: Boolean? = null,
    /** Projeção simples do resumo; campos agrupados representam as mesmas apostas, sem valores adicionais. */
    @SerializedName("apostas") val bets: List<CartBetDto>? = null,
)

data class CartBetDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("modalidade") val modality: JsonElement? = null,
    @SerializedName("tipoConcurso") val contestType: JsonElement? = null,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement? = null,
    @SerializedName("numerosSelecionadosMaisMilionariaTrevo") val selectedClovers: JsonElement? = null,
    @SerializedName("valor") val amount: BigDecimal? = null,
    @SerializedName("quantidadeApostas") val betQuantity: Int? = null,
    @SerializedName("quantidadeNumeros") val numberQuantity: Int? = null,
    @SerializedName("quantidadeNumerosMaisMilionariaTrevo") val cloverQuantity: Int? = null,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int? = null,
    @SerializedName("concursoAlvo") val targetContest: Int? = null,
    @SerializedName("concursoInicial") val initialContest: Int? = null,
    @SerializedName("indicadorSurpresinha") val surpriseIndicator: JsonElement? = null,
    @SerializedName("surpresinha") val surprise: Boolean? = null,
    @SerializedName("espelho") val mirror: Boolean? = null,
    @SerializedName("geraEspelho") val generateMirror: Boolean? = null,
    @SerializedName("troca") val exchange: Boolean? = null,
    @SerializedName("combo") val combo: Boolean? = null,
    @SerializedName("mesDeSorte") val luckyMonth: JsonElement? = null,
    @SerializedName("timeDoCoracao") val favoriteTeam: JsonElement? = null,
    @SerializedName("partidasLoteca") val lotecaMatches: JsonElement? = null,
    @SerializedName("partidasLotogol") val lotogolMatches: JsonElement? = null,
    @SerializedName("reservaCotaBolao") val poolReservation: CartPoolReservationDto? = null,
)

data class CartPoolReservationDto(
    @SerializedName("codigoBolaoReserva") val poolCode: String? = null,
    @SerializedName("numeroCotaReservada") val shareNumber: Int? = null,
    @SerializedName("vrTotalCota") val total: BigDecimal? = null,
    @SerializedName("qtdCotaTotalBolao") val totalShares: Int? = null,
    @SerializedName("dataHoraExpiracaoReserva") val expiresAt: String? = null,
)

data class CartComboDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("tipoCombo") val type: CartComboTypeDto? = null,
    @SerializedName("apostas") val bets: List<CartBetDto>? = null,
)

data class CartComboTypeDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("descricao") val description: String? = null,
)

data class SaveFavoriteCartRequestDto(
    @SerializedName("nome") val name: String,
    @SerializedName("manterSurpresinhas") val keepSurprises: Boolean = true,
)

data class FavoriteCartDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val name: String? = null,
)

data class FavoriteCartBetDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("modalidade") val modality: FavoriteCartModalityDto? = null,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement? = null,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int? = null,
    @SerializedName("espelho") val mirror: Boolean? = null,
    @SerializedName("indicadorSurpresinha") val surpriseIndicator: String? = null,
    @SerializedName("trevos") val clovers: FavoriteCartCloversDto? = null,
    @SerializedName("quantidadeNumeros") val numberCount: Int? = null,
    @SerializedName("quantidadeTrevos") val cloverCount: Int? = null,
    @SerializedName("timeDoCoracao") val favoriteTeam: FavoriteCartTeamDto? = null,
    @SerializedName("mesDeSorte") val luckyMonth: FavoriteCartMonthDto? = null,
)

data class FavoriteCartTeamDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: String? = null,
)

data class FavoriteCartMonthDto(@SerializedName("nome") val name: String? = null)

data class FavoriteCartModalityDto(
    @SerializedName("valor") val value: Int? = null,
    @SerializedName("descricao") val description: String? = null,
    @SerializedName("descricaoEspecial") val specialDescription: String? = null,
)

data class FavoriteCartCloversDto(
    @SerializedName("trevosSelecionados") val selected: JsonElement? = null,
)

data class AddFavoriteCartRequestDto(
    @SerializedName("incluirEspecial") val specialSelection: Int,
)
