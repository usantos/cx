package br.gov.caixa.loterias.core.domain.account

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ResumeLocalSessionCleanupTest {
    @Test fun `maps absent and completed pending cleanup without caching results`() = runTest {
        var pending = false
        var calls = 0
        val useCase = ResumeLocalSessionCleanup { calls++; pending }
        assertEquals(LocalSessionRecovery.NONE_PENDING, useCase())
        pending = true
        assertEquals(LocalSessionRecovery.RECOVERED, useCase())
        assertEquals(2, calls)
    }

    @Test fun `failure and cancellation never release entry as if no cleanup was pending`() = runTest {
        for (error in listOf(IllegalStateException("failed"), CancellationException("cancelled"))) {
            val useCase = ResumeLocalSessionCleanup { throw error }
            try { useCase(); fail("Must propagate") } catch (actual: Exception) { assertSame(error, actual) }
        }
    }
}
