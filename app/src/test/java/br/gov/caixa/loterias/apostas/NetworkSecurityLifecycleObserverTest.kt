package br.gov.caixa.loterias.apostas

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.security.lifecycle.NetworkSecurityLifecycleObserver
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NetworkSecurityLifecycleObserverTest {
    private val owner = object : LifecycleOwner {
        override val lifecycle: Lifecycle get() = error("Observer must not reach into its owner")
    }

    @Test fun `initial start does not duplicate startup and each reentry refreshes`() = runTest {
        var calls = 0
        val observer = NetworkSecurityLifecycleObserver(PrepareNetworkSecurityUseCase { calls++; AppResult.Success(Unit) },
            this, StandardTestDispatcher(testScheduler))
        observer.onStart(owner)
        runCurrent()
        assertEquals(0, calls)
        repeat(2) {
            observer.onStop(owner)
            observer.onStart(owner)
            runCurrent()
        }
        assertEquals(2, calls)
    }

    @Test fun `failed refresh can be retried at next reentry and destroyed scope does not refresh`() = runTest {
        var calls = 0
        val dispatcher = StandardTestDispatcher(testScheduler)
        val lifecycleScope = CoroutineScope(Job() + dispatcher)
        val observer = NetworkSecurityLifecycleObserver(PrepareNetworkSecurityUseCase { calls++; AppResult.Failure(AppError.Network) },
            lifecycleScope, dispatcher)
        repeat(2) {
            observer.onStop(owner)
            observer.onStart(owner)
            runCurrent()
        }
        assertEquals(2, calls)
        lifecycleScope.cancel()
        observer.onStop(owner)
        observer.onStart(owner)
        runCurrent()
        assertEquals(2, calls)
    }
}
