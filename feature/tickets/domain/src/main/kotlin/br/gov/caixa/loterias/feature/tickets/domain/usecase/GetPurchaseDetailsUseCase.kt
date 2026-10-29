package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase

fun interface GetPurchaseDetailsUseCase {
    suspend operator fun invoke(purchase: Purchase): AppResult<PurchaseDetails>
}
