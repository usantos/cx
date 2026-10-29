package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption

fun interface GetPurchasePaymentMethodsUseCase {
    suspend operator fun invoke(): AppResult<List<TicketFilterOption>>
}
