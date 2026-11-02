package br.gov.caixa.loterias.feature.account.domain.port

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

/** Alterações de sessão usadas pela apresentação sem carregar objetos do navegador ou Activity. */
interface SessionAuthentication {
    fun cancelAuthorization()
    fun logout()

    /** Revoga apenas a identidade indicada, incluindo credenciais lembradas, sob a mesma exclusão da troca de sessão. */
    fun logout(identity: AuthenticatedSessionIdentity): Boolean = false

    fun isCurrent(identity: AuthenticatedSessionIdentity): Boolean = false
}
