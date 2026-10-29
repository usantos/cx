package br.gov.caixa.loterias.feature.payment.presentation.state

import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistration

enum class MercadoPagoRegistrationStage { IDLE, LOADING, READY, OPENED, ERROR, NOTICE, REDIRECT, NAVIGATING, REPEAT_CONSENT, FINISHED }

/** A identidade distingue mensagens iguais consecutivas, mesmo quando o carregamento é breve demais para aparecer. */
class MercadoPagoRegistrationConsent(val message: String) {
    override fun toString() = "MercadoPagoRegistrationConsent(redacted)"
}

data class MercadoPagoRegistrationUiState(
    val stage: MercadoPagoRegistrationStage = MercadoPagoRegistrationStage.IDLE,
    val request: MercadoPagoRegistration? = null,
    val errorMessage: String? = null,
    val notice: ServiceNoticeUiState? = null,
    val redirect: ServiceRedirect? = null,
    val repeatConsent: MercadoPagoRegistrationConsent? = null
)
