package br.gov.caixa.loterias.core.data

import com.google.gson.JsonParser
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckedTicketResultMapperTest {
    // Contract fixture transcribed from legacy DTO fields, not a captured production response.
    @Test fun `query without a bet id preserves status net award and contest rows`() {
        val resource = requireNotNull(javaClass.getResource("/tickets/legacy-ticket-result-contract.json"))
        val payload = JsonParser.parseString(resource.readText())
        assertTrue(payload.toBets().isEmpty())
        val result = payload.toCheckedTicketResult()
        assertEquals(103, result.statusId)
        assertEquals(2, result.modalityId)
        assertEquals(BigDecimal("123.45"), result.netPrize)
        assertTrue(result.bets.isEmpty())
        assertEquals(3, result.awards.size)
        assertEquals(2800, result.awards[0].contest)
        assertEquals("Quadra", result.awards[0].name)
        assertEquals(4, result.awards[0].hitQuantity)
        assertEquals(1, result.awards[0].draw)
        assertEquals(BigDecimal("12.50"), result.awards[0].value)
        assertEquals(BigDecimal("110.95"), result.awards[1].value)
        assertEquals(2801, result.awards[2].contest)
        assertEquals("Não apurado", result.awards[2].status)
        assertNull(result.awards[2].value)
        assertNull(result.awards[2].hitQuantity)
    }

    @Test fun `non awarded query keeps explicit zero prize and no invented awards`() {
        val result = JsonParser.parseString("""{"situacao":{"valor":9},"valorPremio":0,"modalidade":{"valor":1,"descricao":"Quina"}}""").toCheckedTicketResult()
        assertEquals(9, result.statusId)
        assertEquals(BigDecimal.ZERO, result.netPrize)
        assertTrue(result.awards.isEmpty())
    }
}
