package br.gov.caixa.loterias.core.navigation

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.saveable.SaverScope
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import br.gov.caixa.loterias.core.navigation.state.LoggedDetailContext
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoveryEvidence
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySaver
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession
import br.gov.caixa.loterias.core.navigation.state.rememberLoggedDetailContext
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PurchaseRecoveryRestorationTest {
    private val owner = "a".repeat(OWNER_HASH_LENGTH)
    private val evidence = PurchaseRecoveryEvidence.Confirmed(PURCHASE_ID)
    private val scope = object : SaverScope {
        override fun canBeSaved(value: Any): Boolean = true
    }

    @Test fun `saver stores only owner kind and id without lease or purchase payment payload`() {
        val details = LoggedDetailContext(owner)
        details.selectedPurchase = Purchase(PURCHASE_ID, "date", "status", "Pix", BigDecimal.TEN, "PIX-PRIVATE-CODE")
        assertTrue(session(details).grant(evidence))
        val saved = with(PurchaseRecoverySaver) { scope.save(details) }
        assertEquals(listOf(owner, "confirmed", PURCHASE_ID.toString()), saved)
        assertFalse(saved.toString().contains("lease-private-marker"))
        assertFalse(saved.toString().contains("PIX-PRIVATE-CODE"))
        val restored = requireNotNull(PurchaseRecoverySaver.restore(requireNotNull(saved)))
        assertNull(restored.selectedPurchase)
        assertNull(session(restored).current())
        restored.reconcile(owner)
        assertEquals(evidence, session(restored, "new-lease").current())
    }

    @Test fun `composition helper restores recovery only after matching authentication`() {
        lateinit var details: LoggedDetailContext
        var authenticatedOwner: String? = owner
        val render: @Composable () -> Unit = { details = rememberLoggedDetailContext(authenticatedOwner) }
        val saved = Harness().use { harness ->
            harness.content(render)
            assertTrue(session(details).grant(evidence))
            harness.registry.performSave()
        }
        authenticatedOwner = null
        Harness(saved).use { harness ->
            harness.content(render)
            assertNull(details.owner)
            assertNull(session(details).current())
            details.reconcile(owner)
            assertEquals(evidence, session(details, "process-lease").current())
        }
    }

    @Test fun `pending evidence survives another recreation while authentication is still absent`() {
        lateinit var details: LoggedDetailContext
        var authenticatedOwner: String? = owner
        val render: @Composable () -> Unit = { details = rememberLoggedDetailContext(authenticatedOwner) }
        val saved = Harness().use { harness ->
            harness.content(render)
            assertTrue(session(details).grant(PurchaseRecoveryEvidence.Review(null)))
            harness.registry.performSave()
        }
        authenticatedOwner = null
        val pending = Harness(saved).use { harness ->
            harness.content(render)
            assertNull(session(details).current())
            harness.registry.performSave()
        }
        authenticatedOwner = owner
        Harness(pending).use { harness ->
            harness.content(render)
            assertEquals(PurchaseRecoveryEvidence.Review(null), session(details, "next-process-lease").current())
        }
    }

    @Test fun `different authenticated owner discards pending recovery`() {
        val details = LoggedDetailContext(owner)
        assertTrue(session(details).grant(evidence))
        val saved = requireNotNull(with(PurchaseRecoverySaver) { scope.save(details) })
        val restored = requireNotNull(PurchaseRecoverySaver.restore(saved))
        restored.reconcile("b".repeat(OWNER_HASH_LENGTH))
        assertNull(session(restored).current())
        assertNull(with(PurchaseRecoverySaver) { scope.save(restored) })
    }

    @Test fun `logout erases the saved grant and no receipt payload is restored`() {
        val details = LoggedDetailContext(owner)
        assertTrue(session(details).grant(evidence))
        details.clear()
        assertNull(with(PurchaseRecoverySaver) { scope.save(details) })
        assertNull(session(details).current())
    }

    private fun session(details: LoggedDetailContext, lease: String = "lease-private-marker") =
        PurchaseRecoverySession(details, owner, lease) { true }

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
}

private const val OWNER_HASH_LENGTH = 64
private const val PURCHASE_ID = 9L
