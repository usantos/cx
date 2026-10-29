package br.gov.caixa.loterias.feature.tickets.data.mapper

import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import com.google.gson.JsonElement
import com.google.gson.JsonObject

internal fun JsonElement?.toConfirmedBetsConfiguration(): ConfirmedBetsConfiguration = mapTicketFilters()
internal fun JsonElement?.toPurchaseMonths(): List<PurchaseMonth> = mapPurchaseMonths()
internal fun JsonElement?.toFilterOptions(vararg labelKeys: String, idKey: String = "id"): List<TicketFilterOption> =
    mapTicketFilterOptions(*labelKeys, idKey = idKey)
internal fun JsonElement?.toCheckedTicketResult(): CheckedTicketResult = mapCheckedTicketResult()
internal fun JsonElement?.toBets(): List<ConfirmedBet> = mapConfirmedBets()
internal fun JsonElement?.toPurchases(): List<Purchase> = mapTicketPurchases()

internal fun JsonObject.ticketLong(name: String): Long? = runCatching { ticketElement(name)?.asLong }.getOrNull()

internal fun JsonElement?.ticketFindFirstText(name: String): String = when {
    this == null || isJsonNull -> ""
    isJsonObject -> asJsonObject.ticketText(name).ifBlank {
        asJsonObject.entrySet().firstNotNullOfOrNull { (_, value) ->
            value.ticketFindFirstText(name).takeIf(String::isNotBlank)
        }.orEmpty()
    }
    isJsonArray -> asJsonArray.firstNotNullOfOrNull { value ->
        value.ticketFindFirstText(name).takeIf(String::isNotBlank)
    }.orEmpty()
    else -> ""
}

internal fun JsonObject.ticketText(name: String): String {
    val value = ticketElement(name) ?: return ""
    return when {
        value.isJsonPrimitive -> runCatching { value.asString }.getOrDefault("")
        value.isJsonObject -> listOf("descricao", "nome", "sigla", "valor")
            .firstNotNullOfOrNull { key -> value.asJsonObject.ticketElement(key)?.let { runCatching { it.asString }.getOrNull() } }.orEmpty()
        else -> ""
    }
}

internal fun String.ticketDisplayName(): String = trim().lowercase().split('_', '-', ' ')
    .filter(String::isNotBlank).joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
