package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class PrizeRedemptionTimer(
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers
) {
    private var timer: Job? = null

    fun cancel() {
        timer?.cancel()
    }

    fun start(seconds: Int) {
        timer = scope.launch(dispatchers.io) {
            var remaining = seconds
            while (remaining > 0) {
                delay(1_000)
                remaining--
                mutableState.dispatch(PrizeRedemptionStateAction.TimerTicked(remaining))
            }
        }
    }
}
