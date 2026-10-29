package br.gov.caixa.loterias.core.network.mapper

import com.google.gson.JsonElement

fun JsonElement?.flattenNumbers(): List<Int> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
    else -> emptyList()
}
