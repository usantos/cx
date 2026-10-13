package br.gov.caixa.loterias.core.preferences

import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class LegacyRecentPurchaseRepositoryTest {
    private val zone = ZoneId.of("America/Sao_Paulo")

    @Test fun `reads old database date format and normalizes decimal scale`() = runTest {
        val store = MemoryStore()
        store.rows["30.5"] = listOf("06/09/2026 09:00", "07/09/2026 08:00")
        val repository = LegacyRecentPurchaseRepository(store) { zone }
        assertEquals(Instant.parse("2026-09-07T11:00:00Z"), repository.latestPurchaseAt(BigDecimal("30.500")))
        assertNull(repository.latestPurchaseAt(BigDecimal("30.51")))
    }

    @Test fun `ignores malformed dates without discarding valid records`() = runTest {
        val store = MemoryStore()
        val repository = LegacyRecentPurchaseRepository(store) { zone }
        store.rows["1"] = listOf("", "31/02/2026 09:00", "not a date")
        assertNull(repository.latestPurchaseAt(BigDecimal.ONE))
        store.rows["1"] = store.rows.getValue("1") + "07/09/2026 09:00"
        assertEquals(Instant.parse("2026-09-07T12:00:00Z"), repository.latestPurchaseAt(BigDecimal.ONE))
    }

    @Test fun `persists at legacy minute resolution across repository recreation without deleting other amounts`() = runTest {
        val store = MemoryStore()
        val repository = LegacyRecentPurchaseRepository(store) { zone }
        repository.record(BigDecimal("15.00"), Instant.parse("2026-09-06T12:00:00Z"))
        repository.record(BigDecimal("20"), Instant.parse("2026-09-07T11:00:00Z"))
        repository.record(BigDecimal("15"), Instant.parse("2026-09-07T12:05:59Z"))
        val recreated = LegacyRecentPurchaseRepository(store) { zone }
        assertEquals(listOf("07/09/2026 09:05"), store.rows["15"])
        assertEquals(Instant.parse("2026-09-07T12:05:00Z"), recreated.latestPurchaseAt(BigDecimal("15.0")))
        assertEquals(Instant.parse("2026-09-07T11:00:00Z"), recreated.latestPurchaseAt(BigDecimal("20.00")))
    }

    private class MemoryStore : RecentPurchaseStore {
        val rows = mutableMapOf<String, List<String>>()
        override fun datesForAmount(amount: String) = rows[amount].orEmpty()
        override fun replaceForAmount(amount: String, date: String) { rows[amount] = listOf(date) }
    }
}
