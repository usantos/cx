package br.gov.caixa.loterias.feature.account.presentation.route

import android.content.Intent
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

/** Lança a tentativa escolhida, preservando o fallback e a intenção da repetição. */
internal class LoginAuthorizationLaunch(
    private val state: LoginAuthorizationState,
    private val viewModel: LoginViewModel,
    private val authentication: AuthenticationGateway,
    private val connection: LoginAuthorizationConnection,
    private val biometric: LoginBiometricAuthorization,
    private val launchBrowser: (Intent) -> Unit,
) {
    fun authorize(forceBrowser: Boolean = false, switchUser: Boolean = false) {
        state.switchingUser = switchUser
        if (connection.disconnected()) {
            connection.connectionLost()
            return
        }
        if (!viewModel.beginAuthorization()) return
        state.offline = false
        try {
            if (!forceBrowser && !switchUser && viewModel.canUseBiometricLogin()) {
                biometric.launch()
            } else {
                state.preferBrowserOnRetry = true
                state.browserPending = true
                launchBrowser(
                    if (switchUser) {
                        authentication.createAccountSwitchAuthorizationIntent()
                    } else {
                        authentication.createAuthorizationIntent()
                    }
                )
            }
        } catch (_: Exception) {
            state.browserPending = false
            if (connection.disconnected()) {
                connection.connectionLost()
            } else if (!forceBrowser && !switchUser && viewModel.canUseBiometricLogin()) {
                viewModel.biometricAuthorizationFailed()
            } else {
                viewModel.authorizationLaunchFailed()
            }
        }
    }

    fun retry() {
        if (!connection.disconnected()) {
            if (state.browserPending) {
                state.retryPending = true
                connection.returnBrowserToHost()
            } else {
                authorize(forceBrowser = state.preferBrowserOnRetry, switchUser = state.switchingUser)
            }
        }
    }
}
