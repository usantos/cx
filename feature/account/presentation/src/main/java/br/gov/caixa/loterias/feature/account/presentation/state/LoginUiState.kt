package br.gov.caixa.loterias.feature.account.presentation.state

data class LoginUiState(
    val rememberedFirstName: String? = null,
    val rememberedLoginAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val authorizationPending: Boolean = false,
    val authenticated: Boolean = false,
    val biometricEnabled: Boolean = false,
    val errorMessage: String? = null,
    val browserFallbackGeneration: Long = 0,
    val browserFallbackPending: Boolean = false,
    val biometricRowVisible: Boolean = true,
    val biometricEnrollmentPending: Boolean = false,
    val biometricDisablePending: Boolean = false,
    val destination: String = "home",
    val blockedAccountCheckFailed: Boolean = false,
    val consultationOnlyNoticePending: Boolean = false,
)
