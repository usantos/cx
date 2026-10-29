package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice

internal class PrizeRedemptionNotices(
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val blockOfflineLogin: (ServiceRedirect) -> Boolean
) {
    fun interactionBlocked() = mutableState.value.registrationNotice != null || mutableState.value.registrationPending ||
        mutableState.value.notice != null || mutableState.value.redirect != null || mutableState.value.redirectPaused

    fun dismissNotice(expected: ServiceNoticeUiState): Boolean = synchronized(mutableState) {
        if (mutableState.value.notice !== expected) return false
        mutableState.dispatch(PrizeRedemptionStateAction.NoticeDismissed(expected))
        return true
    }

    fun consumeNoticeAction(expected: ServiceNoticeUiState): ServiceRedirect? = synchronized(mutableState) {
        if (mutableState.value.notice !== expected) return null
        val destination = expected.destination ?: return null
        if (blockOfflineLogin(destination)) return null
        mutableState.dispatch(PrizeRedemptionStateAction.NoticeActionConsumed(expected))
        return destination
    }

    fun dismissRegistrationNotice(expected: PrizeRegistrationNotice): Boolean = synchronized(mutableState) {
        if (mutableState.value.registrationNotice !== expected) return false
        mutableState.dispatch(PrizeRedemptionStateAction.RegistrationNoticeDismissed(expected))
        return true
    }

    fun consumeRegistrationNotice(expected: PrizeRegistrationNotice): String? = synchronized(mutableState) {
        if (mutableState.value.registrationNotice !== expected) return null
        mutableState.dispatch(PrizeRedemptionStateAction.RegistrationNoticeConsumed(expected))
        return expected.email
    }

    fun registrationFinished() = mutableState.dispatch(PrizeRedemptionStateAction.RegistrationFinished)
}
