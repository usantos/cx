package br.gov.caixa.loterias.feature.account.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.state.LoginAction
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState

internal class LoginEntryDecisions(
    private val mutableState: ReduxStateStore<LoginUiState, LoginAction>,
    private val authentication: SessionAuthentication,
) {

    fun enterVisitor(): Boolean {
        val current = mutableState.value
        if (current.isLoading || current.authorizationPending || current.authenticated ||
            current.blockedAccountCheckFailed || current.consultationOnlyNoticePending
        ) {
            return false
        }
        try {
            authentication.logout()
        } catch (_: Exception) {
            mutableState.dispatch(LoginAction.VisitorEntryFailed(messageText("nao_foi_possivel_encerrar_a_sess_3e9e60")))
            return false
        }
        mutableState.dispatch(LoginAction.EntryReset)
        return true
    }

    fun acknowledgeBlockedAccountFailure(): Boolean {
        if (!mutableState.value.blockedAccountCheckFailed) return false
        // Se o armazenamento seguro não puder limpar as credenciais, preserva a decisão de bloqueio para nova tentativa.
        try {
            authentication.logout()
        } catch (_: Exception) {
            return false
        }
        mutableState.dispatch(LoginAction.EntryReset)
        return true
    }

    fun acknowledgeConsultationOnlyNotice(): Boolean {
        if (!mutableState.value.consultationOnlyNoticePending || mutableState.value.blockedAccountCheckFailed) return false
        mutableState.dispatch(LoginAction.ConsultationNoticeAcknowledged)
        return true
    }
}
