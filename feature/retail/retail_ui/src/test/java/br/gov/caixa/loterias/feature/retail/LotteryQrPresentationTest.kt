package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.presentation.mapper.LotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.mapper.formatRemainingTime
import br.gov.caixa.loterias.feature.retail.presentation.mapper.lotteryQrUrgency
import org.junit.Assert.assertEquals
import org.junit.Test

class LotteryQrPresentationTest {
    @Test fun `formats countdown using minutes and seconds`() {
        assertEquals("14:59", formatRemainingTime(COUNTDOWN_SAMPLE_SECONDS))
        assertEquals("01:00", formatRemainingTime(ONE_MINUTE_SECONDS))
        assertEquals("00:09", formatRemainingTime(SINGLE_DIGIT_SECONDS))
    }

    @Test fun `negative countdown is clamped to zero`() {
        assertEquals("00:00", formatRemainingTime(-1))
    }

    @Test fun `qr urgency preserves legacy thresholds`() {
        assertEquals(LotteryQrUrgency.NORMAL, lotteryQrUrgency(ABOVE_RED_THRESHOLD_SECONDS))
        assertEquals(LotteryQrUrgency.RED, lotteryQrUrgency(RED_THRESHOLD_SECONDS))
        assertEquals(LotteryQrUrgency.PULSE, lotteryQrUrgency(PULSE_THRESHOLD_SECONDS))
        assertEquals(LotteryQrUrgency.VIBRATE, lotteryQrUrgency(VIBRATION_THRESHOLD_SECONDS))
        assertEquals(LotteryQrUrgency.EXPIRED, lotteryQrUrgency(0))
    }
}

private const val COUNTDOWN_SAMPLE_SECONDS = 899
private const val ONE_MINUTE_SECONDS = 60
private const val SINGLE_DIGIT_SECONDS = 9
private const val ABOVE_RED_THRESHOLD_SECONDS = 61
private const val RED_THRESHOLD_SECONDS = 60
private const val PULSE_THRESHOLD_SECONDS = 10
private const val VIBRATION_THRESHOLD_SECONDS = 3
