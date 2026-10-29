package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.shouldShowPoolButton
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.mapper.withHomeMegaSenaCampaign
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class HomeLotteryMapperTest {
    @Test fun `mapping retains backend contest dates prices and special name`() {
        val game = game(2, "MEGA_SENA").copy(
            modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA", specialName = "Mega da Virada"),
            contestType = "especial", contestNumber = 4321, drawDate = "31/12/2026",
            closingDateTime = "2026-12-31T18:00:00", open = false,
            estimatedPrize = BigDecimal("10000000.00"),
            prices = listOf(BetPrice(7, 0, BigDecimal("35.00")), BetPrice(6, 0, BigDecimal("5.00"))),
        )
        val mapped = parameters(game).toHomeLotteries(false).single()
        assertEquals("Mega da Virada", mapped.name)
        assertEquals("especial", mapped.contestType)
        assertEquals(4321, mapped.contestNumber)
        assertEquals("31/12/2026", mapped.drawDate)
        assertEquals(game.closingDateTime, mapped.closingDateTime)
        assertEquals(BigDecimal("5.00"), mapped.minimumBetAmount)
        assertEquals(game.estimatedPrize, mapped.estimatedPrize)
        assertFalse(mapped.open)
        assertFalse(mapped.octoberRoseTheme)
    }

    @Test fun `normalization retains ordering result codes and filters unknown modalities`() {
        val mapped = parameters(game(2, ""), game(3, "DUPLA_SENA"), game(4, "DIA_DE_SORTE"),
            game(5, "SUPER_7"), game(6, "MAIS_MILIONARIA"), game(7, "_QUÍNA_"), game(999, "UNKNOWN"))
            .toHomeLotteries(false)
        assertEquals(listOf("megasena", "duplasena", "diadesorte", "supersete", "maismilionaria", "quina"),
            mapped.map { it.resultCode })
    }

    @Test fun `null parameters retain the existing synthetic combo behavior`() {
        val missing: BettingParameters? = null
        assertTrue(missing.toHomeLotteries(false).isEmpty())
        val combo = missing.toHomeLotteries(true).single()
        assertEquals(HomeLotteryKind.COMBO, combo.kind)
        assertEquals(0, combo.modalityId)
        assertEquals("", combo.resultCode)
        assertEquals(BigDecimal.ZERO, combo.minimumBetAmount)
        assertTrue(combo.open)
    }

    @Test fun `mapping leaves backend data untouched and uses zero for missing prices`() {
        val source = parameters(game(2, "MEGA_SENA"))
        val mapped = source.toHomeLotteries(false).single()
        assertEquals(BigDecimal.ZERO, mapped.minimumBetAmount)
        assertEquals(source, parameters(game(2, "MEGA_SENA")))
        assertEquals("Mega-Sena", mapped.name)
    }

    @Test fun `pool availability keeps the existing distinction between SUPER7 and supersete aliases`() {
        val source = parameters(game(5, "SUPER_7")).copy(
            availablePoolModalities = listOf(PoolModalityAvailability(0, "SUPER_7", "NORMAL")))
        val mapped = source.toHomeLotteries(false).single()
        assertEquals("supersete", mapped.resultCode)
        assertFalse(shouldShowPoolButton(true, source, mapped))
        assertTrue(shouldShowPoolButton(true, source.copy(availablePoolModalities =
            listOf(PoolModalityAvailability(5, "SUPER_7", "NORMAL"))), mapped))
    }

    @Test fun `default campaign preserves each lottery presentation`() {
        val original = parameters(game(2, "MEGA_SENA"), game(3, "QUINA")).toHomeLotteries(false)
        val mapped = original.withHomeMegaSenaCampaign(CaixaDSCampaign.DEFAULT)
        assertEquals(original, mapped)
        original.zip(mapped).forEach { (before, after) -> assertSame(before, after) }
    }

    @Test fun `mega campaign changes only its presentation and preserves backend contest data`() {
        val original = parameters(game(2, "MEGA_SENA"), game(3, "QUINA")).toHomeLotteries(false)
        val mapped = original.withHomeMegaSenaCampaign(CaixaDSCampaign.OCTOBER_ROSE)
        val style = resolveCaixaDSLotteryStyle(2, "", CaixaDSCampaign.OCTOBER_ROSE)
        assertEquals(style.light, mapped.first().color)
        assertEquals(style.dark, mapped.first().darkColor)
        assertEquals(style.content, mapped.first().headerContentColor)
        assertEquals(style.listText, mapped.first().listText)
        assertEquals(style.darkContent, mapped.first().darkContent)
        assertTrue(mapped.first().octoberRoseTheme)
        assertEquals(original.first().contestNumber, mapped.first().contestNumber)
        assertEquals(original.first().minimumBetAmount, mapped.first().minimumBetAmount)
        assertEquals(original.first().name, mapped.first().name)
        assertSame(original.last(), mapped.last())
    }

    private fun parameters(vararg games: LotteryGameParameters) =
        BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 30, games.toList())

    private fun game(id: Int, code: String) = LotteryGameParameters(
        LotteryModality(id, "Mega-Sena", code), 1234, "17/10/2026", 60, 6, 20, 10,
        emptyList(), emptyList(),
    )
}
