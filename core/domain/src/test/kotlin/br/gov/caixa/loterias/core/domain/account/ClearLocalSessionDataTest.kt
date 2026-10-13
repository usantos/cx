package br.gov.caixa.loterias.core.domain.account

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ClearLocalSessionDataTest {
    @Test fun `delegates every explicit cleanup to the repository`() = runTest {
        var calls = 0
        val useCase = ClearLocalSessionData { calls++ }
        useCase()
        useCase()
        assertEquals(2, calls)
    }

    @Test fun `failure and cancellation never turn into successful cleanup`() = runTest {
        for (error in listOf(IllegalStateException("failed"), CancellationException("cancelled"))) {
            val useCase = ClearLocalSessionData { throw error }
            try { useCase(); fail("Must report incomplete cleanup") }
            catch (actual: Exception) { assertSame(error, actual) }
        }
    }
}
