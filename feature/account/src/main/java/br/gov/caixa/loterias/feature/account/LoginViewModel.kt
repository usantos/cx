package br.gov.caixa.loterias.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.AccountStatus
import br.gov.caixa.loterias.core.domain.account.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.core.domain.account.PostLoginAccessResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val authorizationPending: Boolean = false,
    val authenticated: Boolean = false,
    val biometricEnabled: Boolean = false,
    val errorMessage: String? = null,
    val destination: String = "home",
    val blockedAccountCheckFailed: Boolean = false,
    val consultationOnlyNoticePending: Boolean = false,
)

class LoginViewModel(
    private val authentication: AuthenticationGateway,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val verifyAccess: VerifyPostLoginAccessUseCase,
) : ViewModel() {
    private val biometric get() = authentication as? br.gov.caixa.loterias.core.auth.BiometricAuthenticationGateway
    private val mutableState = MutableStateFlow(LoginUiState(biometricEnabled = biometric?.biometricEnabled == true))
    val state = mutableState.asStateFlow()

    fun beginAuthorization(): Boolean {
        if (state.value.authorizationPending || state.value.isLoading || state.value.authenticated || state.value.blockedAccountCheckFailed || state.value.consultationOnlyNoticePending) return false
        mutableState.update { it.copy(authorizationPending = true, errorMessage = null) }
        return true
    }

    fun authorizationCancelled() {
        authentication.cancelAuthorization()
        mutableState.update { it.copy(authorizationPending = false, errorMessage = null) }
    }

    fun authorizationLaunchFailed() {
        authentication.cancelAuthorization()
        mutableState.update {
            it.copy(authorizationPending = false, errorMessage = "Não foi possível abrir o navegador para entrar. Tente novamente.")
        }
    }

    fun canUseBiometricLogin(): Boolean = biometric?.let {
        it.biometricEnabled && it.biometricAvailable && it.biometricCredentialAvailable
    } == true

    fun completeBiometric(resultData: android.content.Intent?) = completeLogin {
        checkNotNull(biometric).completeBiometricAuthorization(resultData)
    }

    fun complete(resultData: android.content.Intent?) = completeLogin {
        authentication.completeAuthorization(resultData)
    }

    private fun completeLogin(exchange: suspend () -> Unit) {
        if (state.value.isLoading || state.value.authenticated || state.value.blockedAccountCheckFailed || state.value.consultationOnlyNoticePending) return
        mutableState.value = LoginUiState(
            isLoading = true,
            biometricEnabled = state.value.biometricEnabled,
        )
        viewModelScope.launch(dispatchers.io) {
            try {
                exchange()
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                mutableState.update { it.copy(isLoading = false) }
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { current ->
                    current.copy(isLoading = false, errorMessage = "Não foi possível entrar. Tente novamente.")
                }
                return@launch
            }
            val access = try { verifyAccess() } catch (cancelled: kotlinx.coroutines.CancellationException) {
                mutableState.update { it.copy(isLoading = false) }
                throw cancelled
            }
            when (access) {
                is PostLoginAccessResult.Verified -> mutableState.update { current ->
                    current.copy(
                        isLoading = false,
                        authenticated = access.access.status != AccountStatus.PARTIALLY_BLOCKED,
                        consultationOnlyNoticePending = access.access.status == AccountStatus.PARTIALLY_BLOCKED,
                        errorMessage = null,
                        destination = access.access.destination.route,
                    )
                }
                is PostLoginAccessResult.AccountCheckFailed -> {
                    // Revoke the acquired owner before exposing an error or allowing Back to an old graph.
                    try {
                        authentication.logout()
                        mutableState.update { current ->
                            current.copy(isLoading = false, errorMessage = access.error.userMessage())
                        }
                    } catch (_: Exception) {
                        // Keep the shared blocking notice until local cleanup can be retried safely.
                        mutableState.update { it.copy(isLoading = false, blockedAccountCheckFailed = true) }
                    }
                }
                PostLoginAccessResult.BlockedAccountCheckFailed -> mutableState.update {
                    it.copy(isLoading = false, blockedAccountCheckFailed = true)
                }
            }
        }
    }

    /** Trial navigation never inherits credentials from an exchange whose access was not verified. */
    fun enterVisitor(): Boolean {
        val current = state.value
        if (current.isLoading || current.authorizationPending || current.authenticated ||
            current.blockedAccountCheckFailed || current.consultationOnlyNoticePending) return false
        try { authentication.logout() } catch (_: Exception) {
            mutableState.update { it.copy(errorMessage = "Não foi possível encerrar a sessão. Tente novamente.") }
            return false
        }
        mutableState.update { LoginUiState(biometricEnabled = it.biometricEnabled) }
        return true
    }

    /** Acknowledgment clears the unvalidated local session; never launches another login itself. */
    fun acknowledgeBlockedAccountFailure(): Boolean {
        if (!state.value.blockedAccountCheckFailed) return false
        // If secure storage cannot clear credentials, retain the blocking decision for a retry.
        try { authentication.logout() } catch (_: Exception) { return false }
        mutableState.update { LoginUiState(biometricEnabled = it.biometricEnabled) }
        return true
    }

    /** Partial blocking permits consultation; acknowledgment does not clear the valid session. */
    fun acknowledgeConsultationOnlyNotice(): Boolean {
        if (!state.value.consultationOnlyNoticePending || state.value.blockedAccountCheckFailed) return false
        mutableState.update { it.copy(consultationOnlyNoticePending = false, authenticated = true) }
        return true
    }

    fun setBiometricEnabled(enabled: Boolean) {
        if (state.value.isLoading || state.value.authorizationPending) return
        try {
            biometric?.setBiometricEnabled(enabled)
            mutableState.update { it.copy(biometricEnabled = enabled, errorMessage = null) }
        } catch (_: Exception) {
            mutableState.update { it.copy(biometricEnabled = false,
                errorMessage = "Cadastre a biometria ou o bloqueio de tela nas configurações do aparelho.") }
        }
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível validar o cadastro. Verifique sua internet e tente novamente."
        is AppError.Validation -> reason.ifBlank { "Não foi possível validar o cadastro." }
        AppError.Unexpected -> "Ocorreu um erro inesperado ao validar o cadastro. Tente novamente."
    }
}

private val PostLoginDestination.route: String
    get() = when (this) {
        PostLoginDestination.HOME -> "home"
        PostLoginDestination.REGISTRATION -> "account/register"
        PostLoginDestination.TERMS -> "support/terms-acceptance"
        PostLoginDestination.SELF_ASSESSMENT -> "responsible/start/required"
    }
