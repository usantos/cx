package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto

internal fun LotteryModalityDto.toBettingModality() = LotteryModality(
    id = id ?: 0,
    name = name.orEmpty(),
    code = bettingModalityCode(id ?: 0),
    specialName = specialName.orEmpty(),
    managerName = managerName.orEmpty(),
)

internal fun bettingModalityCode(id: Int) = when (id) {
    2 -> "MEGA_SENA"
    3 -> "QUINA"
    7 -> "SUPER_7"
    8 -> "LOTOFACIL"
    9 -> "MAIS_MILIONARIA"
    11 -> "DIA_DE_SORTE"
    14 -> "LOTOGOL"
    16 -> "LOTOMANIA"
    18 -> "DUPLA_SENA"
    19 -> "LOTECA"
    20 -> "TIMEMANIA"
    1502 -> "INSTANTANEA"
    else -> ""
}

internal fun bettingModalityId(code: String?) = when (code) {
    "MEGA_SENA" -> 2
    "QUINA" -> 3
    "SUPER_7" -> 7
    "LOTOFACIL" -> 8
    "MAIS_MILIONARIA" -> 9
    "DIA_DE_SORTE" -> 11
    "LOTOGOL" -> 14
    "LOTOMANIA" -> 16
    "DUPLA_SENA" -> 18
    "LOTECA" -> 19
    "TIMEMANIA" -> 20
    "INSTANTANEA" -> 1502
    else -> 0
}

internal fun String?.displayBettingModalityName() = orEmpty().lowercase().split('_')
    .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
