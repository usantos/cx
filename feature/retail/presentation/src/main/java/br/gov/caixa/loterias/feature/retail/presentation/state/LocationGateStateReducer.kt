package br.gov.caixa.loterias.feature.retail.presentation.state

internal sealed interface LocationGateStateAction {
    data object Retry : LocationGateStateAction
    data class PermissionRequired(val permanentlyDenied: Boolean) : LocationGateStateAction
    data object ProviderDisabled : LocationGateStateAction
    data object OutsideBrazil : LocationGateStateAction
    data object CheckFailed : LocationGateStateAction
}

internal fun reduceLocationGateState(state: LocationGateUiState, action: LocationGateStateAction): LocationGateUiState = when (action) {
    LocationGateStateAction.Retry -> LocationGateUiState(checkAttempt = state.checkAttempt + 1)

    is LocationGateStateAction.PermissionRequired -> LocationGateUiState(
        if (action.permanentlyDenied) LocationGateStatus.PERMISSION_PERMANENTLY_DENIED else LocationGateStatus.PERMISSION_REQUIRED
    )

    LocationGateStateAction.ProviderDisabled -> LocationGateUiState(LocationGateStatus.PROVIDER_DISABLED)

    LocationGateStateAction.OutsideBrazil -> LocationGateUiState(LocationGateStatus.OUTSIDE_BRAZIL)

    LocationGateStateAction.CheckFailed -> LocationGateUiState(LocationGateStatus.ERROR)
}
