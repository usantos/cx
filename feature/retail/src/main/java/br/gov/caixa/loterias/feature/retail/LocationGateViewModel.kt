package br.gov.caixa.loterias.feature.retail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LocationGateStatus {
    CHECKING,
    PERMISSION_REQUIRED,
    PERMISSION_PERMANENTLY_DENIED,
    PROVIDER_DISABLED,
    OUTSIDE_BRAZIL,
    ERROR,
}

data class LocationGateUiState(
    val status: LocationGateStatus = LocationGateStatus.CHECKING,
)

sealed interface LocationGateEffect {
    data object Allowed : LocationGateEffect
}

class LocationGateViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(LocationGateUiState())
    val state = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<LocationGateEffect>(extraBufferCapacity = 1)
    val effects = mutableEffects.asSharedFlow()

    fun retry() {
        mutableState.value = LocationGateUiState()
    }

    fun permissionRequired(permanentlyDenied: Boolean = false) {
        mutableState.value = LocationGateUiState(
            if (permanentlyDenied) LocationGateStatus.PERMISSION_PERMANENTLY_DENIED
            else LocationGateStatus.PERMISSION_REQUIRED,
        )
    }

    fun providerDisabled() {
        mutableState.value = LocationGateUiState(LocationGateStatus.PROVIDER_DISABLED)
    }

    fun outsideBrazil() {
        mutableState.value = LocationGateUiState(LocationGateStatus.OUTSIDE_BRAZIL)
    }

    fun failed() {
        mutableState.value = LocationGateUiState(LocationGateStatus.ERROR)
    }

    fun allowed() {
        viewModelScope.launch { mutableEffects.emit(LocationGateEffect.Allowed) }
    }
}
