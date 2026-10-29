package br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueGateAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueGateState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceQueueGateState

class QueueGateViewModel : ViewModel() {
    private val mutableState = reduxStateStore<QueueGateState, QueueGateAction>(QueueGateState(), ::reduceQueueGateState).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private var generation = 0L
    fun begin(): Long {
        generation++
        mutableState.dispatch(QueueGateAction.Began)
        return generation
    }
    fun isCurrent(request: Long): Boolean = request == generation
    fun reset() {
        generation++
        mutableState.dispatch(QueueGateAction.Reset)
    }
    fun passed(request: Long = generation): Boolean {
        if (!isCurrent(request)) return false
        mutableState.dispatch(QueueGateAction.Passed)
        return true
    }
    fun noInternet(request: Long = generation) {
        if (isCurrent(request)) mutableState.dispatch(QueueGateAction.NoInternet)
    }

    // Uma falha de transporte ou certificado nunca deve ser tratada como admissão à fila.
    fun failed(request: Long = generation): Boolean {
        if (!isCurrent(request)) return false
        mutableState.dispatch(QueueGateAction.Failed)
        return true
    }
    fun retry() {
        generation++
        mutableState.dispatch(QueueGateAction.Retried)
    }
}
