package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.coordinator.LoginAuthorizationCoordinator
import br.gov.caixa.loterias.feature.account.presentation.coordinator.LoginBiometricPreferences
import br.gov.caixa.loterias.feature.account.presentation.coordinator.LoginCompletion
import br.gov.caixa.loterias.feature.account.presentation.coordinator.LoginEntryDecisions
import br.gov.caixa.loterias.feature.account.presentation.state.LoginAction
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceLoginState

class LoginViewModel(
    private val authentication: SessionAuthentication,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val verifyAccess: VerifyPostLoginAccessUseCase,
    private val biometric: br.gov.caixa.loterias.feature.account.domain.port.BiometricLoginPreferences? =
        authentication as? br.gov.caixa.loterias.feature.account.domain.port.BiometricLoginPreferences,
) : ViewModel() {
    private val mutableState = reduxStateStore<LoginUiState, LoginAction>(
        LoginUiState(
            biometricEnabled =
                biometric?.biometricEnabled == true,
            biometricRowVisible = biometric?.biometricHardwareAvailable != false
        ),
        ::reduceLoginState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val authorization = LoginAuthorizationCoordinator(mutableState, authentication)
    private val biometrics = LoginBiometricPreferences(mutableState, authentication, biometric)
    private val completion =
        LoginCompletion(mutableState, viewModelScope, dispatchers, authentication, verifyAccess, biometrics::biometricAuthorizationFailed)
    private val entry = LoginEntryDecisions(mutableState, authentication)

    init {
        biometrics.refreshBiometricAvailability()
    }

    fun beginAuthorization(): Boolean = authorization.beginAuthorization()

    fun authorizationCancelled() = authorization.authorizationCancelled()

    fun authorizationLaunchFailed() = authorization.authorizationLaunchFailed()

    fun canUseBiometricLogin(): Boolean = biometrics.canUseBiometricLogin()

    /** Erros ou cancelamento da solicitação e falhas na renovação lembrada continuam pelo SSO da CX. */
    fun biometricAuthorizationFailed() = biometrics.biometricAuthorizationFailed()

    fun consumeBrowserFallback(): Boolean = biometrics.consumeBrowserFallback()

    fun completeBiometric(exchange: suspend () -> Unit) = completion.completeLogin(biometricExchange = true, exchange = exchange)

    fun complete(exchange: suspend () -> Unit) = completion.completeLogin(exchange = exchange)
    fun completeSilent(exchange: suspend () -> Unit) = completion.completeLogin(silentExchange = true, exchange = exchange)

    /** A navegação de experimentação nunca herda credenciais de uma troca cujo acesso não foi verificado. */
    fun enterVisitor(): Boolean = entry.enterVisitor()

    /** A confirmação limpa a sessão local não validada; nunca inicia outro login por conta própria. */
    fun acknowledgeBlockedAccountFailure(): Boolean = entry.acknowledgeBlockedAccountFailure()

    /** O bloqueio parcial permite consulta; a confirmação não limpa a sessão válida. */
    fun acknowledgeConsultationOnlyNotice(): Boolean = entry.acknowledgeConsultationOnlyNotice()

    fun setBiometricEnabled(enabled: Boolean) = biometrics.setBiometricEnabled(enabled)

    fun startBiometricEnrollment() = biometrics.startBiometricEnrollment()

    fun refreshBiometricAvailability() = biometrics.refreshBiometricAvailability()

    fun dismissBiometricEnrollment() = biometrics.dismissBiometricEnrollment()

    fun confirmBiometricDisable(confirmed: Boolean) = biometrics.confirmBiometricDisable(confirmed)
}
