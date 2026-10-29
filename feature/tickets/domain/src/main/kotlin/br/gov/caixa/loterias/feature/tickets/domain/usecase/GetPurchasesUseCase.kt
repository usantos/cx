package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase

fun interface GetPurchasesUseCase {
    suspend operator fun invoke(filters: Map<String, String>): AppResult<List<Purchase>>
}
