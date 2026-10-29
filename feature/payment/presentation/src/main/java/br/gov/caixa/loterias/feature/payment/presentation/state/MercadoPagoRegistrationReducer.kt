package br.gov.caixa.loterias.feature.payment.presentation.state

import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistration

internal sealed interface MercadoPagoRegistrationAction {
    data object Started : MercadoPagoRegistrationAction
    data class Prepared(val request: MercadoPagoRegistration) : MercadoPagoRegistrationAction
    data object BrowserOpened : MercadoPagoRegistrationAction
    data object NavigationStarted : MercadoPagoRegistrationAction
    data class Failed(val message: String) : MercadoPagoRegistrationAction
    data class NoticeReceived(val notice: ServiceNoticeUiState) : MercadoPagoRegistrationAction
    data class RedirectReceived(val redirect: ServiceRedirect) : MercadoPagoRegistrationAction
    data class RepeatConsentRequired(val consent: MercadoPagoRegistrationConsent) : MercadoPagoRegistrationAction
    data object Finished : MercadoPagoRegistrationAction
    data object Reset : MercadoPagoRegistrationAction
}

internal fun reduceMercadoPagoRegistrationState(
    state: MercadoPagoRegistrationUiState,
    action: MercadoPagoRegistrationAction,
): MercadoPagoRegistrationUiState {
    val reduced = when (action) {
        MercadoPagoRegistrationAction.Started -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.LOADING)

        is MercadoPagoRegistrationAction.Prepared -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.READY, action.request)

        MercadoPagoRegistrationAction.BrowserOpened -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.OPENED)

        MercadoPagoRegistrationAction.NavigationStarted -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.NAVIGATING)

        is MercadoPagoRegistrationAction.Failed -> MercadoPagoRegistrationUiState(
            MercadoPagoRegistrationStage.ERROR,
            errorMessage = action.message
        )

        is MercadoPagoRegistrationAction.NoticeReceived -> MercadoPagoRegistrationUiState(
            MercadoPagoRegistrationStage.NOTICE,
            notice = action.notice
        )

        is MercadoPagoRegistrationAction.RedirectReceived -> MercadoPagoRegistrationUiState(
            MercadoPagoRegistrationStage.REDIRECT,
            redirect = action.redirect
        )

        is MercadoPagoRegistrationAction.RepeatConsentRequired -> MercadoPagoRegistrationUiState(
            MercadoPagoRegistrationStage.REPEAT_CONSENT,
            repeatConsent = action.consent
        )

        MercadoPagoRegistrationAction.Finished -> MercadoPagoRegistrationUiState(MercadoPagoRegistrationStage.FINISHED)

        MercadoPagoRegistrationAction.Reset -> MercadoPagoRegistrationUiState()
    }

    return if (state == reduced) state else reduced
}
