package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.toDomain
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.filterRetailers
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.marketplaceModalities
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.retailerDisplayLabel
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.wantedNumberOptions

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
    fun `special Mega Sena names and blank fallback take precedence over the normal label`() {
        listOf("Mega da Virada" to "mega da virada", "" to "mega-sena").forEach { (specialName, expected) ->
            val special = game(2, "Mega-Sena", "MEGA_SENA", 60, listOf(6)).copy(
                modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA", specialName),
                contestType = "ESPECIAL",
            )
            val parameters = BettingParameters(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                listOf(special),
                listOf(PoolModalityAvailability(2, "MEGA_SENA", "ESPECIAL")),
            )

            val result = marketplaceModalities(parameters).single()

            assertEquals(expected, result.name)
            assertEquals("Mega-Sena", result.baseName)
            assertEquals(2, result.contestType)
        }
    }

    @Test
    fun `special cards use legacy line breaks without changing submitted modality name`() {
        val names = listOf(
            "quina de são joão" to "quina de\nsão joão",
            "lotofácil da independência" to "lotofácil da\nindependência",
            "mega da virada" to "mega da\nvirada",
            "dupla de páscoa" to "dupla de\npáscoa",
            "loteca especial" to "loteca\nespecial",
        )
        names.forEach { (name, label) ->
            val modality = MarketplaceFilterModalityUi(2, name, "Mega-Sena", 2, listOf(6), 60)
            assertEquals(label, modality.cardLabel)
            assertEquals(name, MarketplaceFilterUiState(selectedModality = modality).toDomain().modality)
        }
    }

    @Test
    fun `normal and parameterized contest card labels keep their backend text`() {
        val normal = MarketplaceFilterModalityUi(2, "mega sena", "Mega-Sena", 1, listOf(6), 60)
        val configured = normal.copy(name = "mega\n30 anos", contestType = 2)
        assertEquals("mega sena", normal.cardLabel)
        assertEquals("mega\n30 anos", configured.cardLabel)
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

    @Test
    fun `available modalities without simulation games retain native fallback ranges`() {
        val cases = listOf(
            Triple(2, "MEGA_SENA", (6..20).toList() to 60),
            Triple(8, "LOTOFACIL", (15..20).toList() to 25),
            Triple(3, "QUINA", (5..15).toList() to 80),
            Triple(9, "MAIS_MILIONARIA", (6..12).toList() to 50),
            Triple(18, "DUPLA_SENA", (6..15).toList() to 50),
            Triple(11, "DIA_DE_SORTE", (7..15).toList() to 31),
            Triple(7, "SUPER_7", (7..21).toList() to 80),
            Triple(20, "TIMEMANIA", listOf(10) to 80),
            Triple(19, "LOTECA", (17..26).toList() to 80),
            Triple(16, "LOTOMANIA", (5..25).toList() to 80),
            Triple(14, "LOTOGOL", (5..25).toList() to 80),
            Triple(1502, "INSTANTANEA", (5..25).toList() to 80),
        )
        cases.forEach { (id, code, expected) ->
            val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
                games = emptyList(), availablePoolModalities = listOf(PoolModalityAvailability(id, code, "NORMAL")))
            val result = marketplaceModalities(parameters)
            assertEquals("Available $code must remain selectable", 1, result.size)
            assertEquals(id, result.single().id)
            assertEquals(expected.first, result.single().numberOptions)
            assertEquals(expected.second, result.single().maximumNumber)
        }
    }

    @Test
    fun `available modality fallback resolves official code when numeric id is absent`() {
        val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
            games = emptyList(), availablePoolModalities = listOf(PoolModalityAvailability(0, "SUPER_7", "NORMAL")))
        val result = marketplaceModalities(parameters)
        assertEquals(1, result.size)
        assertEquals(7, result.single().id)
        assertEquals((7..21).toList(), result.single().numberOptions)
        assertTrue(result.single().hidesWantedNumbers)
    }

    @Test
    fun `special available modality without simulation game retains contest identity`() {
        val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
            games = emptyList(), availablePoolModalities = listOf(PoolModalityAvailability(2, "MEGA_SENA", "ESPECIAL")))
        val result = marketplaceModalities(parameters)
        assertEquals(1, result.size)
        assertEquals(2, result.single().contestType)
        assertEquals((6..20).toList(), result.single().numberOptions)
    }

    @Test
    fun `numeric identity hides wanted filters despite backend display names`() {
        assertTrue(MarketplaceFilterModalityUi(7, "Super 7", "Super 7", 1, listOf(7), 80).hidesWantedNumbers)
        assertTrue(MarketplaceFilterModalityUi(19, "Loteca dos Pais", "LOTECA", 2, listOf(17), 80).hidesWantedNumbers)
    }

    @Test
    fun `temporary special labels use available contest number instead of simulation game`() {
        val configured = MarketplaceSpecialContests(megaThirtyYearsContest = 3000, lotecaFathersDayContest = 1234)
        val cases = listOf(
            Triple(game(2, "Mega-Sena", "MEGA_SENA", 60, listOf(6)), 3000, "mega-sena\n30 anos"),
            Triple(game(19, "Loteca", "LOTECA", 14, listOf(17)), 1234, "loteca\ndos pais"),
        )
        cases.forEach { (simulation, availableNumber, expected) ->
            val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
                games = listOf(simulation.copy(contestNumber = 9999)),
                availablePoolModalities = listOf(PoolModalityAvailability(
                    simulation.modality.id, simulation.modality.code, "ESPECIAL", contestNumber = availableNumber)))
            val result = marketplaceModalities(parameters, configured).single()
            assertEquals(expected, result.name)
            assertEquals(expected, result.cardLabel)
            assertEquals(simulation.modality.name, result.baseName)
            assertEquals(2, result.contestType)
        }
    }

    @Test
    fun `simulation contest match cannot override unmatched or normal available contest`() {
        val configured = MarketplaceSpecialContests(megaThirtyYearsContest = 3000, lotecaFathersDayContest = 1234)
        listOf(Triple(2, "MEGA_SENA", 3000), Triple(19, "LOTECA", 1234)).forEach { (id, code, gameNumber) ->
            val simulation = game(id, "Backend normal", code, 60, listOf(6)).copy(
                contestNumber = gameNumber,
                modality = LotteryModality(id, "Backend normal", code, "Backend especial"))
            listOf("NORMAL", "ESPECIAL").forEach { type ->
                val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
                    games = listOf(simulation), availablePoolModalities = listOf(
                        PoolModalityAvailability(id, code, type, contestNumber = if (type == "NORMAL") gameNumber else 9999)))
                val expected = if (type == "ESPECIAL") "backend especial" else if (id == 2) "mega sena" else "backend normal"
                assertEquals(expected, marketplaceModalities(parameters, configured).single().name)
            }
        }
    }

    @Test
    fun `temporary special fallback labels work without simulation games`() {
        val configured = MarketplaceSpecialContests(megaThirtyYearsContest = 3000, lotecaFathersDayContest = 1234)
        val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
            games = emptyList(), availablePoolModalities = listOf(
                PoolModalityAvailability(2, "MEGA_SENA", "ESPECIAL", contestNumber = 3000),
                PoolModalityAvailability(19, "LOTECA", "E", contestNumber = 1234)))
        assertEquals(listOf("mega-sena\n30 anos", "loteca\ndos pais"),
            marketplaceModalities(parameters, configured).map { it.cardLabel })
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
