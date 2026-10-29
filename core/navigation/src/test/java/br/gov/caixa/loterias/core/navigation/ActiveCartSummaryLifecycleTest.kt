// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.navigation.state.activeCartSummary
import br.gov.caixa.loterias.core.navigation.state.activeResumingCartSummary
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ActiveCartSummaryLifecycleTest {
    @get:Rule val compose = createComposeRule()
    private class Owner : LifecycleOwner {
        override val lifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.STARTED }
    }
    private fun cart(id: Long) = ShoppingCart(id, BigDecimal("6.00"), emptyList())

    @Test fun `refresh replaces destination snapshot and failed refresh clears stale cart`() {
        val expected = AtomicReference<AppResult<ShoppingCart>>(AppResult.Success(cart(1)))
        val calls = AtomicInteger()
        val source = GetCurrentCartUseCase {
            calls.incrementAndGet()
            expected.get()
        }
        val revision = mutableStateOf(0)
        val observed = AtomicReference<ShoppingCart?>()
        compose.setContent { observed.set(activeCartSummary(source, revision.value)) }
        compose.waitUntil(5000) {
            compose.runOnIdle { Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            observed.get()?.id ==
                1L
        }
        expected.set(AppResult.Failure(AppError.Network))
        compose.runOnIdle {
            revision.value++
            Snapshot.sendApplyNotifications()
        }
        compose.waitUntil(5000) {
            compose.runOnIdle { Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            calls.get() >=
                2 &&
                observed.get() == null
        }
        expected.set(AppResult.Success(cart(3)))
        compose.runOnIdle {
            revision.value++
            Snapshot.sendApplyNotifications()
        }
        compose.waitUntil(5000) {
            compose.runOnIdle { Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            observed.get()?.id ==
                3L
        }
        assertEquals(3, calls.get())
    }

    @Test fun `resuming destination queries only after resume and rechecks after returning`() {
        val lifecycle = Owner()
        val calls = AtomicInteger()
        val observed = AtomicReference<ShoppingCart?>()
        val source = GetCurrentCartUseCase { AppResult.Success(cart(calls.incrementAndGet().toLong())) }
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycle) { observed.set(activeResumingCartSummary(source)) }
        }
        compose.runOnIdle {
            assertNull(observed.get())
            assertEquals(0, calls.get())
            lifecycle.lifecycle.currentState =
                Lifecycle.State.RESUMED
            Snapshot.sendApplyNotifications()
        }
        compose.waitUntil(5000) {
            compose.runOnIdle { Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            observed.get()?.id ==
                1L
        }
        compose.runOnIdle {
            lifecycle.lifecycle.currentState = Lifecycle.State.STARTED
            lifecycle.lifecycle.currentState =
                Lifecycle.State.RESUMED
            Snapshot.sendApplyNotifications()
        }
        compose.waitUntil(5000) {
            compose.runOnIdle { Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            observed.get()?.id ==
                2L
        }
        assertEquals(2, calls.get())
    }
}
