package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

fun interface GetPurchaseBetsUseCase {
    suspend operator fun invoke(purchaseId: Long): AppResult<List<ConfirmedBet>>
}
