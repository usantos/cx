package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.domain.retail.PrizeAward
import br.gov.caixa.loterias.core.domain.retail.PrizeDetails
import java.text.NumberFormat
import java.util.Locale
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun formatPrizeClosingDate(value: String): String {
    val date = runCatching { LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) }.getOrNull()
        ?: runCatching { LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME) }.getOrNull()
        ?: return ""
    return date.format(DateTimeFormatter.ofPattern("dd/MM | HH'h'mm"))
}

internal enum class PrizeBetType(val label: String) {
    EXCHANGE("Aposta de Troca"), POOL("Bolão"), COMBO("Combo"), MIRROR("Espelho"),
    SURPRISE("Surpresinha"), RECURRING("Teimosinha"),
}

internal fun prizeBetTypes(details: PrizeDetails): List<PrizeBetType> = buildList {
    if (details.exchange) add(PrizeBetType.EXCHANGE)
    if (details.isPool) add(PrizeBetType.POOL)
    if (details.combo) add(PrizeBetType.COMBO)
    if (details.mirror) add(PrizeBetType.MIRROR)
    if (details.surprise) add(PrizeBetType.SURPRISE)
    if (details.recurringDraws > 0) add(PrizeBetType.RECURRING)
}

/** Column order from the legacy individual/pool prize tables. Keep backend row order. */
internal fun prizeAwardCells(details: PrizeDetails, award: PrizeAward, index: Int): List<String> {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    val range = award.description.ifEmpty { "-" }
    val amount = award.amount?.let(currency::format) ?: "-"
    return if (details.isPool) {
        listOf(range, amount, if (index == 0) currency.format(details.netValue) else "")
    } else {
        listOf(
            award.contest.toString(), award.status, range,
            award.hitCount?.toString() ?: "-", award.draw?.toString() ?: "-", amount,
        )
    }
}


/** Only an explicitly controlled fixture may render a terminal route without a redemption. */
internal fun prizeScreenState(
    actual: PrizeRedemptionUiState,
    previewChannel: br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionChannel?,
    allowControlledSuccessPreview: Boolean = false,
): PrizeRedemptionUiState = if (
    allowControlledSuccessPreview && previewChannel != null &&
    actual.stage != PrizeRedemptionStage.LOADING && actual.stage != PrizeRedemptionStage.SUCCESS
) actual.copy(stage = PrizeRedemptionStage.SUCCESS, selectedChannel = previewChannel, loading = false)
else actual

/** Installed result lists only PremioDTO.concursos; other contest records stay in the model. */
internal fun prizeAwardedRows(details: PrizeDetails) = details.awards.filter { it.isAwarded }
