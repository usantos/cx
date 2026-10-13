package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import java.math.BigDecimal
import java.time.Clock
import java.time.Duration
import java.time.Instant

/** Local advisory history, not an authority for payment approval or idempotency. */
interface RecentPurchaseRepository {
    suspend fun latestPurchaseAt(amount: BigDecimal): Instant?
    suspend fun record(amount: BigDecimal, purchasedAt: Instant)
}

fun interface HasRecentPurchaseUseCase {
    suspend operator fun invoke(amount: BigDecimal): AppResult<Boolean>
}

class HasRecentPurchase(
    private val repository: RecentPurchaseRepository,
    private val clock: Clock,
) : HasRecentPurchaseUseCase {
    override suspend fun invoke(amount: BigDecimal) = appResultOf {
        require(amount.signum() > 0) { "Valor do carrinho inválido" }
        // Legacy uses the device clock and a strict lower bound (exactly 24h is excluded).
        repository.latestPurchaseAt(amount)?.isAfter(clock.instant().minus(Duration.ofHours(24))) == true
    }
}

fun interface RecordRecentPurchaseUseCase {
    suspend operator fun invoke(amount: BigDecimal): AppResult<Unit>
}

class RecordRecentPurchase(
    private val repository: RecentPurchaseRepository,
    private val clock: Clock,
) : RecordRecentPurchaseUseCase {
    override suspend fun invoke(amount: BigDecimal) = appResultOf {
        require(amount.signum() > 0) { "Valor da compra inválido" }
        repository.record(amount, clock.instant())
    }
}
