package br.gov.caixa.loterias.feature.retail.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal

internal fun JsonObject.retailText(name: String): String = runCatching { get(name)?.takeUnless(JsonElement::isJsonNull)?.asString }.getOrNull().orEmpty()
internal fun JsonObject.retailLong(name: String): Long? = runCatching { get(name)?.takeUnless(JsonElement::isJsonNull)?.asLong }.getOrNull()
internal fun JsonObject.retailOptionalDecimal(name: String): BigDecimal? = runCatching { get(name)?.takeUnless(JsonElement::isJsonNull)?.asBigDecimal }.getOrNull()
internal fun JsonObject.retailDecimal(name: String): BigDecimal = retailOptionalDecimal(name) ?: BigDecimal.ZERO
internal fun JsonElement?.retailObjectOrEmpty(): JsonObject = this?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: JsonObject()
internal fun JsonObject.retailObject(name: String): JsonObject = get(name)?.retailObjectOrEmpty() ?: JsonObject()
internal fun JsonObject.retailArrayObjects(name: String): List<JsonObject> = get(name)?.takeIf(JsonElement::isJsonArray)?.asJsonArray
    ?.mapNotNull { it.takeIf(JsonElement::isJsonObject)?.asJsonObject }.orEmpty()
