package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.core.common.time.LotteryDateTimes
import java.time.Duration
import java.time.Instant
import java.util.Locale

internal data class HomePoolResaleCountdown(val label: String, val colorArgb: Int)

/** HomeViewHolder.startTimerPosEncerramento / EncerramentoUtil. */
internal fun homePoolResaleCountdown(individualClosing: String, resaleClosing: String, now: Instant): HomePoolResaleCountdown? {
    val individual = LotteryDateTimes.parse(individualClosing) ?: return null
    val resale = LotteryDateTimes.parse(resaleClosing) ?: return null
    if (now < individual || now >= resale) return null
    val seconds = Duration.between(now, resale).seconds
    val hours = seconds / SECONDS_PER_HOUR
    val minutes = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val label = if (hours > 0) {
        String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds % SECONDS_PER_MINUTE)
    } else {
        String.format(Locale.ROOT, "%02d:%02d", minutes, seconds % SECONDS_PER_MINUTE)
    }
    val color = when {
        hours == 0L && minutes < URGENT_MINUTES -> URGENT_COLOR_ARGB
        hours == 0L && minutes < WARNING_MINUTES -> WARNING_COLOR_ARGB
        else -> DEFAULT_COLOR_ARGB
    }.toInt()
    return HomePoolResaleCountdown(label, color)
}

private const val SECONDS_PER_HOUR = 3_600
private const val SECONDS_PER_MINUTE = 60
private const val URGENT_MINUTES = 5
private const val WARNING_MINUTES = 10
private const val URGENT_COLOR_ARGB = 0xFFF60E0E
private const val WARNING_COLOR_ARGB = 0xFFEAAA28
private const val DEFAULT_COLOR_ARGB = 0xFF004680
