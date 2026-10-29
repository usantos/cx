package br.gov.caixa.loterias.core.domain.environment.model

/** Configuração imutável compartilhada pelos serviços e pela autenticação durante a vida de um processo. */
data class EffectiveEnvironment(
    val apiBaseUrl: String,
    val ssoAuthUrl: String,
    val ssoTokenUrl: String,
    val ssoLogoutUrl: String,
    val ssoAccountUrl: String,
    val ssoClientId: String,
    val ssoRedirectUri: String,
    val mercadoPagoBaseUrl: String,
    val recargaPayBaseUrl: String,
    val queueAccount: String,
    val queueSystem: String,
) {
    val sessionNamespace: String get() = apiBaseUrl

}
