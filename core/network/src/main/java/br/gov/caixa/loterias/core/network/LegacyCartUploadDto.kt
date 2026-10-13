package br.gov.caixa.loterias.core.network

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/** Wire-only models: never add SQLite IDs, UI selections, or local migration receipts here. */
data class LegacyCartUploadDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("modalidade") val modality: String,
    @SerializedName("tipoConcurso") val contestType: LegacyCartCodeDto<String?>,
    @SerializedName("numerosSelecionados") val numbers: JsonArray?,
    @SerializedName("numerosSelecionadosMaisMilionariaTrevo") val clovers: List<Int>?,
    @SerializedName("indicadorSurpresinha") val quickPickIndicator: LegacyCartCodeDto<Int>,
    @SerializedName("valor") val amount: BigDecimal,
    @SerializedName("concursoAlvo") val targetContest: Int,
    @SerializedName("quantidadeTeimosinhas") val repeatCount: Int,
    @SerializedName("quantidadeApostas") val betCount: Int,
    @SerializedName("espelho") val mirrored: Boolean,
    @SerializedName("geraEspelho") val generateMirror: Boolean,
    @SerializedName("concursoInicial") val initialContest: Int,
    @SerializedName("situacao") val status: LegacyCartCodeDto<Long>,
    @SerializedName("troca") val replacement: Boolean,
    @SerializedName("dataEfetivacao") val effectiveDate: String?,
    @SerializedName("horaEfetivacao") val effectiveTime: String?,
    @SerializedName("vinculoEspelho") val mirrorLink: Long,
    @SerializedName("quantidadeNumeros") val numberCount: Int,
    @SerializedName("quantidadeNumerosMaisMilionariaTrevo") val cloverCount: Int,
    @SerializedName("surpresinha") val quickPick: Boolean,
    @SerializedName("timeDoCoracao") val favoriteTeam: LegacyCartTeamDto,
    @SerializedName("mesDeSorte") val luckyMonth: LegacyCartMonthDto,
    @SerializedName("partidasLoteca") val loteca: List<LegacyCartLotecaDto>,
    @SerializedName("partidasLotogol") val lotogol: List<LegacyCartLotogolDto>,
    @SerializedName("indicadorCotaBolao") val poolShare: Boolean = false,
) {
    override fun toString() = "LegacyCartUploadDto(redacted)"
}

data class LegacyCartCodeDto<T>(
    @SerializedName("valor") val value: T,
    @SerializedName("descricao") val description: String?,
)

data class LegacyCartTeamDto(
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
    @SerializedName("parametroEquipe") val parameter: LegacyCartTeamDto? = null,
    @SerializedName("placar") val score: String? = null,
    @SerializedName("vitoria") val winner: Boolean? = null,
) {
    override fun toString() = "LegacyCartTeamDto(redacted)"
}

data class LegacyCartMonthDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("abreviacao") val abbreviation: String? = null,
)

data class LegacyCartLotecaDto(
    @SerializedName("equipe1") val first: LegacyCartTeamDto,
    @SerializedName("equipe2") val second: LegacyCartTeamDto,
    @SerializedName("empate") val draw: Boolean,
)

data class LegacyCartLotogolDto(
    @SerializedName("equipe1") val first: LegacyCartTeamDto,
    @SerializedName("equipe2") val second: LegacyCartTeamDto,
    @SerializedName("indexPlacarEquipe1") val firstScoreIndex: Int,
    @SerializedName("indexPlacarEquipe2") val secondScoreIndex: Int,
)
