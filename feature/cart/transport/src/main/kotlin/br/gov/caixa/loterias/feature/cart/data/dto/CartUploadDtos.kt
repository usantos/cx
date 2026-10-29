package br.gov.caixa.loterias.feature.cart.data.dto

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/** Modelos exclusivos de transporte: nunca adiciona aqui identificadores SQLite, seleções de interface ou recibos locais de migração. */
data class CartUploadDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("modalidade") val modality: String,
    @SerializedName("tipoConcurso") val contestType: CartCodeDto<String?>,
    @SerializedName("numerosSelecionados") val numbers: JsonArray?,
    @SerializedName("numerosSelecionadosMaisMilionariaTrevo") val clovers: List<Int>?,
    @SerializedName("indicadorSurpresinha") val quickPickIndicator: CartCodeDto<Int>,
    @SerializedName("valor") val amount: BigDecimal,
    @SerializedName("concursoAlvo") val targetContest: Int,
    @SerializedName("quantidadeTeimosinhas") val repeatCount: Int,
    @SerializedName("quantidadeApostas") val betCount: Int,
    @SerializedName("espelho") val mirrored: Boolean,
    @SerializedName("geraEspelho") val generateMirror: Boolean,
    @SerializedName("concursoInicial") val initialContest: Int,
    @SerializedName("situacao") val status: CartCodeDto<Long>,
    @SerializedName("troca") val replacement: Boolean,
    @SerializedName("dataEfetivacao") val effectiveDate: String?,
    @SerializedName("horaEfetivacao") val effectiveTime: String?,
    @SerializedName("vinculoEspelho") val mirrorLink: Long,
    @SerializedName("quantidadeNumeros") val numberCount: Int,
    @SerializedName("quantidadeNumerosMaisMilionariaTrevo") val cloverCount: Int,
    @SerializedName("surpresinha") val quickPick: Boolean,
    @SerializedName("timeDoCoracao") val favoriteTeam: CartTeamDto,
    @SerializedName("mesDeSorte") val luckyMonth: CartMonthDto,
    @SerializedName("partidasLoteca") val loteca: List<CartLotecaDto>,
    @SerializedName("partidasLotogol") val lotogol: List<CartLotogolDto>,
    @SerializedName("indicadorCotaBolao") val poolShare: Boolean = false,
) {
    override fun toString() = "CartUploadDto(redacted)"
}

data class CartCodeDto<T>(
    @SerializedName("valor") val value: T,
    @SerializedName("descricao") val description: String?,
)

data class CartTeamDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("numeroPais") val countryNumber: Int? = null,
    @SerializedName("pais") val country: String? = null,
    @SerializedName("siglaPais") val countryCode: String? = null,
    @SerializedName("uf") val state: String? = null,
    @SerializedName("nomeClass") val className: String? = null,
    @SerializedName("indicadorSelecao") val isNationalTeam: Boolean? = null,
    @SerializedName("descricaoCurta") val shortDescription: String? = null,
    @SerializedName("descricaoLonga") val longDescription: String? = null,
    @SerializedName("parametroEquipe") val parameter: CartTeamDto? = null,
    @SerializedName("placar") val score: String? = null,
    @SerializedName("vitoria") val winner: Boolean? = null,
) {
    override fun toString() = "CartTeamDto(redacted)"
}

data class CartMonthDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("abreviacao") val abbreviation: String? = null,
)

data class CartLotecaDto(
    @SerializedName("equipe1") val first: CartTeamDto,
    @SerializedName("equipe2") val second: CartTeamDto,
    @SerializedName("empate") val draw: Boolean,
)

data class CartLotogolDto(
    @SerializedName("equipe1") val first: CartTeamDto,
    @SerializedName("equipe2") val second: CartTeamDto,
    @SerializedName("indexPlacarEquipe1") val firstScoreIndex: Int,
    @SerializedName("indexPlacarEquipe2") val secondScoreIndex: Int,
)
