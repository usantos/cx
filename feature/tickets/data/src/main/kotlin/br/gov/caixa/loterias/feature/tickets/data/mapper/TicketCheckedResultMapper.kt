package br.gov.caixa.loterias.feature.tickets.data.mapper

import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketAward
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal

internal fun JsonElement?.mapCheckedTicketResult(): CheckedTicketResult {
    val root = ticketPayloadObject() ?: return CheckedTicketResult()
    val prize = root.ticketElement("premio")?.takeIf(JsonElement::isJsonObject)?.asJsonObject
    fun JsonObject.optionalDecimal(key: String): BigDecimal? =
        runCatching { ticketElement(key)?.asBigDecimal }.getOrNull()
    val rows = buildList {
        prize?.get("concursos").ticketPayloadArrayObjects().forEach { contest ->
            val number = contest.ticketChildInt("concurso", "numero")
            contest.get("faixas").ticketPayloadArrayObjects().forEach { award ->
                add(CheckedTicketAward(number, contest.ticketText("situacao"), award.ticketText("nome"),
                    award.ticketInt("quantidadeAcertos"), award.ticketInt("sorteio"),
                    award.optionalDecimal("valorPremioCota") ?: award.optionalDecimal("valorLiquido")))
            }
        }
        prize?.get("concursosNaoPremiado").ticketPayloadArrayObjects().forEach { contest ->
            add(CheckedTicketAward(contest.ticketChildInt("concurso", "numero"), contest.ticketText("situacao")))
        }
    }
    return CheckedTicketResult(
        bets = mapConfirmedBets(), statusId = root.ticketChildInt("situacao", "valor") ?: root.ticketInt("situacao"),
        modalityId = root.ticketChildInt("modalidade", "valor"), modalityName = root.ticketText("modalidade").ticketDisplayName(),
        netPrize = prize?.optionalDecimal("valorLiquido") ?: root.optionalDecimal("valorPremio"),
        awards = rows,
    )
}
