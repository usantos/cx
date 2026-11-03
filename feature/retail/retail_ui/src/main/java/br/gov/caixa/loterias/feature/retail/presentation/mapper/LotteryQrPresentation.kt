package br.gov.caixa.loterias.feature.retail.presentation.mapper

fun formatRemainingTime(totalSeconds: Int): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    return "%02d:%02d".format(safeSeconds / SECONDS_PER_MINUTE, safeSeconds % SECONDS_PER_MINUTE)
}

enum class LotteryQrUrgency { EXPIRED, NORMAL, RED, PULSE, VIBRATE }

fun lotteryQrUrgency(remainingSeconds: Int): LotteryQrUrgency = when {
    remainingSeconds <= 0 -> LotteryQrUrgency.EXPIRED
    remainingSeconds <= QR_VIBRATION_THRESHOLD_SECONDS -> LotteryQrUrgency.VIBRATE
    remainingSeconds <= QR_PULSE_THRESHOLD_SECONDS -> LotteryQrUrgency.PULSE
    remainingSeconds <= QR_RED_THRESHOLD_SECONDS -> LotteryQrUrgency.RED
    else -> LotteryQrUrgency.NORMAL
}

private const val SECONDS_PER_MINUTE = 60
private const val QR_RED_THRESHOLD_SECONDS = 60
private const val QR_PULSE_THRESHOLD_SECONDS = 10
private const val QR_VIBRATION_THRESHOLD_SECONDS = 3
