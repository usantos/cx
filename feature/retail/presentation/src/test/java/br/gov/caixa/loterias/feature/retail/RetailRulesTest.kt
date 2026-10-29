package br.gov.caixa.loterias.feature.retail

import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.mapper.LotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.mapper.canContinuePix
import br.gov.caixa.loterias.feature.retail.presentation.mapper.formatRemainingTime
import br.gov.caixa.loterias.feature.retail.presentation.mapper.isValidPixKey
import br.gov.caixa.loterias.feature.retail.presentation.mapper.isValidPrizeEmail
import br.gov.caixa.loterias.feature.retail.presentation.mapper.lotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.mapper.selectedPixKey

class RetailRulesTest {
    @Test fun `formats countdown using minutes and seconds`() {
        assertEquals("14:59", formatRemainingTime(899))
        assertEquals("01:00", formatRemainingTime(60))
        assertEquals("00:09", formatRemainingTime(9))
    }

    @Test fun `negative countdown is clamped to zero`() {
        assertEquals("00:00", formatRemainingTime(-1))
    }

    @Test fun `prize email requires local part domain and suffix`() {
        assertEquals(true, isValidPrizeEmail("apostador@caixa.gov.br"))
        assertEquals(false, isValidPrizeEmail(""))
        assertEquals(false, isValidPrizeEmail("@caixa.gov.br"))
        assertEquals(false, isValidPrizeEmail("apostador@caixa"))
        assertEquals(false, isValidPrizeEmail("apostador@.br"))
        assertEquals(false, isValidPrizeEmail("apostador@caixa."))
    }

    @Test fun `mercado pago rejects malformed addresses before redemption`() {
        listOf(
            " apostador@caixa.gov.br ", "a@@caixa.gov.br", "a b@caixa.gov.br",
            "a..b@caixa.gov.br", "a@caixa.b", "a@caixa.123", "a@caixa.gov.br\n",
        ).forEach { email -> assertEquals(email, false, isValidPrizeEmail(email)) }
    }

    @Test fun `pix preserves its distinct legacy email contract`() {
        assertEquals(true, isValidPixKey("cliente+premio@exemplo.com.br"))
        assertEquals(false, isValidPrizeEmail("cliente+premio@exemplo.com.br"))
        assertEquals(true, isValidPixKey("cliente@meu-dominio.com.br"))
        listOf("a@@b.com", "a b@b.com", " cliente@caixa.gov.br ", "11999999999 ").forEach {
            assertEquals(it, false, isValidPixKey(it))
        }
    }

    @Test fun `validates every pix key format from legacy app`() {
        assertEquals(true, isValidPixKey("11999999999"))
        assertEquals(true, isValidPixKey("cliente@caixa.gov.br"))
        assertEquals(true, isValidPixKey("123e4567-e89b-12d3-a456-426614174000"))
        assertEquals(false, isValidPixKey("123E4567-E89B-12D3-A456-426614174000"))
        assertEquals(false, isValidPixKey("123"))
        assertEquals(false, isValidPixKey(""))
    }

    @Test fun `pix selection requires ownership only for another key`() {
        val cpf = PrizeRedemptionUiState(cpf = "12345678901")
        assertEquals("12345678901", cpf.selectedPixKey())
        assertEquals(true, cpf.canContinuePix())

        val other = cpf.copy(
            pixKeySource = PrizePixKeySource.OTHER,
            pixKey = "cliente@caixa.gov.br",
        )
        assertEquals(false, other.canContinuePix())
        assertEquals(true, other.copy(pixOwnershipAccepted = true).canContinuePix())
    }

    @Test fun `qr urgency preserves legacy thresholds`() {
        assertEquals(LotteryQrUrgency.NORMAL, lotteryQrUrgency(61))
        assertEquals(LotteryQrUrgency.RED, lotteryQrUrgency(60))
        assertEquals(LotteryQrUrgency.PULSE, lotteryQrUrgency(10))
        assertEquals(LotteryQrUrgency.VIBRATE, lotteryQrUrgency(3))
        assertEquals(LotteryQrUrgency.EXPIRED, lotteryQrUrgency(0))
    }
}
