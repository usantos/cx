package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/** O repassômetro da Home projeta os 48% da receita diária transferidos para causas sociais. */
internal fun SocialTransfers.projectedHomeTransfers(nowMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Double {
    val baseMillis = try {
        LocalDate.parse(baseDate, DateTimeFormatter.ofPattern("dd/MM/uuuu"))
            .atStartOfDay(zoneId).toInstant().toEpochMilli()
    } catch (_: DateTimeParseException) {
        nowMillis
    }
    val seconds = (nowMillis - baseMillis).toDouble() / 1_000.0
    return accumulatedRevenue.toDouble() * HOME_SOCIAL_TRANSFER_SHARE +
        lastWeekRevenue.toDouble() * HOME_SOCIAL_TRANSFER_SHARE / 604_800.0 * seconds
}

/** O primeiro ValueAnimator legado começa em 48% da receita acumulada já repartida. */
internal fun SocialTransfers.initialHomeTransfers(): Double =
    accumulatedRevenue.toDouble() * HOME_SOCIAL_TRANSFER_SHARE * HOME_SOCIAL_TRANSFER_SHARE

private const val HOME_SOCIAL_TRANSFER_SHARE = 0.48
