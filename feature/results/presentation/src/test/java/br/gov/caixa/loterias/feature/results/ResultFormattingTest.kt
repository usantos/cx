package br.gov.caixa.loterias.feature.results

import br.gov.caixa.loterias.feature.results.presentation.mapper.resultMoney
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultWinners
import org.junit.Assert.assertEquals
import org.junit.Test

class ResultFormattingTest {
    @Test fun `formats the real PRD payout amounts without losing cents`() {
        assertEquals("R$ 34.793,09", resultMoney("34793.09"))
        assertEquals("R$ 47.148.780,00", resultMoney("47148780.00"))
        assertEquals("indisponível", resultMoney("indisponível"))
    }

    @Test fun `winner labels distinguish zero singular and plural`() {
        assertEquals("Sem ganhadores", resultWinners(0))
        assertEquals("1 aposta ganhadora", resultWinners(1))
        assertEquals("54 apostas ganhadoras", resultWinners(54))
    }

    @Test fun `maps backend prize codes for each supported modality`() {
        val expected = mapOf(
            "megasena" to "Sena - 6 números acertados", "quina" to "Quina - 5 números acertados",
            "duplasena" to "Sena - 6 números acertados", "lotofacil" to "15 números acertados",
            "lotomania" to "20 números acertados", "diadesorte" to "7 números acertados",
            "timemania" to "7 números acertados", "supersete" to "7 números acertados",
            "loteca" to "1º (14 jogos acertados)", "maismilionaria" to "6 números +2 trevos acertados"
        )
        expected.forEach { (code, description) -> assertEquals(description, resultPayoutDescription(code, "0001")) }
        assertEquals("Terno - 3 números acertados", resultPayoutDescription("duplasena", "0008"))
        assertEquals("2 números e 1 trevo acertados", resultPayoutDescription("maismilionaria", "0010"))
        assertEquals("0 acertos", resultPayoutDescription("lotomania", "0007"))
    }

    @Test fun `retains server descriptions and unrecognized codes`() {
        assertEquals("Faixa especial", resultPayoutDescription("megasena", "Faixa especial"))
        assertEquals("9999", resultPayoutDescription("megasena", "9999"))
        assertEquals("0001", resultPayoutDescription("nova", "0001"))
    }
}
