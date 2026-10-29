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
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val label = if (hours > 0) String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds % 60)
        else String.format(Locale.ROOT, "%02d:%02d", minutes, seconds % 60)
    val color = when {
        hours == 0L && minutes < 5 -> 0xFFF60E0E
        hours == 0L && minutes < 10 -> 0xFFEAAA28
        else -> 0xFF004680
    }.toInt()
    return HomePoolResaleCountdown(label, color)
}
