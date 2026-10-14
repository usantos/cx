package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.domain.betting.BetPrice
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.PoolModalityAvailability
import br.gov.caixa.loterias.core.domain.retail.Retailer
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplaceModalitiesTest {
    @Test
    fun `uses only pool modalities and derives dozen quantities from backend prices`() {
        val parameters = BettingParameters(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            0,
            games = listOf(
                game(2, "Mega-Sena", "MEGA_SENA", 60, listOf(8, 6, 8)),
                game(3, "Quina", "QUINA", 80, listOf(5)),
            ),
            availablePoolModalities = listOf(PoolModalityAvailability(2, "MEGA_SENA", "NORMAL")),
        )

        val result = marketplaceModalities(parameters)

        assertEquals(listOf("mega sena"), result.map { it.name })
        assertEquals(listOf(6, 8), result.last().numberOptions)
        assertEquals(60, result.last().maximumNumber)
        assertEquals(2, result.last().id)
        assertEquals(1, result.last().contestType)
    }

    @Test
    fun `matches modality by code and labels special contests from backend`() {
        val special = game(8, "Lotofácil", "LOTOFACIL", 25, listOf(15)).copy(
            modality = LotteryModality(8, "Lotofácil", "LOTOFACIL", "Lotofácil da Independência"),
            contestType = "ESPECIAL",
        )
        val parameters = BettingParameters(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            0,
            listOf(special),
            listOf(PoolModalityAvailability(0, "LOTOFACIL", "E")),
        )

        val result = marketplaceModalities(parameters).last()

        assertEquals("lotofácil da independência", result.name)
        assertEquals("Lotofácil", result.baseName)
        assertEquals(8, result.id)
        assertEquals(2, result.contestType)
    }

    @Test
    fun `ignores pool entries without matching simulation parameters and removes duplicates`() {
        val mega = game(2, "Mega-Sena", "MEGA_SENA", 60, listOf(6))
        val parameters = BettingParameters(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            0,
            listOf(mega),
            listOf(
                PoolModalityAvailability(2, "MEGA_SENA", "NORMAL"),
                PoolModalityAvailability(2, "MEGA_SENA", "NORMAL"),
                PoolModalityAvailability(99, "UNKNOWN", "NORMAL"),
            ),
        )
        assertEquals(1, marketplaceModalities(parameters).size)
    }

    @Test
    fun `wanted numbers follow backend maximum with legacy fallback`() {
        val mega = MarketplaceFilterModalityUi(2, "Mega-Sena", "Mega-Sena", 1, listOf(6), 60)
        assertEquals((1..60).toList(), wantedNumberOptions(mega))
        assertEquals((1..80).toList(), wantedNumberOptions(null))
        assertTrue(MarketplaceFilterModalityUi(19, "Loteca", "Loteca", 1, listOf(17), 0).hidesWantedNumbers)
        assertTrue(MarketplaceFilterModalityUi(7, "Super Sete", "Super Sete", 1, listOf(7), 0).hidesWantedNumbers)
    }

    @Test
    fun `retailer labels and local name filtering reproduce legacy behavior`() {
        val center = retailer(101, "0001-01", "Lotérica Centro")
        val south = retailer(102, "1230000049", "Lotérica Sul")

        assertEquals("000101 - Lotérica Centro", retailerDisplayLabel(center))
        assertEquals("000004 - Lotérica Sul", retailerDisplayLabel(south))
        assertEquals(listOf(center), filterRetailers("centro", listOf(center, south)))
    }

    private fun game(
        id: Int,
        name: String,
        code: String,
        maximum: Int,
        quantities: List<Int>,
    ) = LotteryGameParameters(
        modality = LotteryModality(id, name, code),
        contestNumber = 1,
        drawDate = "",
        maximumNumber = maximum,
        minimumSelection = quantities.minOrNull() ?: 0,
        maximumSelection = quantities.maxOrNull() ?: 0,
        maximumSurprises = 0,
        recurringOptions = emptyList(),
        prices = quantities.map { BetPrice(it, 0, BigDecimal.ONE) },
    )

    private fun retailer(id: Long, code: String, name: String) =
        Retailer(id, code, name, "", "", "", "", "", false)
}
