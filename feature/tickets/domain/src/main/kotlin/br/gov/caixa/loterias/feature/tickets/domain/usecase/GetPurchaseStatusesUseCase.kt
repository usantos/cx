package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption

fun interface GetPurchaseStatusesUseCase {
    suspend operator fun invoke(paymentMethodId: Long?): AppResult<List<TicketFilterOption>>
}
