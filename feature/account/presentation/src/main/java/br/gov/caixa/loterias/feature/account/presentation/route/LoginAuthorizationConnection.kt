package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

/** Recupera o hospedeiro e invalida somente a tentativa interrompida pela rede. */
internal class LoginAuthorizationConnection(
    private val state: LoginAuthorizationState,
    private val viewModel: LoginViewModel,
    private val network: NetworkAvailability?,
    private val activity: Activity?,
    private val returnFromBrowser: (() -> Unit)?,
) {
    fun disconnected() = network?.current() == ConnectionAvailability.DISCONNECTED

    fun connectionLost() {
        if (state.offline || viewModel.state.value.authenticated) return
        state.offline = true
        state.retryPending = false
        // Invalida o tíquete desta requisição/PKCE; não encerra a sessão nem limpa o carrinho do usuário.
        viewModel.authorizationCancelled()
        if (state.browserPending) returnBrowserToHost()
    }

    fun returnBrowserToHost() {
        state.cancelActivityReturn?.invoke()
        state.cancelActivityReturn = if (returnFromBrowser != null) {
            returnFromBrowser()
            null
        } else {
            returnToLoginActivity(activity) {
                state.browserPending && !viewModel.state.value.authenticated && (state.offline || state.retryPending)
            }
        }
    }
}
