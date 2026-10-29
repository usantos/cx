package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.isPendingPix
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchasePixDeadline
import java.time.Clock

internal class TicketsPixDeadlineTracker(private val clock: Clock, private val elapsedMillis: () -> Long) {
    private var deadline: PurchasePixDeadline? = null

    fun capture(purchase: Purchase, serverDateTime: String) {
        if (purchase.isPendingPix() && (
                deadline?.purchaseId != purchase.id ||
                    deadline?.expiresAt != purchase.paymentExpiresAt
                )
        ) {
            deadline = PurchasePixDeadline.capture(
                purchase.id,
                purchase.paymentExpiresAt,
                serverDateTime,
                elapsedMillis(),
                clock.instant()
            )
        }
    }

    fun remainingSeconds(purchase: Purchase?): Long {
        purchase ?: return 0
        return deadline?.takeIf { it.purchaseId == purchase.id && it.expiresAt == purchase.paymentExpiresAt }
            ?.remainingSeconds(elapsedMillis()) ?: 0
    }
}
