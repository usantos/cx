package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository

class GetPurchaseProgress(private val repository: PaymentRepository) : GetPurchaseProgressUseCase {
    override suspend fun invoke(purchaseId: Long) = appResultOf {
        require(purchaseId > 0) { messageText("compra_invalida_66f609") }
        repository.purchaseProgress(purchaseId)
    }
}
