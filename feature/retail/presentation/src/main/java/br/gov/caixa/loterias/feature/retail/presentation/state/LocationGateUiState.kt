package br.gov.caixa.loterias.feature.retail.presentation.state

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
    val checkAttempt: Long = 0,
)

sealed interface LocationGateEffect {
    data object Allowed : LocationGateEffect
}
