package br.gov.caixa.loterias.feature.retail.presentation.mapper

import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText

import br.gov.caixa.loterias.feature.retail.domain.model.PrizeAward
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import java.text.NumberFormat
import java.util.Locale
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

internal fun formatPrizeClosingDate(value: String): String {
    val date = runCatching { LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) }.getOrNull()
        ?: runCatching { LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME) }.getOrNull()
        ?: return ""
    return date.format(DateTimeFormatter.ofPattern("dd/MM | HH'h'mm"))
}

internal enum class PrizeBetType(val label: String) {
    EXCHANGE(messageText("aposta_de_troca_5f4dcc")),
    POOL(messageText("bolao_99927e")),
    COMBO("Combo"),
    MIRROR("Espelho"),
    SURPRISE("Surpresinha"),
    RECURRING("Teimosinha"),
}

internal fun prizeBetTypes(details: PrizeDetails): List<PrizeBetType> = buildList {
    if (details.exchange) add(PrizeBetType.EXCHANGE)
    if (details.isPool) add(PrizeBetType.POOL)
    if (details.combo) add(PrizeBetType.COMBO)
    if (details.mirror) add(PrizeBetType.MIRROR)
    if (details.surprise) add(PrizeBetType.SURPRISE)
    if (details.recurringDraws > 0) add(PrizeBetType.RECURRING)
}

/** Ordem das colunas das tabelas legadas de prêmio individual e bolão. Mantém a ordem das linhas do servidor. */
internal fun prizeAwardCells(details: PrizeDetails, award: PrizeAward, index: Int): List<String> {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    val range = award.description.ifEmpty { "-" }
    val amount = award.amount?.let(currency::format) ?: "-"
    return if (details.isPool) {
        listOf(range, amount, if (index == 0) currency.format(details.netValue) else "")
    } else {
        listOf(
            award.contest.toString(),
            award.status,
            range,
            award.hitCount?.toString() ?: "-",
            award.draw?.toString() ?: "-",
            amount
        )
    }
}

/** Somente uma fixture explicitamente controlada pode renderizar uma rota terminal sem um resgate. */
internal fun prizeScreenState(
    actual: PrizeRedemptionUiState,
    previewChannel: br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel?,
    allowControlledSuccessPreview: Boolean = false,
): PrizeRedemptionUiState = if (
    allowControlledSuccessPreview && previewChannel != null &&
    actual.stage != PrizeRedemptionStage.LOADING && actual.stage != PrizeRedemptionStage.SUCCESS
) {
    actual.copy(stage = PrizeRedemptionStage.SUCCESS, selectedChannel = previewChannel, loading = false)
} else {
    actual
}

/** O resultado instalado lista apenas PremioDTO.concursos; outros registros de concursos permanecem no modelo. */
internal fun prizeAwardedRows(details: PrizeDetails) = details.awards.filter { it.isAwarded }
