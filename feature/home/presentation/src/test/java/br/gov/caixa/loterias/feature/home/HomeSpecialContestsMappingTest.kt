package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.feature.betting.domain.model.*
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class HomeSpecialContestsMappingTest {
    private val mega = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
        3000, "", 60, 6, 20, 10, emptyList(), emptyList(), contestType = "ESPECIAL")
    private val loteca = mega.copy(modality = LotteryModality(9, "Loteca", "LOTECA"), contestNumber = 1234)
    private fun lotteries(games: List<LotteryGameParameters>, config: HomeSpecialContests = HomeSpecialContests()) =
        BettingParameters(BigDecimal.TEN, BigDecimal.TEN, 10, games).toHomeLotteries(false, config)

    @Test fun `configured contest number and exact modality and special type are all required`() {
        val config = HomeSpecialContests(3000, 1234)
        val matches = lotteries(listOf(mega, loteca), config)
        assertEquals(HomeSpecialBanner.MEGA_THIRTY_YEARS, matches[0].specialBanner)
        assertEquals(HomeSpecialBanner.LOTECA_FATHERS_DAY, matches[1].specialBanner)
        assertTrue(lotteries(listOf(mega, loteca)).all { it.specialBanner == null })
        assertTrue(lotteries(listOf(mega.copy(contestNumber = 3001), loteca.copy(contestNumber = 1235)), config)
            .all { it.specialBanner == null })
        assertTrue(lotteries(listOf(mega.copy(contestType = "NORMAL"), loteca.copy(contestType = "NORMAL")), config)
            .all { it.specialBanner == null })
        assertNull(lotteries(listOf(mega.copy(modality = LotteryModality(3, "Quina", "QUINA"))), config).single().specialBanner)
    }
    @Test fun `normal headers use legacy enum names and special headers use backend description`() {
        val expected = mapOf("MEGA_SENA" to "mega-sena", "DUPLA_SENA" to "dupla sena",
            "DIA_DE_SORTE" to "dia de sorte", "SUPER_7" to "super sete", "LOTOFACIL" to "lotofácil",
            "MAIS_MILIONARIA" to "+milionária", "INSTANTANEA" to "instantânea", "COMBO" to "Combos de Apostas")
        expected.forEach { (code, title) ->
            val game = mega.copy(contestType = "NORMAL", modality = LotteryModality(2, "Nome diferente no backend", code))
            assertEquals(title, br.gov.caixa.loterias.feature.home.presentation.mapper.homeClassicTitle(lotteries(listOf(game)).single()))
        }
        val special = mega.copy(modality = mega.modality.copy(specialName = "Mega da Virada"))
        assertEquals("mega da virada", br.gov.caixa.loterias.feature.home.presentation.mapper.homeClassicTitle(lotteries(listOf(special)).single()))
    }

    @Test fun `bright modalities use legacy dark prize text and independent dark header content`() {
        val codes = listOf("TIMEMANIA", "DIA_DE_SORTE", "SUPER_7")
        val cards = lotteries(codes.map { mega.copy(contestType = "NORMAL", modality = LotteryModality(2, it, it)) })
        val colors = br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
        assertEquals(listOf(colors.TimemaniaContent, colors.DiaDeSorteContent, colors.SuperSeteContent), cards.map { it.listText })
        assertEquals(listOf(colors.TimemaniaContent, colors.White, colors.White), cards.map { it.darkContent })
        val lotogol = lotteries(listOf(mega.copy(contestType = "NORMAL", modality = LotteryModality(13, "Lotogol", "LOTOGOL")))).single()
        assertEquals(colors.MegaSenaLight, lotogol.color)
        assertEquals(colors.MegaSenaDark, lotogol.darkColor)
    }

}
