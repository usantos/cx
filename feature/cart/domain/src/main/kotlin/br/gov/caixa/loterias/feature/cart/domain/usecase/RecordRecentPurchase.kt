package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.appResultOf
import java.math.BigDecimal
import java.time.Clock
import br.gov.caixa.loterias.feature.cart.domain.repository.RecentPurchaseRepository

class RecordRecentPurchase(private val repository: RecentPurchaseRepository, private val clock: Clock) : RecordRecentPurchaseUseCase {
    override suspend fun invoke(amount: BigDecimal) = appResultOf {
        require(amount.signum() > 0) { messageText("valor_da_compra_invalido_9e6191") }
        repository.record(amount, clock.instant())
    }
}
