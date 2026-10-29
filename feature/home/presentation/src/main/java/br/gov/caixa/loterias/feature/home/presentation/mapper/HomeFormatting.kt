package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText

import java.math.BigDecimal
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val brazil = Locale.forLanguageTag("pt-BR")
private val legacyDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", brazil)
private val legacyDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", brazil)
private val displayDateTime = DateTimeFormatter.ofPattern("EEEE,\ndd/MM/yyyy 'às' HH:mm:ss", brazil)

fun formatCompactPrize(value: BigDecimal): String {
    val million = BigDecimal("1000000")
    val thousand = BigDecimal("1000")
    return when {
        value >= million -> {
            val millions = value.divide(million).stripTrailingZeros()
            "R$ ${millions.toPlainString().replace(
                '.',
                ','
            )} ${if (millions.compareTo(BigDecimal.ONE) == 0) messageText("milhao_afa9f1") else messageText("milhoes_44bbbb")}"
        }

        value >= thousand -> "R$ ${value.divide(thousand).stripTrailingZeros().toPlainString()} Mil"

        else -> formatBrazilianCurrency(value)
    }
}

fun formatHomeDrawDate(value: String): String {
    val parsed = parseDateTime(value) ?: return value
    return displayDateTime.format(parsed).replaceFirstChar { it.titlecase(brazil) }
}

fun remainingTimeLabel(closingDateTime: String, nowMillis: Long = System.currentTimeMillis()): String {
    val closing = parseDateTime(closingDateTime) ?: return messageText("horario_indisponivel_ac0a5e")
    val remaining = Duration.between(
        java.time.Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()).toLocalDateTime(),
        closing
    )
    if (remaining.isNegative || remaining.isZero) return "Encerradas"
    val days = remaining.toDays()
    val hours = remaining.minusDays(days).toHours()
    val minutes = remaining.minusDays(days).minusHours(hours).toMinutes()
    val seconds = remaining.minusDays(days).minusHours(hours).minusMinutes(minutes).seconds
    return when {
        days > 0 -> unit(days, "dia", "dias") + if (hours > 0) " e ${unit(hours, "hora", "horas")}" else ""
        hours > 0 -> unit(hours, "hora", "horas") + " e ${unit(minutes, "minuto", "minutos")}"
        minutes > 0 -> unit(minutes, "minuto", "minutos")
        else -> unit(seconds, "segundo", "segundos")
    }
}

private fun unit(value: Long, singular: String, plural: String) = "$value ${if (value == 1L) singular else plural}"

private fun parseDateTime(value: String): LocalDateTime? {
    if (value.isBlank()) return null
    return try {
        LocalDateTime.parse(value, legacyDateTime)
    } catch (_: DateTimeParseException) {
        try {
            java.time.LocalDate.parse(value, legacyDate).atStartOfDay()
        } catch (_: DateTimeParseException) {
            runCatching { LocalDateTime.parse(value) }.getOrNull()
        }
    }
}
