package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.loginConnectivityNotice
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

internal class PrizeRedemptionRedirects(
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val networkAvailability: NetworkAvailability
) {
    fun consumeRedirect(expected: ServiceRedirect): Boolean = synchronized(mutableState) {
        if (mutableState.value.redirect != expected) return false
        if (blockOfflineLogin(expected)) return false
        mutableState.dispatch(PrizeRedemptionStateAction.RedirectConsumed(expected))
        return true
    }

    fun resumeAfterRedirect() = mutableState.dispatch(PrizeRedemptionStateAction.RedirectResumed)

    fun blockOfflineLogin(destination: ServiceRedirect): Boolean {
        val notice = loginConnectivityNotice(destination, networkAvailability) ?: return false
        mutableState.dispatch(PrizeRedemptionStateAction.OfflineLoginBlocked(notice))
        return true
    }
}
