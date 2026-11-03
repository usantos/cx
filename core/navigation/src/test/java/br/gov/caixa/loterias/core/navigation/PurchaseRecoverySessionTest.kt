package br.gov.caixa.loterias.core.navigation

import br.gov.caixa.loterias.core.navigation.state.LoggedDetailContext
import br.gov.caixa.loterias.core.navigation.state.LoggedDetailContextCodec
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoveryEvidence
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import java.math.BigDecimal
import java.nio.ByteBuffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PurchaseRecoverySessionTest {
    private val confirmed = PurchaseRecoveryEvidence.Confirmed(PURCHASE_ID)
    private val privateKey = "lease-private-marker"
    private val purchase = Purchase(PURCHASE_ID, "hoje", "Aprovada", "Pix", BigDecimal.TEN)
    private val bet = ConfirmedBet(BET_ID, "Mega-Sena", 1, "hoje", "Ativa", listOf(1))

    @Test fun `current owner can recover a confirmed purchase and explicitly finish the recovery`() {
        val session = session(LoggedDetailContext("owner"))
        assertNull(session.current())
        assertTrue(session.grant(confirmed))
        assertEquals(confirmed, session.current())
        session.finish()
        assertNull(session.current())
    }

    @Test fun `accepted ambiguous review can recover without inventing a purchase identifier`() {
        val session = session(LoggedDetailContext("owner"))
        val review = PurchaseRecoveryEvidence.Review(null)
        assertTrue(session.grant(review))
        assertEquals(review, session.current())
    }

    @Test fun `a route identifier without owned evidence grants no recovery`() {
        val session = session(LoggedDetailContext("owner"))
        assertNull(session.current())
        assertFalse(session.permitsSelectedPurchase(confirmed.purchaseId))
        assertFalse(session.permitsSelectedBet(bet.id))
    }

    @Test fun `different owner or ended session cannot issue a recovery grant`() {
        val details = LoggedDetailContext("owner")
        assertFalse(session(details, owner = "another").grant(confirmed))
        assertFalse(session(details, current = { false }).grant(confirmed))
        assertNull(session(details).current())
    }

    @Test fun `late callback from an old lease cannot issue or read a grant`() {
        val details = LoggedDetailContext("owner")
        var active = true
        val old = session(details, current = { active })
        assertTrue(old.grant(confirmed))
        active = false
        assertNull(old.current())
        assertFalse(old.grant(PurchaseRecoveryEvidence.Review(null)))
        assertNull(session(details, key = "new-lease").current())
    }

    @Test fun `known purchase receipt is available only for its loaded authenticated owner`() {
        val details = LoggedDetailContext("owner").apply { selectedPurchase = purchase }
        assertTrue(session(details).permitsSelectedPurchase(purchase.id))
        assertFalse(session(details).permitsSelectedPurchase(bet.id))
        assertFalse(session(details, owner = "another").permitsSelectedPurchase(purchase.id))
        assertFalse(session(details, current = { false }).permitsSelectedPurchase(purchase.id))
        assertFalse(session(details).permitsSelectedPurchase(0))
    }

    @Test fun `known bet receipt uses the existing owner bound detail context`() {
        val details = LoggedDetailContext("owner").apply { selectedBet = bet }
        assertTrue(session(details).permitsSelectedBet(bet.id))
        assertFalse(session(details).permitsSelectedBet(purchase.id))
        assertFalse(session(details, current = { false }).permitsSelectedBet(bet.id))
    }

    @Test fun `process recovery preserves only owned evidence and rebinds after authentication`() {
        val details = LoggedDetailContext("owner")
        assertTrue(session(details).grant(confirmed))
        val bytes = requireNotNull(LoggedDetailContextCodec.save(details))
        assertFalse(String(bytes, Charsets.ISO_8859_1).contains(privateKey))
        val restored = requireNotNull(LoggedDetailContextCodec.defer(bytes))
        val next = session(restored, key = "new-process-lease")
        assertNull(next.current())
        restored.reconcile("owner")
        assertEquals(confirmed, next.current())
    }

    @Test fun `restored evidence is discarded for another authenticated owner`() {
        val details = LoggedDetailContext("owner")
        assertTrue(session(details).grant(confirmed))
        val restored = requireNotNull(LoggedDetailContextCodec.defer(requireNotNull(LoggedDetailContextCodec.save(details))))
        restored.reconcile("another")
        assertNull(session(restored, owner = "another").current())
    }

    @Test fun `logout clears evidence together with private navigation fields`() {
        val details = LoggedDetailContext("owner")
        val recovery = session(details)
        assertTrue(recovery.grant(confirmed))
        details.clear()
        assertNull(recovery.current())
        assertFalse(recovery.grant(confirmed))
    }

    @Test fun `codec versions two and three remain readable without granting recovery`() {
        val saved = requireNotNull(LoggedDetailContextCodec.save(LoggedDetailContext("owner")))
        val version = ByteBuffer.wrap(saved).int
        val versionThree = if (version == CURRENT_CODEC_VERSION) saved.copyOf(saved.size - OPTIONAL_MARKER_BYTES) else saved.copyOf()
        ByteBuffer.wrap(versionThree).putInt(PREVIOUS_CODEC_VERSION)
        val versionTwo = versionThree.copyOf(versionThree.size - APPLIED_FILTER_MARKER_BYTES)
        ByteBuffer.wrap(versionTwo).putInt(2)
        listOf(versionTwo, versionThree).forEach { bytes ->
            val restored = LoggedDetailContextCodec.restore(bytes, "owner")
            assertNotNull(restored)
            assertNull(session(requireNotNull(restored)).current())
        }
    }

    @Test fun `nonpositive purchase identifiers cannot form evidence`() {
        assertThrows(IllegalArgumentException::class.java) { PurchaseRecoveryEvidence.Confirmed(0) }
        assertThrows(IllegalArgumentException::class.java) { PurchaseRecoveryEvidence.Review(-1) }
    }

    private fun session(
        details: LoggedDetailContext,
        owner: String? = "owner",
        key: String = privateKey,
        current: () -> Boolean = { true },
    ) = PurchaseRecoverySession(details, owner, key, current)
}

private const val PURCHASE_ID = 9L
private const val BET_ID = 7L
private const val CURRENT_CODEC_VERSION = 4
private const val PREVIOUS_CODEC_VERSION = 3
private const val OPTIONAL_MARKER_BYTES = 1
private const val APPLIED_FILTER_MARKER_BYTES = 2
