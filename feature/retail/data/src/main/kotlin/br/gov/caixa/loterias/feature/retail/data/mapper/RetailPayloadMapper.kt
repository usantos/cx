package br.gov.caixa.loterias.feature.retail.data.mapper

import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import com.google.gson.JsonElement
import com.google.gson.JsonObject

internal fun JsonObject.toRetailer(favorite: Boolean) = Retailer(
    id = retailLong("id") ?: retailLong("codigo") ?: 0,
    code = retailText("numeroFormatado").ifBlank { retailText("codigo") },
    fantasyName = retailText("nomeFantasia"),
    legalName = retailText("nome"),
    address = retailText("logradouro"),
    district = retailText("bairro"),
    city = retailText("nomeMunicipio"),
    state = retailText("nomeUF").ifBlank { retailText("siglaUf") },
    favorite = favorite,
)

internal fun JsonElement?.retailObjects(): List<JsonObject> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.flatMap { it.retailObjects() }
    isJsonObject -> {
        val objectValue = asJsonObject
        if ((objectValue.has("id") || objectValue.has("codigo")) && objectValue.has("nomeFantasia")) listOf(objectValue)
        else objectValue.entrySet().flatMap { it.value.retailObjects() }
    }
    else -> emptyList()
}
