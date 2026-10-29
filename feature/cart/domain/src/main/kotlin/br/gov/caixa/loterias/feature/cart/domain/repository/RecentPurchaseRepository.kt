package br.gov.caixa.loterias.feature.cart.domain.repository

import java.math.BigDecimal
import java.time.Instant

/** Histórico local de orientação, sem autoridade para aprovação de pagamento ou idempotência. */
interface RecentPurchaseRepository {
    suspend fun latestPurchaseAt(amount: BigDecimal): Instant?
    suspend fun record(amount: BigDecimal, purchasedAt: Instant)
}
