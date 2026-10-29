package br.gov.caixa.loterias.feature.tickets.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import com.google.gson.JsonElement
import com.google.gson.JsonObject

internal fun JsonElement?.ticketToLuckyMonth(): LuckyMonth? {
    val value = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: return null
    val number = value.ticketInt("numero") ?: return null
    return LuckyMonth(number, value.ticketText("nome"), value.ticketText("abreviacao"))
}

internal fun JsonElement?.ticketToSportsTeam(): SportsTeam? {
    val outer = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: return null
    val value = outer.ticketElement("parametroEquipe")?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: outer
    val number = value.ticketInt("numero") ?: outer.ticketInt("numero") ?: 0
    val name = value.ticketText("nome").ifBlank { outer.ticketText("nome") }
    if (number <= 0 && name.isBlank()) return null
    return SportsTeam(
        number = number,
        name = name,
        state = value.ticketText("uf").ifBlank { outer.ticketText("uf") },
        shortDescription = value.ticketText("descricaoCurta").ifBlank { outer.ticketText("descricaoCurta") },
        longDescription = value.ticketText("descricaoLonga").ifBlank { outer.ticketText("descricaoLonga") },
        countryNumber = value.ticketInt("numeroPais") ?: outer.ticketInt("numeroPais") ?: 0,
        country = value.ticketText("pais").ifBlank { outer.ticketText("pais") },
        countryCode = value.ticketText("siglaPais").ifBlank { outer.ticketText("siglaPais") },
        className = value.ticketText("nomeClass").ifBlank { outer.ticketText("nomeClass") },
        selectionIndicator = value.ticketBool("indicadorSelecao") || outer.ticketBool("indicadorSelecao"),
    )
}

internal fun JsonElement?.ticketToLotecaSelections(): List<LotecaMatchSelection> = ticketJsonObjects().mapIndexedNotNull { index, value ->
    val homeElement = value.ticketElement("equipe1")
    val awayElement = value.ticketElement("equipe2")
    val home = homeElement.ticketToSportsTeam() ?: return@mapIndexedNotNull null
    val away = awayElement.ticketToSportsTeam() ?: return@mapIndexedNotNull null
    val match = SportsMatch(
        number = value.ticketInt("numero") ?: index + 1,
        home = home,
        away = away,
        legendDescription = value.ticketText("descricaoLegenda"),
        legendLetter = value.ticketText("letraLegenda"),
    )
    LotecaMatchSelection(
        match = match,
        homeWin = homeElement.ticketJsonBool("vitoria"),
        draw = value.ticketBool("empate"),
        awayWin = awayElement.ticketJsonBool("vitoria"),
    )
}

internal fun JsonElement?.ticketToLotogolSelections(): List<LotogolMatchSelection> = ticketJsonObjects().mapIndexedNotNull { index, value ->
    val homeElement = value.ticketElement("equipe1")
    val awayElement = value.ticketElement("equipe2")
    val home = homeElement.ticketToSportsTeam() ?: return@mapIndexedNotNull null
    val away = awayElement.ticketToSportsTeam() ?: return@mapIndexedNotNull null
    val homeScore = value.ticketInt("indexPlacarEquipe1") ?: homeElement.ticketJsonScore() ?: return@mapIndexedNotNull null
    val awayScore = value.ticketInt("indexPlacarEquipe2") ?: awayElement.ticketJsonScore() ?: return@mapIndexedNotNull null
    LotogolMatchSelection(
        match = SportsMatch(index + 1, home, away),
        homeScore = homeScore,
        awayScore = awayScore,
    )
}

internal fun JsonElement?.ticketJsonObjects(): List<JsonObject> =
    this?.takeIf(JsonElement::isJsonArray)?.asJsonArray?.mapNotNull {
        it.takeIf(JsonElement::isJsonObject)?.asJsonObject
    }.orEmpty()

internal fun JsonElement?.ticketJsonBool(name: String): Boolean =
    this?.takeIf(JsonElement::isJsonObject)?.asJsonObject?.ticketBool(name) == true

internal fun JsonElement?.ticketJsonScore(): Int? = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject
    ?.ticketText("placar")?.filter(Char::isDigit)?.toIntOrNull()
