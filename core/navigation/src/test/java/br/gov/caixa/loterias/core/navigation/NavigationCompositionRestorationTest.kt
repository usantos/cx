// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.navigation

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.core.navigation.workflow.rememberPrivateEntryKey
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Testes de composição apenas em execução: a navegação com estado salvável dispensa uma tela renderizada. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NavigationCompositionRestorationTest {
    private class EmptyApplier : AbstractApplier<Unit>(Unit) {
        override fun insertTopDown(index: Int, instance: Unit) = Unit
        override fun insertBottomUp(index: Int, instance: Unit) = Unit
        override fun remove(index: Int, count: Int) = Unit
        override fun move(from: Int, to: Int, count: Int) = Unit
        override fun onClear() = Unit
    }
    private class Harness(restored: Map<String, List<Any?>>? = null) : AutoCloseable {
        val registry = SaveableStateRegistry(restored) { true }
        private val recomposer = Recomposer(Dispatchers.Unconfined)
        private val composition = Composition(EmptyApplier(), recomposer)
        fun content(block: @Composable () -> Unit) {
            composition.setContent { CompositionLocalProvider(LocalSaveableStateRegistry provides registry, content = block) }
        }
        override fun close() {
            composition.dispose()
            recomposer.close()
        }
    }

    @Test fun `private entry key survives configuration only for exact live lease`() {
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        var selected: AuthenticatedSessionIdentity? = identity
        var key = ""
        val render: @Composable () -> Unit = { key = rememberPrivateEntryKey(selected) }
        val saved = Harness().use { harness ->
            harness.content(render)
            harness.registry.performSave()
        }
        val original = key
        Harness(saved).use { it.content(render) }
        assertEquals(original, key)
        selected = AuthenticatedSessionIdentity(identity.ownerKey)
        Harness(saved).use { it.content(render) }
        assertNotEquals(original, key)
        selected = null
        Harness(saved).use { it.content(render) }
        assertNotEquals(original, key)
    }

    @Test fun `navigation restores durable revision but clears volatile prepared parameters`() {
        lateinit var state: AppNavigationState
        val render: @Composable () -> Unit = { state = rememberAppNavigationState("a".repeat(64)) }
        val saved = Harness().use { harness ->
            harness.content(render)
            state.marketplaceRevision = 7
            state.requestedHomeModalityId = 2
            harness.registry.performSave()
        }
        Harness(saved).use { harness ->
            harness.content(render)
            assertEquals(7, state.marketplaceRevision)
            assertNull(state.preparedBettingParameters)
            assertNull(state.requestedHomeModalityId)
            assertEquals("a".repeat(64), state.details.owner)
        }
    }

    @Test fun `raw session continuations and expiry markers never restore from an activity bundle`() {
        lateinit var session: AppSessionState
        val render: @Composable () -> Unit = { session = rememberAppSessionState() }
        val saved = Harness().use { harness ->
            harness.content(render)
            session.pendingRestoredRoute = "tickets/my-bets"
            session.visitorLoginRequest = "cart/current"
            session.visitorReturnRoute = "home/pool"
            session.visitorReturnOwnerKey = "a".repeat(64)
            session.resumedHomeAction = "home/pool"
            session.accountCompletionRequired = true
            session.foregroundExpiryMarker = "expiry-marker"
            session.prizeChannelHost = true
            harness.registry.performSave()
        }
        Harness(saved).use { harness ->
            harness.content(render)
            assertNull(session.pendingRestoredRoute)
            assertNull(session.visitorLoginRequest)
            assertNull(session.visitorReturnRoute)
            assertNull(session.visitorReturnOwnerKey)
            assertNull(session.resumedHomeAction)
            assertFalse(session.accountCompletionRequired)
            assertNull(session.foregroundExpiryMarker)
            assertFalse(session.prizeChannelHost)
        }
    }

    @Test fun `private purchase payload is absent from saved navigation and requires encrypted recovery`() {
        lateinit var state: AppNavigationState
        val render: @Composable () -> Unit = { state = rememberAppNavigationState("a".repeat(64)) }
        val saved = Harness().use { harness ->
            harness.content(render)
            state.details.selectedPurchase = br.gov.caixa.loterias.feature.tickets.domain.model.Purchase(
                id = 1,
                date = "fixture",
                status = "pending",
                paymentMethod = "Pix",
                total = java.math.BigDecimal.ONE,
                pixCopyPaste = "PRIVATE_PIX_SENTINEL"
            )
            harness.registry.performSave()
        }
        assertFalse(saved.values.flatten().any { it is ByteArray })
        Harness(saved).use { harness ->
            harness.content(render)
            assertNull(state.details.selectedPurchase)
            assertNull(state.details.pendingSnapshot)
        }
    }
}
