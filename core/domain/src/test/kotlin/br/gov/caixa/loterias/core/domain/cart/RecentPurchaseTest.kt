package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RecentPurchaseTest {
    private val now = Instant.parse("2026-09-07T12:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)

    @Test fun `checks exact amount with strict 24 hour boundary and device clock rollback`() = runTest {
        val repository = History()
        val check = HasRecentPurchase(repository, clock)
        val amount = BigDecimal("30.50")
        assertEquals(AppResult.Success(false), check(amount))
        repository.date = now.minusSeconds(86_401)
        assertEquals(AppResult.Success(false), check(amount))
        repository.date = now.minusSeconds(86_400)
        assertEquals(AppResult.Success(false), check(amount))
        repository.date = now.minusSeconds(86_399)
        assertEquals(AppResult.Success(true), check(amount))
        repository.date = now.plusSeconds(60)
        assertEquals(AppResult.Success(true), check(amount))
        assertEquals(amount, repository.amount)
    }

    @Test fun `records injected time and rejects nonpositive amounts before storage`() = runTest {
        val repository = History()
        val record = RecordRecentPurchase(repository, clock)
        assertEquals(AppResult.Success(Unit), record(BigDecimal("15.00")))
        assertEquals(now, repository.date)
        assertEquals(BigDecimal("15.00"), repository.amount)
        for (amount in listOf(BigDecimal.ZERO, BigDecimal("-1"))) {
            assertTrue(record(amount) is AppResult.Failure)
            assertTrue(HasRecentPurchase(repository, clock)(amount) is AppResult.Failure)
        }
        assertEquals(BigDecimal("15.00"), repository.amount)
    }

    @Test fun `storage errors do not masquerade as an empty history or successful recording`() = runTest {
        val repository = History().apply { failure = IllegalStateException("storage") }
        assertEquals(AppResult.Failure(AppError.Unexpected), HasRecentPurchase(repository, clock)(BigDecimal.ONE))
        assertEquals(AppResult.Failure(AppError.Unexpected), RecordRecentPurchase(repository, clock)(BigDecimal.ONE))
        repository.failure = CancellationException("cancelled")
        for (operation in listOf<suspend () -> Any>(
            { HasRecentPurchase(repository, clock)(BigDecimal.ONE) },
            { RecordRecentPurchase(repository, clock)(BigDecimal.ONE) },
        )) {
            try { operation(); fail("Cancellation was swallowed") } catch (_: CancellationException) { }
        }
    }

    private class History : RecentPurchaseRepository {
        var date: Instant? = null
        var amount: BigDecimal? = null
        var failure: Exception? = null
        override suspend fun latestPurchaseAt(amount: BigDecimal): Instant? {
            failure?.let { throw it }
            this.amount = amount
            return date
        }
        override suspend fun record(amount: BigDecimal, purchasedAt: Instant) {
            failure?.let { throw it }
            this.amount = amount
            date = purchasedAt
        }
    }
}
