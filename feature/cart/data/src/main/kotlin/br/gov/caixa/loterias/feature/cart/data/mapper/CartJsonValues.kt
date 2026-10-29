package br.gov.caixa.loterias.feature.cart.data.mapper

import com.google.gson.JsonElement
import java.math.BigDecimal

internal fun String.cartDisplayName(): String = trim().lowercase()
    .split('_', '-', ' ')
    .filter(String::isNotBlank)
    .joinToString(" ") { word -> word.replaceFirstChar(Char::uppercase) }

internal fun JsonElement?.cartNumberRows(): List<List<Int>> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray && asJsonArray.any { it.isJsonArray } -> asJsonArray.map { it.cartFlattenNumbers() }
    else -> listOf(cartFlattenNumbers()).filter(List<Int>::isNotEmpty)
}

internal fun JsonElement?.cartFlattenNumbers(): List<Int> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.flatMap { it.cartFlattenNumbers() }
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
    else -> emptyList()
}

internal fun JsonElement?.cartModalityCode(): String {
    val raw = when {
        this == null || isJsonNull -> ""
        isJsonPrimitive -> asString
        isJsonObject -> asJsonObject.cartFirstText("descricaoGestor", "nome", "descricao", "valor")
        else -> ""
    }
    return raw.trim().uppercase().replace('-', '_').replace(' ', '_')
}

internal fun JsonElement?.cartModalityName(contestType: JsonElement?): String {
    if (this == null || isJsonNull) return ""
    if (isJsonObject) {
        val value = asJsonObject
        val special = contestType.cartTextValue().contains("ESPECIAL", ignoreCase = true)
        return value.cartFirstText(*(if (special) arrayOf("descricaoEspecial", "descricao", "nome", "descricaoGestor") else arrayOf("descricao", "nome", "descricaoGestor")))
            .ifBlank { cartModalityCode().cartDisplayName() }
    }
    return cartModalityCode().cartDisplayName()
}

internal fun JsonElement?.cartTextValue(): String = when {
    this == null || isJsonNull -> ""
    isJsonPrimitive -> asString
    isJsonObject -> asJsonObject.cartFirstText("descricao", "nome", "valor", "abreviacao")
    else -> ""
}

internal fun com.google.gson.JsonObject.cartFirstText(vararg names: String): String = names.firstNotNullOfOrNull { name ->
    get(name)?.takeUnless { it.isJsonNull }?.let { element ->
        when {
            element.isJsonPrimitive -> element.asString
            element.isJsonObject -> element.asJsonObject.cartFirstText("nome", "descricao", "valor", "uf")
            else -> null
        }
    }?.takeIf(String::isNotBlank)
}.orEmpty()

internal fun Int?.cartOrZero() = this ?: 0

internal fun BigDecimal?.cartOrZero() = this ?: BigDecimal.ZERO
