package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.network.config.NetworkSecurityPreparation
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NetworkSecurityPreparationTest {
    @Test fun `resolution and installation finish before the next refresh begins`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val events = mutableListOf<String>()
        var call = 0
        val prepare = NetworkSecurityPreparation {
            val id = ++call
            events += "resolve-$id"
            if (id == 1) gate.await()
            events += "install-$id"
        }
        val first = async { prepare() }
        runCurrent()
        val second = async { prepare() }
        runCurrent()
        assertEquals(listOf("resolve-1"), events)
        gate.complete(Unit)
        assertEquals(AppResult.Success(Unit), first.await())
        assertEquals(AppResult.Success(Unit), second.await())
        assertEquals(listOf("resolve-1", "install-1", "resolve-2", "install-2"), events)
    }

    @Test fun `failure returns typed error and permits retry`() = runTest {
        var failed = true
        val prepare = NetworkSecurityPreparation { if (failed) throw IOException("offline") }
        assertEquals(AppResult.Failure(AppError.Network), prepare())
        failed = false
        assertEquals(AppResult.Success(Unit), prepare())
        assertEquals(AppResult.Failure(AppError.Unexpected), NetworkSecurityPreparation { error("invalid configuration") }())
    }

    @Test fun `cancellation propagates and releases the installation lock`() = runTest {
        var cancel = true
        val prepare = NetworkSecurityPreparation { if (cancel) throw CancellationException("cancelled") }
        try { prepare(); fail("Expected cancellation") } catch (_: CancellationException) { }
        cancel = false
        assertEquals(AppResult.Success(Unit), prepare())
    }
}
