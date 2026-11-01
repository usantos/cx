package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.dto.CartCodeDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartLotecaDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartLotogolDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartMonthDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartTeamDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartUploadDto
import br.gov.caixa.loterias.feature.cart.domain.model.CartMatchTeam
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import br.gov.caixa.loterias.feature.cart.domain.model.CartTeam
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet
import com.google.gson.JsonArray

/** Constrói o contrato legado de verificação sem serializar metadados de domínio ou SQLite. */
fun LocalCartBet.toUploadDto() = CartUploadDto(
    id = serverId?.takeUnless { it == 0L },
    modality = modality.name,
    // Compatibilidade com apostas visitantes gravadas antes da correção do contrato.
    // A conversão ocorre no envio, preservando o conteúdo assinado pelo recibo local.
    contestType = CartCodeDto(contestType.value.cartContestCode(), contestType.description),
    numbers = when (val selection = numbers) {
        null -> null
        is CartNumbers.Flat -> selection.values.toNumberArray()
        is CartNumbers.Columns -> JsonArray().apply { selection.values.forEach { add(it.toNumberArray()) } }
    },
    clovers = clovers?.toList(),
    quickPickIndicator = CartCodeDto(uploadSurpriseIndicator(), quickPickIndicator.description),
    amount = amount,
    targetContest = targetContest,
    repeatCount = repeatCount,
    betCount = betCount,
    mirrored = mirrored,
    generateMirror = generateMirror,
    initialContest = initialContest,
    status = CartCodeDto(status.value, status.description),
    replacement = replacement,
    effectiveDate = effectiveDate,
    effectiveTime = effectiveTime,
    mirrorLink = mirrorLink,
    numberCount = numberCount,
    cloverCount = cloverCount,
    quickPick = quickPick,
    favoriteTeam = favoriteTeam.toUploadDto(),
    luckyMonth = luckyMonth?.let { CartMonthDto(it.number, it.name, it.abbreviation) } ?: CartMonthDto(),
    loteca = loteca.map { CartLotecaDto(it.first.toUploadDto(), it.second.toUploadDto(), it.draw) },
    lotogol = lotogol.map { CartLotogolDto(it.first.toUploadDto(), it.second.toUploadDto(), it.firstScoreIndex, it.secondScoreIndex) }
)

private fun List<Int>.toNumberArray() = JsonArray().apply {
    this@toNumberArray.forEach { number -> add(number) }
}

private fun CartTeam?.toUploadDto(): CartTeamDto = if (this == null) {
    CartTeamDto()
} else {
    CartTeamDto(
        number, name, countryNumber, country, countryCode, state, className, isNationalTeam, shortDescription, longDescription
    )
}

private fun CartMatchTeam.toUploadDto() = team.toUploadDto().copy(
    parameter = parameter.toUploadDto(),
    score = score,
    winner = winner
)

private fun LocalCartBet.uploadSurpriseIndicator(): Int = when {
    quickPickIndicator.value in 0..1 && quickPick -> if (modality == CartModality.TIMEMANIA && favoriteTeam != null) {
        NUMERIC_SURPRISE_INDICATOR
    } else {
        2
    }

    quickPickIndicator.value == 0 -> 1

    else -> quickPickIndicator.value
}

internal fun String?.cartContestCode(): String? = when (this) {
    "N" -> "1"
    "E" -> "2"
    else -> this
}

private const val NUMERIC_SURPRISE_INDICATOR = 3
