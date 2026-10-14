package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionBettingParametersRepositoryTest {
    @Test fun `reuses current parameters before 180 seconds and refreshes exactly at TTL`() = runTest {
        val remote = Fake()
        var now = 0L
        val repository = SessionBettingParametersRepository(remote, { "prd" }, { null }, { now })
        val first = repository.parameters()
        now = 179999
        assertSame(first, repository.parameters())
        assertEquals(1, remote.calls)
        now = 180000
        repository.parameters()
        assertEquals(2, remote.calls)
    }

    @Test fun `login logout owner replacement and namespace changes invalidate previous configuration`() = runTest {
        val remote = Fake()
        var identity: AuthenticatedSessionIdentity? = null
        var namespace = "prd"
        val repository = SessionBettingParametersRepository(remote, { namespace }, { identity }, { 0L })
        repository.parameters()
        identity = AuthenticatedSessionIdentity("a".repeat(64))
        repository.parameters()
        identity = AuthenticatedSessionIdentity("a".repeat(64))
        repository.parameters()
        namespace = "tqs"
        repository.parameters()
        identity = null
        repository.parameters()
        assertEquals(5, remote.calls)
    }

    @Test fun `expired configuration does not conceal backend failure`() = runTest {
        val remote = Fake()
        var now = 0L
        val repository = SessionBettingParametersRepository(remote, { "prd" }, { null }, { now })
        repository.parameters()
        now = 180000
        remote.fail = true
        try { repository.parameters(); error("expected failure") } catch (_: java.io.IOException) { }
        remote.fail = false
        repository.parameters()
        assertEquals(3, remote.calls)
    }

    @Test fun `session replaced during request discards response`() = runTest {
        val remote = Fake()
        var identity: AuthenticatedSessionIdentity? = AuthenticatedSessionIdentity("a".repeat(64))
        val repository = SessionBettingParametersRepository(remote, { "prd" }, { identity }, { 0L })
        remote.afterLoad = { identity = AuthenticatedSessionIdentity("b".repeat(64)) }
        assertTrue(runCatching { repository.parameters() }.exceptionOrNull() is IllegalStateException)
        remote.afterLoad = {}
        repository.parameters()
        assertEquals(2, remote.calls)
    }

    private class Fake : BettingRepository {
        var calls = 0
        var fail = false
        var afterLoad: () -> Unit = {}
        override suspend fun parameters(): BettingParameters {
            calls++
            if (fail) throw java.io.IOException("offline")
            afterLoad()
            return BettingParameters(BigDecimal.TEN, BigDecimal.ONE, calls, emptyList())
        }
        override suspend fun modalities() = emptyList<LotteryModality>()
        override suspend fun combos() = emptyList<BetCombo>()
        override suspend fun addBet(draft: BetDraft): ShoppingCart = error("unused")
        override suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart = error("unused")
    }
}
