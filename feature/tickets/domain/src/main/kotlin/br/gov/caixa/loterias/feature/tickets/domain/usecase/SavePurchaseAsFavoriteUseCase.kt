package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface SavePurchaseAsFavoriteUseCase {
    suspend operator fun invoke(purchaseId: Long, name: String, keepSurprises: Boolean): AppResult<Unit>
}
