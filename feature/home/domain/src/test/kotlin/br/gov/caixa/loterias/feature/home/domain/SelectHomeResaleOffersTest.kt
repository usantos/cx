package br.gov.caixa.loterias.feature.home.domain

import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectHomeResaleOffersTest {
    private val deadline = Instant.parse("2026-10-21T19:00:00Z")
    private val offer = PoolModalityAvailability(2, "MEGA_SENA", "NORMAL", 3073, "21/10/2026", "16:00:00")

    @Test fun `resale uses injected server time including closing instant`() {
        var serverNow = deadline.minusSeconds(1)
        val select = SelectHomeResaleOffers(ServerTimeSource { serverNow })
        assertEquals(listOf(offer), select(listOf(offer), true))
        serverNow = deadline
        assertEquals(listOf(offer), select(listOf(offer), true))
        serverNow = deadline.plusMillis(1)
        assertTrue(select(listOf(offer), true).isEmpty())
    }

    @Test fun `disabled resale malformed closing and missing contest are rejected`() {
        val select = SelectHomeResaleOffers(ServerTimeSource { deadline })
        assertTrue(select(listOf(offer), false).isEmpty())
        assertTrue(select(listOf(offer.copy(contestNumber = null), offer.copy(resaleClosingDate = "invalid")), true).isEmpty())
    }
}
