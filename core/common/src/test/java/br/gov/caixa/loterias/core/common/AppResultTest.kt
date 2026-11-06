package br.gov.caixa.loterias.core.common

import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.common.result.fold

class AppResultTest {
    @Test
    fun `maps success and known failures`() = runTest {
        assertEquals(AppResult.Success(7), appResultOf { 7 })
        assertEquals(AppResult.Failure(AppError.Network), appResultOf<Int> { throw IOException() })
        assertEquals(
            AppResult.Failure(AppError.Validation("invalid")),
            appResultOf<Int> { throw IllegalArgumentException("invalid") },
        )
        assertEquals(
            AppResult.Failure(AppError.Validation("")),
            appResultOf<Int> { throw IllegalArgumentException() },
        )
        assertEquals(AppResult.Failure(AppError.Unexpected), appResultOf<Int> { error("boom") })
    }

    @Test
    fun `never consumes coroutine cancellation`() {
        assertThrows(CancellationException::class.java) {
            kotlinx.coroutines.runBlocking { appResultOf<Int> { throw CancellationException() } }
        }
    }

    @Test
    fun `fold selects matching branch`() {
        assertEquals("ok:3", AppResult.Success(3).fold({ "ok:$it" }, { "error" }))
        assertEquals("error", AppResult.Failure(AppError.Network).fold({ "ok" }, { "error" }))
    }

    @Test
    fun `dispatcher holder exposes injected dispatchers`() {
        val dispatchers = FixedCoroutineDispatchers(Dispatchers.Unconfined)
        assertSame(dispatchers.io, dispatchers.computation)
        assertSame(dispatchers.io, dispatchers.main)
        assertSame(Dispatchers.IO, DefaultCoroutineDispatchers.io)
        assertSame(Dispatchers.Default, DefaultCoroutineDispatchers.computation)
        assertSame(Dispatchers.Main, DefaultCoroutineDispatchers.main)
    }
}
