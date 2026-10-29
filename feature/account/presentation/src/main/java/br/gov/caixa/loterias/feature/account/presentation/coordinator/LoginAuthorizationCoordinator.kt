package br.gov.caixa.loterias.feature.account.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.state.LoginAction
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState

internal class LoginAuthorizationCoordinator(
    private val mutableState: ReduxStateStore<LoginUiState, LoginAction>,
    private val authentication: SessionAuthentication,
) {

    fun beginAuthorization(): Boolean {
        if (mutableState.value.authorizationPending || mutableState.value.isLoading || mutableState.value.authenticated ||
            mutableState.value.blockedAccountCheckFailed ||
            mutableState.value.consultationOnlyNoticePending
        ) {
            return false
        }
        mutableState.dispatch(LoginAction.AuthorizationStarted)
        return true
    }

    fun authorizationCancelled() {
        authentication.cancelAuthorization()
        mutableState.dispatch(LoginAction.AuthorizationCancelled)
    }

    fun authorizationLaunchFailed() {
        authentication.cancelAuthorization()
        mutableState.dispatch(LoginAction.AuthorizationFailed(messageText("nao_foi_possivel_abrir_o_navegad_6f18e4")))
    }
}
