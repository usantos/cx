package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision

internal sealed interface RapidEntryAction {
    data class Prepared(val result: AppResult<HomeBettingDecision>) : RapidEntryAction
}

internal fun reduceRapidEntryState(state: AppResult<HomeBettingDecision>?, action: RapidEntryAction): AppResult<HomeBettingDecision>? =
    when (action) {
        is RapidEntryAction.Prepared -> if (state === action.result) state else action.result
    }
