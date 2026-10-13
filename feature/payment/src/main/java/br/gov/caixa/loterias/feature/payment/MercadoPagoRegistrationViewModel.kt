package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.NetworkAvailability
import br.gov.caixa.loterias.core.domain.account.GetAccountProfileUseCase
import br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistration
import br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistrationResult
import br.gov.caixa.loterias.core.domain.payment.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.serviceNoticeUi
import br.gov.caixa.loterias.core.presentation.connectionFailureNotice
import br.gov.caixa.loterias.core.presentation.loginConnectivityNotice
import java.time.Clock
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MercadoPagoRegistrationStage { IDLE, LOADING, READY, OPENED, ERROR, NOTICE, REDIRECT, NAVIGATING, REPEAT_CONSENT, FINISHED }

/** Identity distinguishes consecutive equal messages, even if loading is too brief to render. */
class MercadoPagoRegistrationConsent(val message: String) {
    override fun toString() = "MercadoPagoRegistrationConsent(redacted)"
}

data class MercadoPagoRegistrationUiState(
    val stage: MercadoPagoRegistrationStage = MercadoPagoRegistrationStage.IDLE,
    val request: MercadoPagoRegistration? = null,
    val errorMessage: String? = null,
    val notice: ServiceNoticeUiState? = null,
    val redirect: ServiceRedirect? = null,
    val repeatConsent: MercadoPagoRegistrationConsent? = null,
)

/** Starts only after explicit consent; returning from the browser never submits a purchase. */
class MercadoPagoRegistrationViewModel(
    private val profile: GetAccountProfileUseCase,
    private val prepare: PrepareMercadoPagoRegistrationUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val localClock: Clock,
    private val networkAvailability: NetworkAvailability,
) : ViewModel() {
    private val mutableState = MutableStateFlow(MercadoPagoRegistrationUiState())
    val state = mutableState.asStateFlow()
    private var selectedEmail: String? = null

    fun start(email: String? = null) {
        if (state.value.stage != MercadoPagoRegistrationStage.IDLE) return
        mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.LOADING)
        viewModelScope.launch(dispatchers.io) {
            val targetEmail = email ?: when (val result = profile()) {
                is AppResult.Success -> result.value.email
                is AppResult.Failure -> { fail(result.error); return@launch }
            }
            selectedEmail = targetEmail
            when (val result = prepare(targetEmail)) {
                is AppResult.Success -> when (val preparation = result.value) {
                    is MercadoPagoRegistrationResult.Ready -> {
                        selectedEmail = null
                        mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.READY, preparation.request)
                    }
                    is MercadoPagoRegistrationResult.Rejected -> handleServiceFailure(preparation.failure)
                }
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    fun takeBrowserRequest(expected: MercadoPagoRegistration): String? {
        if (state.value.stage != MercadoPagoRegistrationStage.READY || state.value.request !== expected) return null
        mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.OPENED)
        return expected.url
    }

    fun browserReturned(): Boolean {
        if (state.value.stage != MercadoPagoRegistrationStage.OPENED) return false
        reset()
        return true
    }

    fun browserUnavailable() {
        if (state.value.stage == MercadoPagoRegistrationStage.OPENED) {
            mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.ERROR,
                errorMessage = "Não foi possível abrir o cadastro Mercado Pago no navegador.")
        }
    }

    fun dismissError(): Boolean {
        if (state.value.stage != MercadoPagoRegistrationStage.ERROR) return false
        reset()
        return true
    }

    fun dismissNotice(expected: ServiceNoticeUiState): Boolean {
        if (state.value.stage != MercadoPagoRegistrationStage.NOTICE || state.value.notice !== expected) return false
        reset()
        return true
    }

    fun consumeNoticeAction(expected: ServiceNoticeUiState): ServiceRedirect? {
        if (state.value.stage != MercadoPagoRegistrationStage.NOTICE || state.value.notice !== expected) return null
        val destination = expected.destination ?: return null
        if (blockOfflineLogin(destination)) return null
        selectedEmail = null
        mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.NAVIGATING)
        return destination
    }

    fun consumeRedirect(expected: ServiceRedirect): Boolean {
        if (state.value.stage != MercadoPagoRegistrationStage.REDIRECT || state.value.redirect != expected) return false
        if (blockOfflineLogin(expected)) return false
        selectedEmail = null
        mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.NAVIGATING)
        return true
    }

    fun navigationReturned(): Boolean {
        if (state.value.stage != MercadoPagoRegistrationStage.NAVIGATING) return false
        reset()
        return true
    }

    private fun blockOfflineLogin(destination: ServiceRedirect): Boolean {
        val notice = loginConnectivityNotice(destination, networkAvailability) ?: return false
        selectedEmail = null
        mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.NOTICE, notice = notice)
        return true
    }

    fun finishWithoutAction(): Boolean {
        if (state.value.stage != MercadoPagoRegistrationStage.FINISHED) return false
        reset()
        return true
    }

    fun repeatRegistration(expected: MercadoPagoRegistrationConsent) {
        if (state.value.stage != MercadoPagoRegistrationStage.REPEAT_CONSENT || state.value.repeatConsent !== expected) return
        val email = selectedEmail ?: return
        reset()
        start(email)
    }

    fun declineRepeatRegistration(expected: MercadoPagoRegistrationConsent): Boolean {
        if (state.value.stage != MercadoPagoRegistrationStage.REPEAT_CONSENT || state.value.repeatConsent !== expected) return false
        reset()
        return true
    }

    private fun handleServiceFailure(failure: ServiceFailure) {
        if (failure !is ServiceFailure.RegistrationRequired) selectedEmail = null
        mutableState.value = when (failure) {
            is ServiceFailure.ConnectionFailed -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.NOTICE,
                notice = connectionFailureNotice(failure.reason, networkAvailability.current()))
            ServiceFailure.Unchanged -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.FINISHED)
            is ServiceFailure.NoticeRequired -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.NOTICE,
                notice = serviceNoticeUi(failure.notice, LocalDateTime.now(localClock)))
            is ServiceFailure.RedirectRequired -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.REDIRECT, redirect = failure.destination)
            is ServiceFailure.RegistrationRequired -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.REPEAT_CONSENT,
                repeatConsent = MercadoPagoRegistrationConsent(failure.message))
        }
    }

    private fun reset() {
        selectedEmail = null
        mutableState.value = MercadoPagoRegistrationUiState()
    }

    private fun fail(error: AppError) {
        selectedEmail = null
        mutableState.value = MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.ERROR,
            errorMessage = if (error == AppError.Network) "Não foi possível iniciar o cadastro Mercado Pago. Verifique sua conexão."
            else "Não foi possível iniciar o cadastro Mercado Pago. Tente novamente.")
    }
}
