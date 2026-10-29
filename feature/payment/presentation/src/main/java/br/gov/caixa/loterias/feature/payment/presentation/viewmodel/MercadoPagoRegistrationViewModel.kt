package br.gov.caixa.loterias.feature.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.rule.connectionFailureNotice
import br.gov.caixa.loterias.core.presentation.rule.loginConnectivityNotice
import br.gov.caixa.loterias.core.presentation.rule.serviceNoticeUi
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistration
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationResult
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.state.MercadoPagoRegistrationAction
import br.gov.caixa.loterias.feature.payment.presentation.state.MercadoPagoRegistrationConsent
import br.gov.caixa.loterias.feature.payment.presentation.state.MercadoPagoRegistrationStage
import br.gov.caixa.loterias.feature.payment.presentation.state.MercadoPagoRegistrationUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reduceMercadoPagoRegistrationState
import java.time.Clock
import java.time.LocalDateTime
import kotlinx.coroutines.launch

/** Inicia somente após consentimento explícito; retornar do navegador nunca envia uma compra. */
class MercadoPagoRegistrationViewModel(
    private val profile: GetAccountProfileUseCase,
    private val prepare: PrepareMercadoPagoRegistrationUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val localClock: Clock,
    private val networkAvailability: NetworkAvailability,
) : ViewModel() {
    private val mutableState = reduxStateStore<MercadoPagoRegistrationUiState, MercadoPagoRegistrationAction>(
        MercadoPagoRegistrationUiState(),
        ::reduceMercadoPagoRegistrationState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private var selectedEmail: String? = null

    fun start(email: String? = null) {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.IDLE) return
            mutableState.dispatch(MercadoPagoRegistrationAction.Started)
            viewModelScope.launch(dispatchers.io) {
                val targetEmail = email ?: when (val result = profile()) {
                    is AppResult.Success -> result.value.email

                    is AppResult.Failure -> {
                        fail(result.error)
                        return@launch
                    }
                }
                selectedEmail = targetEmail
                when (val result = prepare(targetEmail)) {
                    is AppResult.Success -> when (val preparation = result.value) {
                        is MercadoPagoRegistrationResult.Ready -> {
                            selectedEmail = null
                            mutableState.dispatch(MercadoPagoRegistrationAction.Prepared(preparation.request))
                        }

                        is MercadoPagoRegistrationResult.Rejected -> handleServiceFailure(preparation.failure)
                    }

                    is AppResult.Failure -> fail(result.error)
                }
            }
        }
    }

    fun takeBrowserRequest(expected: MercadoPagoRegistration): String? {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.READY || state.value.request !== expected) return null
            mutableState.dispatch(MercadoPagoRegistrationAction.BrowserOpened)
            return expected.url
        }
    }

    fun browserReturned(): Boolean {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.OPENED) return false
            reset()
            return true
        }
    }

    fun browserUnavailable() {
        synchronized(mutableState) {
            if (state.value.stage == MercadoPagoRegistrationStage.OPENED) {
                mutableState.dispatch(MercadoPagoRegistrationAction.Failed(messageText("nao_foi_possivel_abrir_o_cadastr_c2af28")))
            }
        }
    }

    fun dismissError(): Boolean {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.ERROR) return false
            reset()
            return true
        }
    }

    fun dismissNotice(expected: ServiceNoticeUiState): Boolean {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.NOTICE || state.value.notice !== expected) return false
            reset()
            return true
        }
    }

    fun consumeNoticeAction(expected: ServiceNoticeUiState): ServiceRedirect? {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.NOTICE || state.value.notice !== expected) return null
            val destination = expected.destination ?: return null
            if (blockOfflineLogin(destination)) return null
            selectedEmail = null
            mutableState.dispatch(MercadoPagoRegistrationAction.NavigationStarted)
            return destination
        }
    }

    fun consumeRedirect(expected: ServiceRedirect): Boolean {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.REDIRECT || state.value.redirect != expected) return false
            if (blockOfflineLogin(expected)) return false
            selectedEmail = null
            mutableState.dispatch(MercadoPagoRegistrationAction.NavigationStarted)
            return true
        }
    }

    fun navigationReturned(): Boolean {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.NAVIGATING) return false
            reset()
            return true
        }
    }

    private fun blockOfflineLogin(destination: ServiceRedirect): Boolean {
        val notice = loginConnectivityNotice(destination, networkAvailability) ?: return false
        selectedEmail = null
        mutableState.dispatch(MercadoPagoRegistrationAction.NoticeReceived(notice))
        return true
    }

    fun finishWithoutAction(): Boolean {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.FINISHED) return false
            reset()
            return true
        }
    }

    fun repeatRegistration(expected: MercadoPagoRegistrationConsent) {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.REPEAT_CONSENT || state.value.repeatConsent !== expected) return
            val email = selectedEmail ?: return
            reset()
            start(email)
        }
    }

    fun declineRepeatRegistration(expected: MercadoPagoRegistrationConsent): Boolean {
        synchronized(mutableState) {
            if (state.value.stage != MercadoPagoRegistrationStage.REPEAT_CONSENT || state.value.repeatConsent !== expected) return false
            reset()
            return true
        }
    }

    private fun handleServiceFailure(failure: ServiceFailure) {
        if (failure !is ServiceFailure.RegistrationRequired) selectedEmail = null
        when (failure) {
            is ServiceFailure.ConnectionFailed -> mutableState.dispatch(
                MercadoPagoRegistrationAction.NoticeReceived(connectionFailureNotice(failure.reason, networkAvailability.current()))
            )

            ServiceFailure.Unchanged -> mutableState.dispatch(MercadoPagoRegistrationAction.Finished)

            is ServiceFailure.NoticeRequired -> mutableState.dispatch(
                MercadoPagoRegistrationAction.NoticeReceived(serviceNoticeUi(failure.notice, LocalDateTime.now(localClock)))
            )

            is ServiceFailure.RedirectRequired -> mutableState.dispatch(MercadoPagoRegistrationAction.RedirectReceived(failure.destination))

            is ServiceFailure.RegistrationRequired -> mutableState.dispatch(
                MercadoPagoRegistrationAction.RepeatConsentRequired(MercadoPagoRegistrationConsent(failure.message))
            )
        }
    }

    private fun reset() {
        selectedEmail = null
        mutableState.dispatch(MercadoPagoRegistrationAction.Reset)
    }

    private fun fail(error: AppError) {
        selectedEmail = null
        mutableState.dispatch(
            MercadoPagoRegistrationAction.Failed(
                if (error ==
                    AppError.Network
                ) {
                    messageText("nao_foi_possivel_iniciar_o_cadas_cf9f3e")
                } else {
                    messageText("nao_foi_possivel_iniciar_o_cadas_640b5c")
                }
            )
        )
    }
}
