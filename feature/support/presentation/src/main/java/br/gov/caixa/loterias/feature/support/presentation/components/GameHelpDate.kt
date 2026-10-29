package br.gov.caixa.loterias.feature.support.presentation.components

import br.gov.caixa.loterias.core.common.time.LotteryDateTimes
import br.gov.caixa.loterias.feature.support.presentation.formatting.localizedMessage as messageText
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun gameHelpDate(value: String, includeTime: Boolean = false): String {
    val text = value.trim()
    if (text.isEmpty()) return messageText("nao_informado_01af1e")
    LotteryDateTimes.parse(text)?.atZone(ZoneId.of("America/Sao_Paulo"))?.let { date ->
        val pattern = if (includeTime) "dd/MM | HH'h'mm" else "dd/MM"
        return date.format(DateTimeFormatter.ofPattern(pattern))
    }
    val date = runCatching { LocalDate.parse(text) }.getOrNull()
        ?: runCatching { LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/uuuu")) }.getOrNull()
    return date?.format(DateTimeFormatter.ofPattern("dd/MM")) ?: text
}
