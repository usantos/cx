package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotecaSelection
import com.google.gson.JsonElement

internal fun JsonElement?.cartTeamName(): String {
    if (this == null || !isJsonObject) return cartTextValue()
    val name = asJsonObject.cartFirstText("nome", "descricaoCurta", "descricaoLonga")
    val state = asJsonObject.cartFirstText("uf")
    return listOf(name, state).filter(String::isNotBlank).joinToString("/")
}

internal fun JsonElement?.cartMatchRows(): List<String> {
    if (this == null || !isJsonArray) return emptyList()
    return asJsonArray.mapIndexedNotNull { index, element ->
        if (!element.isJsonObject) return@mapIndexedNotNull null
        val match = element.asJsonObject
        val home = match.get("equipe1").cartTeamName()
        val away = match.get("equipe2").cartTeamName()
        val result = listOf(
            match.cartFirstText("indexPlacarEquipe1"),
            match.cartFirstText("indexPlacarEquipe2")
        ).filter(String::isNotBlank).joinToString(" x ")
        val selection = match.cartFirstText("letraLegenda", "descricaoLegenda")
        messageText(
            "1_s_2_s_x_3_s_4_s_4784ee",
            match.cartFirstText("numero").ifBlank { (index + 1).toString() },
            home,
            away,
            listOf(result, selection).filter(String::isNotBlank).joinToString(
                " · ",
                prefix = if (result.isNotBlank() || selection.isNotBlank()) " · " else ""
            )
        ).trim()
    }
}

internal fun JsonElement?.cartLotecaSelections(): List<CartLotecaSelection> {
    if (this == null || !isJsonArray) return emptyList()
    return asJsonArray.mapIndexedNotNull { index, element ->
        if (!element.isJsonObject) return@mapIndexedNotNull null
        val match = element.asJsonObject
        val home = match.get("equipe1")
        val away = match.get("equipe2")
        fun JsonElement?.selected(): Boolean = this?.takeIf { it.isJsonPrimitive }
            ?.asJsonPrimitive?.let { it.isBoolean && it.asBoolean } == true
        CartLotecaSelection(
            number = index + 1,
            homeLabel = home.cartTeamName(),
            awayLabel = away.cartTeamName(),
            homeSelected = home?.takeIf { it.isJsonObject }?.asJsonObject?.get("vitoria").selected(),
            drawSelected = match.get("empate").selected(),
            awaySelected = away?.takeIf { it.isJsonObject }?.asJsonObject?.get("vitoria").selected(),
            description = match.cartFirstText("descricaoLegenda")
        )
    }
}
