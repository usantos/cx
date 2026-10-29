package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryRequest

fun interface PreparePoolEntryUseCase {
    suspend operator fun invoke(request: PoolEntryRequest): AppResult<PoolEntryDecision>
}
