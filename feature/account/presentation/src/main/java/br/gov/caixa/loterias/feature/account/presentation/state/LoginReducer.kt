package br.gov.caixa.loterias.feature.account.presentation.state

sealed interface LoginAction {
    data object AuthorizationStarted : LoginAction
    data object AuthorizationCancelled : LoginAction
    data class AuthorizationFailed(val message: String) : LoginAction
    data object BiometricAuthorizationFailed : LoginAction
    data class BrowserFallbackConsumed(val generation: Long) : LoginAction
    data object BiometricDisableRequested : LoginAction
    data object BiometricEnrollmentRequested : LoginAction
    data object BiometricEnrollmentDismissed : LoginAction
    data object BiometricDisableDismissed : LoginAction
    data class BiometricAvailabilityChanged(
        val visible: Boolean,
        val rememberedLoginAvailable: Boolean = false,
        val firstName: String? = null,
    ) : LoginAction
    data class BiometricPreferenceChanged(val enabled: Boolean) : LoginAction
    data class BiometricPreferenceFailed(val message: String) : LoginAction
    data object LoginStarted : LoginAction
    data object LoginCancelled : LoginAction
    data class LoginFailed(val message: String?) : LoginAction
    data class AccessVerified(val authenticated: Boolean, val consultationOnly: Boolean, val destination: String) : LoginAction
    data object AccountCheckFailed : LoginAction
    data class VisitorEntryFailed(val message: String) : LoginAction
    data object EntryReset : LoginAction
    data object ConsultationNoticeAcknowledged : LoginAction
}

fun reduceLoginState(state: LoginUiState, action: LoginAction): LoginUiState = when (action) {
    LoginAction.AuthorizationStarted -> state.copy(authorizationPending = true, errorMessage = null)

    LoginAction.AuthorizationCancelled -> state.copy(authorizationPending = false, errorMessage = null)

    is LoginAction.AuthorizationFailed -> state.copy(authorizationPending = false, errorMessage = action.message)

    LoginAction.LoginStarted -> state.copy(isLoading = true, authorizationPending = false, errorMessage = null)

    LoginAction.LoginCancelled -> state.copy(isLoading = false)

    is LoginAction.LoginFailed -> state.copy(isLoading = false, errorMessage = action.message)

    is LoginAction.AccessVerified -> state.copy(
        isLoading = false,
        authenticated = action.authenticated,
        consultationOnlyNoticePending = action.consultationOnly,
        errorMessage = null,
        destination = action.destination
    )

    LoginAction.AccountCheckFailed -> state.copy(isLoading = false, blockedAccountCheckFailed = true)

    is LoginAction.VisitorEntryFailed -> state.copy(errorMessage = action.message)

    LoginAction.EntryReset -> LoginUiState(
        rememberedFirstName = state.rememberedFirstName,
        rememberedLoginAvailable = state.rememberedLoginAvailable,
        biometricEnabled = state.biometricEnabled,
        biometricRowVisible = state.biometricRowVisible,
        browserFallbackGeneration = state.browserFallbackGeneration
    )

    LoginAction.ConsultationNoticeAcknowledged -> state.copy(consultationOnlyNoticePending = false, authenticated = true)

    else -> reduceLoginBiometricState(state, action)
}

private fun reduceLoginBiometricState(state: LoginUiState, action: LoginAction): LoginUiState = when (action) {
    LoginAction.BiometricAuthorizationFailed -> state.copy(
        isLoading = false,
        authorizationPending = false,
        browserFallbackPending = true,
        browserFallbackGeneration = state.browserFallbackGeneration + 1,
        errorMessage = null
    )

    is LoginAction.BrowserFallbackConsumed -> if (state.browserFallbackGeneration == action.generation && state.browserFallbackPending) {
        state.copy(browserFallbackPending = false)
    } else {
        state
    }

    LoginAction.BiometricDisableRequested -> state.copy(biometricDisablePending = true)

    LoginAction.BiometricEnrollmentRequested -> state.copy(biometricEnrollmentPending = true, errorMessage = null)

    LoginAction.BiometricEnrollmentDismissed -> state.copy(biometricEnrollmentPending = false)

    LoginAction.BiometricDisableDismissed -> state.copy(biometricDisablePending = false)

    is LoginAction.BiometricAvailabilityChanged -> state.copy(
        biometricRowVisible = action.visible,
        rememberedLoginAvailable = action.rememberedLoginAvailable,
        rememberedFirstName = action.firstName
    )

    is LoginAction.BiometricPreferenceChanged -> state.copy(biometricEnabled = action.enabled, errorMessage = null)

    is LoginAction.BiometricPreferenceFailed -> state.copy(errorMessage = action.message)

    else -> state
}
