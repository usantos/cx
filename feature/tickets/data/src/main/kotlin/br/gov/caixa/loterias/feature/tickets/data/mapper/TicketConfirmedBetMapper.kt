package br.gov.caixa.loterias.feature.tickets.data.mapper

import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import com.google.gson.JsonElement
import com.google.gson.JsonObject

internal fun JsonElement?.mapConfirmedBets(): List<ConfirmedBet> = ticketCollectObjects().filter { objectValue ->
    objectValue.ticketLong("id") != null && (
        objectValue.has("modalidade") || objectValue.has("numeroConcurso") ||
            objectValue.has("concursoAlvo") || objectValue.has("numerosSelecionados")
        )
}.distinctBy { it.ticketLong("id") }.map { value ->
    val numberColumns = value.ticketElement("numerosSelecionados").ticketNumberRows().ifEmpty {
        value.ticketElement("dezenas").ticketNumberRows()
    }
    val modalityCode = value.ticketRawText("modalidade")
    ConfirmedBet(
        id = value.ticketLong("id") ?: 0,
        modality = value.ticketText("modalidade").ticketDisplayName(),
        contest = value.ticketInt("numeroConcurso") ?: value.ticketInt("concursoInicial") ?: value.ticketInt("concursoAlvo")
            ?: value.ticketChildInt("concursoAlvo", "numero") ?: 0,
        date = value.ticketText("dataEfetivacao").ifBlank { value.ticketText("dataCompra") },
        status = value.ticketText("situacao").ticketDisplayName(),
        numbers = numberColumns.flatten(),
        prize = value.ticketDecimal("valorPremio"),
        originalRecurringBet = !value.ticketBool("troca") && ((value.ticketInt("quantidadeTeimosinhas") ?: 0) > 0 ||
            value.ticketBool("apostaOriginal") || value.ticketBool("teimosinha")),
        redeemedPrize = value.ticketBool("houveResgate") || value.ticketText("situacao").contains("RESGAT", true),
        exchangeBetId = value.ticketLong("idApostaTroca"),
        modalityId = value.ticketChildInt("modalidade", "valor") ?: value.ticketInt("idModalidade")
            ?: modalityCode.ticketModalityId(),
        statusId = value.ticketChildLong("situacao", "valor") ?: value.ticketLong("idSituacao") ?: 0,
        contestTypeId = value.ticketContestTypeId(),
        poolShare = value.ticketBool("indicadorCotaBolao") || value.ticketBool("cotaBolao"),
        surprise = value.ticketSurpriseFlag(),
        time = value.ticketText("horaEfetivacao"),
        numberColumns = numberColumns,
        clovers = value.ticketNumbers("numerosSelecionadosMaisMilionariaTrevo").ifEmpty {
            value.ticketElement("trevos")?.takeIf(JsonElement::isJsonObject)?.asJsonObject
                ?.ticketNumbers("trevosSelecionados").orEmpty()
        },
        favoriteTeam = value.ticketElement("timeDoCoracao").ticketToSportsTeam(),
        luckyMonth = value.ticketElement("mesDeSorte").ticketToLuckyMonth(),
        lotecaSelections = value.ticketElement("partidasLoteca").ticketToLotecaSelections(),
        lotogolSelections = value.ticketElement("partidasLotogol").ticketToLotogolSelections(),
        recurringDraws = value.ticketInt("quantidadeTeimosinhas") ?: 0,
        quantity = value.ticketInt("quantidadeApostas") ?: 1,
        mirror = value.ticketBool("espelho") || value.ticketBool("geraEspelho"),
        exchange = value.ticketBool("troca"),
        combo = value.ticketBool("combo"),
    )
}

internal fun JsonObject.ticketContestTypeId(): Long {
    ticketChildLong("tipoConcurso", "valor")?.let { return it }
    ticketLong("idTipoConcurso")?.let { return it }
    val raw = ticketElement("tipoConcurso")?.let { contestType ->
        when {
            contestType.isJsonPrimitive -> runCatching { contestType.asString }.getOrDefault("")
            contestType.isJsonObject -> contestType.asJsonObject.ticketRawText("valor")
                .ifBlank { contestType.asJsonObject.ticketRawText("descricao") }
            else -> ""
        }
    }.orEmpty()
    return when {
        raw.equals("E", true) || raw.contains("ESPECIAL", true) -> 2
        raw.equals("N", true) || raw.contains("NORMAL", true) -> 1
        else -> 0
    }
}

internal fun JsonObject.ticketSurpriseFlag(): Boolean {
    if (ticketBool("surpresinha")) return true
    val indicator = ticketText("indicadorSurpresinha")
    return indicator.isNotBlank() && indicator != "0" &&
        !indicator.contains("NAO", ignoreCase = true) && !indicator.contains("NÃO", ignoreCase = true)
}

internal fun String.ticketModalityId(): Int = when (uppercase().replace('-', '_').replace(' ', '_')) {
    "MEGA_SENA" -> 2
    "QUINA" -> 3
    "SUPER_7", "SUPER_SETE" -> 7
    "LOTOFACIL" -> 8
    "MAIS_MILIONARIA", "+MILIONARIA" -> 9
    "DIA_DE_SORTE" -> 11
    "LOTOGOL" -> 14
    "LOTOMANIA" -> 16
    "DUPLA_SENA" -> 18
    "LOTECA" -> 19
    "TIMEMANIA" -> 20
    else -> 0
}
