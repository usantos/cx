package br.gov.caixa.loterias.feature.betting.data.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

data class FavoriteBetEnumDto(
    @SerializedName("valor") val value: Int? = null,
    @SerializedName("descricao") val description: String? = null,
)

data class FavoriteBetNamedParameterDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: String? = null,
)

data class FavoriteBetCloversDto(
    @SerializedName("trevosSelecionados") val selected: JsonElement? = null,
)

data class FavoriteBetDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("modalidade") val modality: FavoriteBetEnumDto? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement? = null,
    @SerializedName("timeDoCoracao") val heartTeam: FavoriteBetNamedParameterDto? = null,
    @SerializedName("mesDeSorte") val luckyMonth: FavoriteBetNamedParameterDto? = null,
    @SerializedName("trevos") val clovers: FavoriteBetCloversDto? = null,
)

data class FavoriteBetPageDto(
    @SerializedName("rowCount") val rowCount: Long? = null,
    @SerializedName("lista") val items: List<FavoriteBetDto>? = null,
    @SerializedName("listaSize") val itemCount: Int? = null,
)

data class GroupedFavoriteBetsDto(
    @SerializedName("resultado") val groups: Map<String, FavoriteBetPageDto>? = null,
    @SerializedName("pageSize") val pageSize: Int? = null,
)
