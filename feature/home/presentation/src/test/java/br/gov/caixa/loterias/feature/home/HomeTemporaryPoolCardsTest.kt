package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.feature.betting.domain.model.*
import br.gov.caixa.loterias.feature.home.presentation.mapper.shouldShowPoolButton
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.*
import org.junit.Test

class HomeTemporaryPoolCardsTest {
    private val now = Instant.parse("2026-10-21T18:00:00Z")
    private val mega = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
        3074, "22/10/2026 21:00:00", 60, 6, 20, 10, emptyList(), emptyList(), estimatedPrize = BigDecimal.TEN)
    private val pool = PoolModalityAvailability(2, "MEGA_SENA", "NORMAL", 3073,
        "21/10/2026", "16:00:00", LotteryModality(2, "Mega-Sena", "MEGA_SENA", "Mega da Virada"),
        BigDecimal("200000000"), "21/10/2026 21:00:00")
    private fun parameters(games: List<LotteryGameParameters> = listOf(mega), pools: List<PoolModalityAvailability> = listOf(pool)) =
        BettingParameters(BigDecimal.TEN, BigDecimal.TEN, 10, games, pools)
    private fun BettingParameters.cards(enabled: Boolean = true, instant: Instant = now) =
        toHomeLotteries(false, HomeSpecialContests(3073), enabled, br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
            br.gov.caixa.loterias.core.common.time.ServerTimeSource { instant },
        )(availablePoolModalities, enabled))

    @Test fun `valid resale replaces different contest with real availability metadata and preserves same contest`() {
        val card = parameters().cards().single()
        assertEquals(3073, card.contestNumber)
        assertEquals(pool.estimatedPrize, card.estimatedPrize)
        assertEquals(pool.drawDateTime, card.drawDate)
        assertFalse(card.open)
        assertEquals("21/10/2026 16:00:00", card.poolResaleClosingDateTime)
        assertTrue(shouldShowPoolButton(true, parameters(), card))
        val sameContest = parameters(listOf(mega.copy(contestNumber = 3073))).cards().single()
        assertEquals(BigDecimal.TEN, sameContest.estimatedPrize)
        assertTrue(sameContest.open)
        assertEquals(mega.drawDate, sameContest.drawDate)
    }

    @Test fun `special missing card inserts first normal missing card inserts last and preserves configured banner`() {
        val quina = mega.copy(modality = LotteryModality(3, "Quina", "QUINA"))
        val special = parameters(listOf(quina), listOf(pool.copy(contestType = "ESPECIAL"))).cards()
        assertEquals(listOf("megasena", "quina"), special.map { it.resultCode })
        assertEquals("Mega da Virada", special.first().name)
        assertEquals(HomeSpecialBanner.MEGA_THIRTY_YEARS, special.first().specialBanner)
        assertEquals(listOf("quina", "megasena"), parameters(listOf(quina)).cards().map { it.resultCode })
    }

    @Test fun `flag malformed date missing number and expired resale do not create temporary cards`() {
        assertEquals(3074, parameters().cards(false).single().contestNumber)
        listOf(pool.copy(resaleClosingDate = "invalid"), pool.copy(contestNumber = null),
            pool.copy(resaleClosingDate = "20/10/2026")).forEach {
            assertEquals(3074, parameters(pools = listOf(it)).cards().single().contestNumber)
        }
        assertEquals(3073, parameters().cards(instant = Instant.parse("2026-10-21T19:00:00Z")).single().contestNumber)
        assertEquals(3074, parameters().cards(instant = Instant.parse("2026-10-21T19:00:00.001Z")).single().contestNumber)
    }

    @Test fun `resale CTA remains available for closed individual sales and still requires flag and modality`() {
        val card = parameters().cards().single()
        assertFalse(shouldShowPoolButton(false, parameters(), card))
        assertFalse(shouldShowPoolButton(true, parameters(pools = emptyList()), card))
        assertTrue(shouldShowPoolButton(true, parameters(), card.copy(open = false)))
    }
}
