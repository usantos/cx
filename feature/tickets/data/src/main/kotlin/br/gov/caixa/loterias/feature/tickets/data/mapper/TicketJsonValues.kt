package br.gov.caixa.loterias.feature.tickets.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal

internal fun JsonElement?.ticketPayloadObject(): JsonObject? = when {
    this == null || isJsonNull -> null
    isJsonObject && asJsonObject.has("payload") -> asJsonObject.get("payload").ticketPayloadObject()
    isJsonObject -> asJsonObject
    else -> null
}

internal fun JsonElement?.ticketPayloadArrayObjects(): List<JsonObject> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.mapNotNull { it.takeIf(JsonElement::isJsonObject)?.asJsonObject }
    isJsonObject && asJsonObject.has("payload") -> asJsonObject.get("payload").ticketPayloadArrayObjects()
    else -> emptyList()
}

/** Reproduz ResultadoConsultaBilheteDTO e ViewUtils.getFaixaPremiadoNaoPremiado. */

internal fun JsonElement?.ticketCollectObjects(): List<JsonObject> = when {
    this == null || isJsonNull -> emptyList()
    isJsonObject -> listOf(asJsonObject) + asJsonObject.entrySet().flatMap { it.value.ticketCollectObjects() }
    isJsonArray -> asJsonArray.flatMap { it.ticketCollectObjects() }
    else -> emptyList()
}

internal fun JsonObject.ticketElement(name: String): JsonElement? = get(name)?.takeUnless(JsonElement::isJsonNull)

internal fun JsonObject.ticketInt(name: String): Int? = runCatching { ticketElement(name)?.asInt }.getOrNull()

internal fun JsonObject.ticketBool(name: String): Boolean = runCatching { ticketElement(name)?.asBoolean }.getOrNull() ?: false

internal fun JsonObject.ticketDecimal(name: String): BigDecimal = runCatching { ticketElement(name)?.asBigDecimal }.getOrNull() ?: BigDecimal.ZERO

internal fun JsonObject.ticketChildInt(name: String, child: String): Int? = runCatching { ticketElement(name)?.asJsonObject?.ticketInt(child) }.getOrNull()

internal fun JsonObject.ticketChildLong(name: String, child: String): Long? = runCatching { ticketElement(name)?.asJsonObject?.ticketLong(child) }.getOrNull()

internal fun JsonObject.ticketRawText(name: String): String = ticketElement(name)?.let { value ->
    when {
        value.isJsonPrimitive -> runCatching { value.asString }.getOrDefault("")
        value.isJsonObject -> value.asJsonObject.ticketText("descricao").ifBlank {
            value.asJsonObject.ticketText("nome")
        }
        else -> ""
    }
}.orEmpty()

internal fun JsonObject.ticketNumbers(name: String): List<Int> = ticketElement(name).ticketFlattenNumbers()

internal fun JsonElement?.ticketNumberRows(): List<List<Int>> = when {
    this == null || isJsonNull -> emptyList()
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(listOf(asInt))
    isJsonArray && asJsonArray.any(JsonElement::isJsonArray) -> asJsonArray.map { it.ticketFlattenNumbers() }
    isJsonArray -> listOf(ticketFlattenNumbers())
    else -> emptyList()
}

internal fun JsonElement?.ticketFlattenNumbers(): List<Int> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.flatMap { it.ticketFlattenNumbers() }
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
    else -> emptyList()
}
