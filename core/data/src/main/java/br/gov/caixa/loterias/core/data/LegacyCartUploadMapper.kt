package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.network.*
import com.google.gson.JsonArray

/** Builds the legacy verification contract without serializing Domain or SQLite metadata. */
internal fun LegacyLocalCartBet.toUploadDto() = LegacyCartUploadDto(
    id = serverId?.takeUnless { it == 0L },
    modality = modality.name,
    contestType = LegacyCartCodeDto(contestType.value, contestType.description),
    numbers = when (val selection = numbers) {
        null -> null
        is LegacyCartNumbers.Flat -> selection.values.toNumberArray()
        is LegacyCartNumbers.Columns -> JsonArray().apply { selection.values.forEach { add(it.toNumberArray()) } }
    },
    clovers = clovers?.toList(),
    quickPickIndicator = LegacyCartCodeDto(quickPickIndicator.value, quickPickIndicator.description),
    amount = amount,
    targetContest = targetContest,
    repeatCount = repeatCount,
    betCount = betCount,
    mirrored = mirrored,
    generateMirror = generateMirror,
    initialContest = initialContest,
    status = LegacyCartCodeDto(status.value, status.description),
    replacement = replacement,
    effectiveDate = effectiveDate,
    effectiveTime = effectiveTime,
    mirrorLink = mirrorLink,
    numberCount = numberCount,
    cloverCount = cloverCount,
    quickPick = quickPick,
    favoriteTeam = favoriteTeam.toUploadDto(),
    luckyMonth = luckyMonth?.let { LegacyCartMonthDto(it.number, it.name, it.abbreviation) } ?: LegacyCartMonthDto(),
    loteca = loteca.map { LegacyCartLotecaDto(it.first.toUploadDto(), it.second.toUploadDto(), it.draw) },
    lotogol = lotogol.map { LegacyCartLotogolDto(it.first.toUploadDto(), it.second.toUploadDto(), it.firstScoreIndex, it.secondScoreIndex) },
)

private fun List<Int>.toNumberArray() = JsonArray().apply {
    this@toNumberArray.forEach { number -> add(number) }
}

private fun LegacyCartTeam?.toUploadDto(): LegacyCartTeamDto = if (this == null) LegacyCartTeamDto() else LegacyCartTeamDto(
    number, name, countryNumber, country, countryCode, state, className, isNationalTeam, shortDescription, longDescription,
)

private fun LegacyCartMatchTeam.toUploadDto() = team.toUploadDto().copy(
    parameter = parameter.toUploadDto(), score = score, winner = winner,
)
