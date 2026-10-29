package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceiptDeliveryAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceiptDeliveryState
import br.gov.caixa.loterias.feature.retail.presentation.state.reducePrizeReceiptDelivery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class PrizeReceiptDeliveryReducerTest {
    @Test fun `cancelled claim cannot publish success or failure into a newer attempt`() {
        val writing = PrizeReceiptDeliveryState(pendingFileName = "receipt.pdf", generation = 1, activeWrite = 1)
        val cancelled = reducePrizeReceiptDelivery(writing, PrizeReceiptDeliveryAction.Cancelled)
        val retry = reducePrizeReceiptDelivery(cancelled, PrizeReceiptDeliveryAction.WriteStarted(NEWER_WRITE_CLAIM))
        assertSame(retry, reducePrizeReceiptDelivery(retry, PrizeReceiptDeliveryAction.WriteSucceeded(1, "receipt.pdf", "old")))
        assertSame(retry, reducePrizeReceiptDelivery(retry, PrizeReceiptDeliveryAction.WriteFailed(1, "old")))
        assertNull(retry.completion)
        assertEquals(emptyMap<String, String>(), retry.saved)
    }

    @Test fun `viewer failure and dismissal preserve the saved destination`() {
        val saved = PrizeReceiptDeliveryState(saved = mapOf("receipt.pdf" to "destination"))
        val failed = reducePrizeReceiptDelivery(saved, PrizeReceiptDeliveryAction.ViewerFailed("destination"))
        val dismissed = reducePrizeReceiptDelivery(failed, PrizeReceiptDeliveryAction.ViewerDismissed)
        assertNull(dismissed.viewerFailure)
        assertEquals(saved.saved, dismissed.saved)
        assertFalse(dismissed.saving)
    }
}

private const val NEWER_WRITE_CLAIM = 3L
