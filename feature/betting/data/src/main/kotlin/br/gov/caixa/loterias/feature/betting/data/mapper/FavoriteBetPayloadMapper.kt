package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.data.localizedMessage as messageText

import br.gov.caixa.loterias.core.network.mapper.flattenNumbers
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.data.dto.FavoriteBetDto
import com.google.gson.JsonElement

internal fun FavoriteBetDto.toDomain(): FavoriteBet {
    val rows = selectedNumbers.numberRows()
    return FavoriteBet(
        id = id ?: 0,
        modalityId = modality?.value ?: 0,
        modalityName = modality?.description.orEmpty(),
        name = name.orEmpty().ifBlank { messageText("aposta_favorita_5aae3e") },
        numberColumns = rows,
        heartTeam = heartTeam?.name.orEmpty().let { name ->
            heartTeam?.state?.takeIf(String::isNotBlank)?.let { "$name/$it" } ?: name
        },
        luckyMonth = luckyMonth?.name.orEmpty(),
        clovers = clovers?.selected.flattenNumbers()
    )
}

internal fun JsonElement?.numberRows(): List<List<Int>> = when {
    this == null || isJsonNull -> emptyList()
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(listOf(asInt))
    isJsonArray && asJsonArray.any { it.isJsonArray } -> asJsonArray.map { it.flattenNumbers() }
    isJsonArray -> listOf(flattenNumbers())
    else -> emptyList()
}
