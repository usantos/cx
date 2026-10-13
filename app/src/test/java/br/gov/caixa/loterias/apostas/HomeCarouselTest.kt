package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.PoolModalityAvailability
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class HomeCarouselTest {
    @Test
    fun `initial page is centered and aligned to the data cycle`() {
        val initial = infiniteCarouselInitialPage(4)
        assertEquals(0, initial % 4)
        assertEquals(0, loopedCarouselIndex(initial, 4))
        assertEquals(3, loopedCarouselIndex(initial - 1, 4))
        assertEquals(1, loopedCarouselIndex(initial + 1, 4))
    }

    @Test
    fun `focused item is full height and adjacent item is shorter`() {
        assertEquals(1f, carouselCardScale(0f))
        assertEquals(0.9f, carouselCardScale(0.5f))
        assertEquals(0.8f, carouselCardScale(1f))
        assertEquals(0.8f, carouselCardScale(4f))
    }

    @Test
    fun `empty carousel is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { infiniteCarouselInitialPage(0) }
        assertThrows(IllegalArgumentException::class.java) { loopedCarouselIndex(0, 0) }
    }

    @Test
    fun `carousel retains instant ticket and adds combo only when backend returns combos`() {
        val parameters = BettingParameters(
            dailyLimit = BigDecimal.TEN,
            minimumCartAmount = BigDecimal.TEN,
            maximumCartBets = 10,
            games = listOf(
                game(2, "Mega-Sena", "MEGA_SENA"),
                game(1502, "Instantânea", "INSTANTANEA"),
                game(999, "Desconhecida", "UNKNOWN"),
            ),
        )

        val withoutCombos = parameters.toHomeLotteries(hasCombos = false)
        assertEquals(listOf(HomeLotteryKind.LOTTERY, HomeLotteryKind.INSTANT), withoutCombos.map(HomeLottery::kind))
        assertEquals(CaixaDSColorTokens.LegacyMegaSenaLight, withoutCombos.first().color)
        assertEquals(false, withoutCombos.first().octoberRoseTheme)

        val withCombos = parameters.toHomeLotteries(hasCombos = true)
        assertEquals(
            listOf(HomeLotteryKind.LOTTERY, HomeLotteryKind.INSTANT, HomeLotteryKind.COMBO),
            withCombos.map(HomeLottery::kind),
        )
        assertEquals("Combo", withCombos.last().name)
    }

    @Test
    fun `carousel does not duplicate combo already supplied by simulation parameters`() {
        val parameters = BettingParameters(
            dailyLimit = BigDecimal.TEN,
            minimumCartAmount = BigDecimal.TEN,
            maximumCartBets = 10,
            games = listOf(game(0, "Combo", "COMBO")),
        )

        assertEquals(1, parameters.toHomeLotteries(hasCombos = true).size)
        assertEquals(HomeLotteryKind.COMBO, parameters.toHomeLotteries(hasCombos = true).single().kind)
    }

    @Test
    fun `pool button follows backend flag modality availability and card state`() {
        val available = parametersWithPoolAvailability()
        val mega = available.toHomeLotteries(hasCombos = false).single()

        assertEquals(true, shouldShowPoolButton(true, available, mega))
        assertEquals(false, shouldShowPoolButton(false, available, mega))
        assertEquals(
            false,
            shouldShowPoolButton(true, available.copy(availablePoolModalities = emptyList()), mega),
        )
        assertEquals(false, shouldShowPoolButton(true, available, mega.copy(open = false)))
        assertEquals(false, shouldShowPoolButton(true, available, mega.copy(kind = HomeLotteryKind.INSTANT)))
    }

    @Test
    fun `pool availability also matches normalized modality code`() {
        val parameters = parametersWithPoolAvailability().copy(
            availablePoolModalities = listOf(PoolModalityAvailability(0, "MEGA_SENA", "NORMAL")),
        )
        assertEquals(
            true,
            shouldShowPoolButton(true, parameters, parameters.toHomeLotteries(false).single()),
        )
    }

    private fun parametersWithPoolAvailability() = BettingParameters(
        dailyLimit = BigDecimal.TEN,
        minimumCartAmount = BigDecimal.TEN,
        maximumCartBets = 10,
        games = listOf(game(2, "Mega-Sena", "MEGA_SENA")),
        availablePoolModalities = listOf(PoolModalityAvailability(2, "MEGA_SENA", "NORMAL")),
    )

    private fun game(id: Int, name: String, code: String) = LotteryGameParameters(
        modality = LotteryModality(id, name, code),
        contestNumber = 1,
        drawDate = "",
        maximumNumber = 60,
        minimumSelection = 6,
        maximumSelection = 20,
        maximumSurprises = 15,
        recurringOptions = emptyList(),
        prices = emptyList(),
    )
}
