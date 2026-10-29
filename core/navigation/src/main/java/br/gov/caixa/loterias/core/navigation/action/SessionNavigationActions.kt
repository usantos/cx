package br.gov.caixa.loterias.core.navigation.action

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.navigation.state.AppSessionState

/** Funções de retorno pertencentes à sessão; grafos dos destinos não adquirem nem retêm credenciais. */
interface SessionNavigationActions {
    val state: AppSessionState
    val identity: AuthenticatedSessionIdentity?
    val privateEntryKey: String
    val isPrivateSessionCurrent: () -> Boolean
    val onLogout: () -> Unit
    val requestVisitorLogin: (String) -> Unit
    val recheckAccountCompletion: () -> Unit
    val onAuthenticated: (String) -> Unit
    val onTryNow: () -> Unit
}
