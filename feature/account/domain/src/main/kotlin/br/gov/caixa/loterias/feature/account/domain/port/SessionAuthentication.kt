package br.gov.caixa.loterias.feature.account.domain.port

/** Alterações de sessão usadas pela apresentação sem carregar objetos do navegador ou Activity. */
interface SessionAuthentication {
    fun cancelAuthorization()
    fun logout()
}
