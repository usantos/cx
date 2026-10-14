package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.domain.betting.*
import java.math.BigDecimal
import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class LotecaCompletionTest {
    private val single = setOf(LotecaOutcome.AWAY)
    private val double = setOf(LotecaOutcome.HOME, LotecaOutcome.DRAW)
    private val triple = LotecaOutcome.entries.toSet()
    private fun price(doubles: Int, triples: Int = 0) = BetPrice(14 + doubles + 2 * triples, 0, BigDecimal(4L * (doubles + 2 * triples)), doubles, triples)
    private fun state(target: Int = 15, old: Map<Int, Set<LotecaOutcome>> = emptyMap(), prices: List<BetPrice> = listOf(price(1), price(2), price(0,1))) : BettingGameUiState {
        val team = SportsTeam(1,"BAHIA","BA")
        return BettingGameUiState(loading = false, numberQuantity = target, lotecaOutcomes = old,
            game = LotteryGameParameters(LotteryModality(LOTECA_ID,"Loteca","LOTECA"),1275,"18/10/2026",0,15,26,1,listOf(0),prices,
                matches = (1..14).map { SportsMatch(it,team,team.copy(number=2)) }))
    }
    private fun assertValid(result: BettingGameUiState, target: Int) {
        assertEquals(target,result.numberQuantity)
        assertEquals(14,result.lotecaOutcomes.size)
        assertEquals(target,result.lotecaOutcomes.values.sumOf { it.size })
        assertTrue(result.canAddToCart)
    }

    @Test fun `footer completes chosen nonminimum target from service combination counts`() {
        val result = state(16).withShake(false,Random(7))
        assertValid(result,16)
        assertEquals(BigDecimal("8"),result.unitPrice)
    }
    @Test fun `preserving strategy keeps selected rows unchanged rather than upgrading singles`() {
        val old = (1..13).associateWith { single }
        val result = state(old=old).withShake(false,Random(19))
        assertValid(result,15)
        old.forEach { (number, selection) -> assertEquals(selection,result.lotecaOutcomes[number]) }
        assertEquals(2,result.lotecaOutcomes[14]?.size)
    }
    @Test fun `relaxes singles at same target before increasing and keeps double row`() {
        val old = (1..14).associateWith { single } + (1 to double)
        val result = state(16,old,listOf(price(2),price(3))).withShake(false,Random(3))
        assertValid(result,16)
        assertEquals(double,result.lotecaOutcomes[1])
        assertNull(result.warningMessage)
    }
    @Test fun `relaxes doubles before general strategy keeping the triple`() {
        val result = state(18,mapOf(1 to triple,2 to double),listOf(price(0,2),price(3,1))).withShake(false,Random(3))
        assertValid(result,18)
        assertEquals(triple,result.lotecaOutcomes[1])
        assertEquals(2,result.lotecaOutcomes.values.count { it.size == 3 })
    }
    @Test fun `general strategy can replace impossible triple at current target`() {
        val result = state(15,mapOf(1 to triple)).withShake(false,Random(2))
        assertValid(result,15)
        assertEquals(0,result.lotecaOutcomes.values.count { it.size == 3 })
        assertNull(result.warningMessage)
    }
    @Test fun `sparse prices increase to next feasible target and announce amount change`() {
        val result = state(16,prices=listOf(price(1),price(4))).withShake(false,Random(1))
        assertValid(result,18)
        assertEquals("Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta.",result.warningMessage)
    }
    @Test fun `impossible invalid and over maximum price configurations terminate without mutation`() {
        val input = state(26,mapOf(1 to single),listOf(price(1),price(13),price(15),price(-1)))
        assertEquals(input,input.withShake(false,Random(4)))
        val empty = input.copy(game=input.game!!.copy(matches=emptyList()))
        assertEquals(empty,empty.withShake(false,Random(4)))
    }
    @Test fun `native Loteca shake renewal preserves completed rows`() {
        val complete = state().withShake(false,Random(1))
        assertValid(complete,15)
        assertEquals(complete,complete.withShake(true,Random(2)))
    }
    @Test fun `completion target twenty six is inclusive`() {
        assertValid(state(26,prices=listOf(price(12))).withShake(false,Random(4)),26)
    }
}
