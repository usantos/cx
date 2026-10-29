package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.data.dto.LotteryGameParameterDto
import com.google.gson.Gson
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class BettingMinimumAmountMappingTest {
    @Test fun `sports minimum amount survives native parameter decoding separately from price rows`() {
        val json = """{
            "concurso":{"modalidade":"LOTECA","numero":1200,"tipoConcurso":"NORMAL"},
            "valorApostaMinima":3.00,
            "valoresAposta":[{"numeroPrognosticos":14,"valor":24.00}]
        }"""
        val dto = Gson().fromJson(json, LotteryGameParameterDto::class.java)
        val game = dto.toBettingGame(emptyMap())
        assertEquals(BigDecimal("3.00"), game.minimumBetAmount)
        assertEquals(BigDecimal("24.00"), game.prices.single().amount)
    }
}
