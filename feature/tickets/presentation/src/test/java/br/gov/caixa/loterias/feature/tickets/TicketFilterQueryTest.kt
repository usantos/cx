package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseFilters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TicketFilterQueryTest {
    @Test fun `relative bet periods omit year and status choices use legacy service codes`() {
        for (year in listOf(null, 0, -1)) {
            val query = BetFilters(period = TicketPeriod(RECENT_PERIOD, year, "Últimos 45 dias")).query()
            assertEquals("45", query["mes"])
            assertFalse(query.containsKey("ano"))
        }
        assertEquals("2026", BetFilters(period = TicketPeriod(OCTOBER, HISTORY_YEAR, "Outubro")).query()["ano"])
        for ((selection, serviceCode) in listOf(1L to "1", 2L to "4", EXPIRED_STATUS to "6")) {
            assertEquals(serviceCode, BetFilters(status = selection).query()["situacao"])
        }
    }

    @Test fun `query models omit optional filters and use regular page size`() {
        val betQuery = BetFilters(
            period = TicketPeriod(SEPTEMBER, HISTORY_YEAR, "Setembro"),
            status = 0,
            ordering = 0,
            betType = 0
        ).query()
        assertEquals("30", betQuery["size"])
        assertFalse(betQuery.containsKey("situacao"))
        val purchaseQuery = PurchaseFilters().query(PURCHASE_OFFSET)
        assertEquals(mapOf("offset" to "3", "size" to "10"), purchaseQuery)
    }
}

private const val RECENT_PERIOD = 45
private const val HISTORY_YEAR = 2026
private const val OCTOBER = 10
private const val SEPTEMBER = 9
private const val EXPIRED_STATUS = 3L
private const val PURCHASE_OFFSET = 3
