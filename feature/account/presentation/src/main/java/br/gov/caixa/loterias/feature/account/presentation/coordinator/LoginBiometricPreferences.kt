package br.gov.caixa.loterias.feature.account.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.state.LoginAction
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState

internal class LoginBiometricPreferences(
    private val mutableState: ReduxStateStore<LoginUiState, LoginAction>,
    private val authentication: SessionAuthentication,
    private val biometric: br.gov.caixa.loterias.feature.account.domain.port.BiometricLoginPreferences?,
) {
    private var enableAfterEnrollment = false

    fun canUseBiometricLogin(): Boolean = biometric?.let {
        it.biometricEnabled && it.biometricAvailable && it.biometricCredentialAvailable
    } == true

    fun biometricAuthorizationFailed() {
        authentication.cancelAuthorization()
        mutableState.dispatch(LoginAction.BiometricAuthorizationFailed)
    }

    fun consumeBrowserFallback(): Boolean = synchronized(mutableState) {
        val current = mutableState.value
        if (!current.browserFallbackPending) return@synchronized false
        mutableState.dispatch(LoginAction.BrowserFallbackConsumed(current.browserFallbackGeneration))
        true
    }

    fun setBiometricEnabled(enabled: Boolean) {
        if (mutableState.value.isLoading || mutableState.value.authorizationPending || mutableState.value.biometricEnrollmentPending ||
            mutableState.value.biometricDisablePending
        ) {
            return
        }
        if (!enabled && mutableState.value.biometricEnabled) {
            mutableState.dispatch(LoginAction.BiometricDisableRequested)
        } else if (enabled && biometric?.biometricAvailable == false) {
            mutableState.dispatch(LoginAction.BiometricEnrollmentRequested)
        } else {
            updateBiometricPreference(enabled)
        }
    }

    fun startBiometricEnrollment() {
        if (!mutableState.value.biometricEnrollmentPending) return
        enableAfterEnrollment = true
        mutableState.dispatch(LoginAction.BiometricEnrollmentDismissed)
    }

    fun refreshBiometricAvailability() {
        val remembered = canUseBiometricLogin()
        mutableState.dispatch(
            LoginAction.BiometricAvailabilityChanged(
                visible = biometric?.biometricHardwareAvailable != false,
                rememberedLoginAvailable = remembered,
                firstName = biometric?.biometricFirstName?.takeIf { remembered }
            )
        )
        if (enableAfterEnrollment && biometric?.biometricAvailable == true) {
            enableAfterEnrollment = false
            updateBiometricPreference(true)
        }
    }

    fun dismissBiometricEnrollment() {
        enableAfterEnrollment = false
        mutableState.dispatch(LoginAction.BiometricEnrollmentDismissed)
    }

    fun confirmBiometricDisable(confirmed: Boolean) {
        if (!mutableState.value.biometricDisablePending) return
        mutableState.dispatch(LoginAction.BiometricDisableDismissed)
        if (confirmed) updateBiometricPreference(false)
    }

    private fun updateBiometricPreference(enabled: Boolean) {
        try {
            biometric?.setBiometricEnabled(enabled)
            mutableState.dispatch(LoginAction.BiometricPreferenceChanged(enabled))
            refreshBiometricAvailability()
        } catch (_: Exception) {
            mutableState.dispatch(LoginAction.BiometricPreferenceFailed(messageText("nao_foi_possivel_atualizar_a_bio_eaef6c")))
        }
    }
}
