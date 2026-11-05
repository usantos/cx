package br.gov.caixa.loterias.feature.notifications.data.datasource.remote

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession

/** Composta na raiz com a mesma sessão de conta capturada. */
fun interface NotificationInboxSessionBinding {
    fun bind(identity: AuthenticatedSessionIdentity): AccountVerificationSession
}
