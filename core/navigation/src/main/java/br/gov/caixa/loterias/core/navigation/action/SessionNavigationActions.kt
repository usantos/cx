package br.gov.caixa.loterias.core.navigation.action

import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

/** Funções de retorno pertencentes à sessão; grafos dos destinos não adquirem nem retêm credenciais. */
interface SessionNavigationActions {
    val purchaseRecovery: br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession? get() = null
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
