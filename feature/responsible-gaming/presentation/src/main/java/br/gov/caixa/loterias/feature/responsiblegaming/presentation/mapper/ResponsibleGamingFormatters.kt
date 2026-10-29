package br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper

import br.gov.caixa.loterias.feature.responsiblegaming.presentation.formatting.localizedMessage as messageText

import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import java.time.Instant
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.math.MathContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

internal data class SocialAmount(val value: String, val unit: String)

internal fun socialAmount(value: BigDecimal?): SocialAmount {
    val amount = value ?: BigDecimal.ZERO
    val (divisor, singular, plural) = when {
        amount >= BigDecimal("1000000000") -> Triple(
            BigDecimal("1000000000"),
            messageText("bilhao_de_reais_552327"),
            messageText("bilhoes_de_reais_a06f6e")
        )

        amount >= BigDecimal("1000000") -> Triple(
            BigDecimal("1000000"),
            messageText("milhao_de_reais_c7c808"),
            messageText("milhoes_de_reais_36a26e")
        )

        amount >= BigDecimal("1000") -> Triple(BigDecimal("1000"), "mil reais", "mil reais")

        else -> Triple(BigDecimal.ONE, "reais", "reais")
    }
    val scaled = amount.divide(divisor, 1, RoundingMode.DOWN)
    val unit = if (scaled < BigDecimal("2")) singular else plural
    return SocialAmount(scaled.toPlainString().replace('.', ','), unit)
}

internal fun currentSocialYear(): Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

internal fun formatCurrency(value: BigDecimal?): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    .format(value ?: BigDecimal.ZERO)
    .replace('\u00A0', ' ')
    .replace('\u202F', ' ')

internal fun formatSocialAmount(value: BigDecimal?): String {
    val amount = value ?: BigDecimal.ZERO
    val (divisor, suffix) = when {
        amount >= BigDecimal("1000000000") -> BigDecimal("1000000000") to " bi"
        amount >= BigDecimal("1000000") -> BigDecimal("1000000") to " mi"
        amount >= BigDecimal("1000") -> BigDecimal("1000") to " mil"
        else -> BigDecimal.ONE to ""
    }
    val scaled = amount.divide(divisor, 1, RoundingMode.DOWN)
    return "R$ ${scaled.toPlainString().replace('.', ',')}$suffix"
}

/** O repassômetro legado projeta 48% da receita semanal a partir da meia-noite de sua data base local. */
internal fun socialTransferredAmount(
    transfers: SocialTransfers?,
    now: Instant = Instant.now(),
    zone: ZoneId = ZoneId.systemDefault(),
): BigDecimal {
    if (transfers == null) return BigDecimal.ZERO
    val base = runCatching {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(zone)
        }.parse(transfers.baseDate)?.toInstant()
    }.getOrNull() ?: now
    val elapsedSeconds = BigDecimal.valueOf(now.toEpochMilli() - base.toEpochMilli())
        .divide(BigDecimal("1000"), MathContext.DECIMAL128)
    val transferredShare = BigDecimal("0.48")
    return transfers.accumulatedRevenue.multiply(transferredShare).add(
        transfers.lastWeekRevenue.multiply(transferredShare).multiply(elapsedSeconds)
            .divide(BigDecimal("604800"), MathContext.DECIMAL128)
    )
}
