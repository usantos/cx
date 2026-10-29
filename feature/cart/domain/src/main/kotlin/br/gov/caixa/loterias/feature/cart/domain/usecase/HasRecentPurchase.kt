package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.appResultOf
import java.math.BigDecimal
import java.time.Clock
import java.time.Duration
import br.gov.caixa.loterias.feature.cart.domain.repository.RecentPurchaseRepository

class HasRecentPurchase(private val repository: RecentPurchaseRepository, private val clock: Clock) : HasRecentPurchaseUseCase {
    override suspend fun invoke(amount: BigDecimal) = appResultOf {
        require(amount.signum() > 0) { messageText("valor_do_carrinho_invalido_e1f371") }
        // O legado usa o relógio do dispositivo e um limite inferior estrito: exatamente 24h é excluído.
        repository.latestPurchaseAt(amount)?.isAfter(clock.instant().minus(Duration.ofHours(24))) == true
    }
}
