package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction

fun interface PrepareHomeBettingActionUseCase {
    suspend operator fun invoke(action: HomeBettingAction): AppResult<HomeBettingDecision>
}
