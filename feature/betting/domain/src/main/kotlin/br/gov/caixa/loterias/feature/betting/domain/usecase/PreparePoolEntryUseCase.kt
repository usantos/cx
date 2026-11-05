package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.betting.model.PoolEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision

fun interface PreparePoolEntryUseCase {
    suspend operator fun invoke(request: PoolEntryRequest): AppResult<PoolEntryDecision>
}
