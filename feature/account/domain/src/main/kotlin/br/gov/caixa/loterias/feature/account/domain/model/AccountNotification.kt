package br.gov.caixa.loterias.feature.account.domain.model

data class AccountNotification(
    val id: Long?,
    val title: String?,
    val content: String?,
    /** Imagens de notificações legadas são base64, e não URLs remotas. */
    val imageBase64: String?,
    val link: String?,
) {
    override fun toString() = "AccountNotification(redacted)"
}
