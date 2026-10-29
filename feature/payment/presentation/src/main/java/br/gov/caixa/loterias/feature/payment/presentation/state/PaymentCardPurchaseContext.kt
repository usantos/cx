package br.gov.caixa.loterias.feature.payment.presentation.state

import br.gov.caixa.loterias.feature.payment.domain.model.CardPurchasePolling
import java.math.BigDecimal

internal class PaymentCardPurchaseContext {
    var trackedPurchase: PaymentCardTrackedPurchase? = null
}

internal data class PaymentCardTrackedPurchase(val poll: CardPurchasePolling, val amount: BigDecimal)
