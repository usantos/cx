package br.gov.caixa.loterias.feature.retail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.retail.domain.model.RetailLocationEligibility
import br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocationUseCase
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateEffect
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.reduceLocationGateState
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

class LocationGateViewModel(private val checkRetailLocation: CheckRetailLocationUseCase) : ViewModel() {
    private val mutableState = reduxStateStore<LocationGateUiState, LocationGateStateAction>(
        LocationGateUiState(),
        ::reduceLocationGateState
    ).also { addCloseable(it) }
    val state = mutableState.state

    private val mutableEffects = SingleConsumerEventQueue<LocationGateEffect>()
    val effects = mutableEffects.events
    private var lookup: Job? = null

    fun retry() {
        lookup?.cancel()
        mutableState.dispatch(LocationGateStateAction.Retry)
    }

    fun permissionRequired(permanentlyDenied: Boolean = false) {
        lookup?.cancel()
        mutableState.dispatch(LocationGateStateAction.PermissionRequired(permanentlyDenied))
    }

    fun providerDisabled() {
        lookup?.cancel()
        mutableState.dispatch(LocationGateStateAction.ProviderDisabled)
    }

    fun checkLocation(mockInsideBrazil: Boolean = false) {
        lookup?.cancel()
        lookup = viewModelScope.launch {
            val eligibility = checkRetailLocation(mockInsideBrazil)
            currentCoroutineContext().ensureActive()
            when (eligibility) {
                RetailLocationEligibility.ALLOWED -> mutableEffects.emit(LocationGateEffect.Allowed)

                RetailLocationEligibility.OUTSIDE_BRAZIL ->
                    mutableState.dispatch(LocationGateStateAction.OutsideBrazil)

                RetailLocationEligibility.UNAVAILABLE ->
                    mutableState.dispatch(LocationGateStateAction.CheckFailed)
            }
        }
    }
}
