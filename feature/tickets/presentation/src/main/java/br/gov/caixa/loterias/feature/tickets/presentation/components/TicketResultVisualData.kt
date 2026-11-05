package br.gov.caixa.loterias.feature.tickets.presentation.components

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Entradas de exibição mapeadas do contrato de consulta de bilhete e dos parâmetros de aposta da sessão. */
data class TicketResultVisualData(
    val modalityId: Int? = null,
    val modalityName: String = "",
    val ticketStatusId: Int? = null,
    val netPrize: BigDecimal? = null,
    val awardRows: List<TicketResultAwardRow> = emptyList(),
    val currentEstimate: String? = null,
    val currentClosing: String? = null,
)

data class TicketResultAwardRow(
    val contest: String,
    val status: String,
    val award: String,
    val quantity: String,
    val draw: String,
    val value: String,
)

fun ticketResultVisualData(
    result: br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult,
    currentGame: br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryGameParameters? = null,
): TicketResultVisualData {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return TicketResultVisualData(
        modalityId = result.modalityId,
        modalityName = result.modalityName,
        ticketStatusId = result.statusId,
        netPrize = result.netPrize,
        awardRows = result.awards.map { row ->
            TicketResultAwardRow(
                row.contest?.toString() ?: "-",
                row.status,
                row.name.ifBlank { "-" },
                row.hitQuantity?.toString() ?: "-",
                row.draw?.toString() ?: "-",
                row.value?.let(currency::format) ?: "-"
            )
        },
        currentEstimate = currentGame?.takeIf { it.hasNextContest }?.estimatedPrize?.let(currency::format),
        currentClosing = currentGame?.takeIf { it.hasNextContest }?.closingDateTime?.takeIf(String::isNotBlank)?.let(::ticketClosingLabel)
    )
}

private fun ticketClosingLabel(value: String): String {
    // ViewUtils.getDateAndHour legado: dd/MM/yyyy HH:mm:ss -> dd/MM | HHhmm.
    val match = Regex("""(\d{2}/\d{2})/\d{4} (\d{2}):(\d{2}).*""").matchEntire(value)
    return match?.let { "${it.groupValues[1]} | ${it.groupValues[2]}h${it.groupValues[CLOSING_MINUTE_GROUP]}" } ?: value
}

private const val CLOSING_MINUTE_GROUP = 3
