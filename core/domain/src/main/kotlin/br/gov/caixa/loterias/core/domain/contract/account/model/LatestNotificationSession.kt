package br.gov.caixa.loterias.core.domain.contract.account.model

import br.gov.caixa.loterias.core.domain.contract.account.model.AccountNotification
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity

interface LatestNotificationSession {
    fun capture(notification: AccountNotification): AuthenticatedSessionIdentity?
    /** Limpa somente a mesma instância, sem sobrescrever uma notificação, carrinho ou sessão mais recente. */
    fun clear(identity: AuthenticatedSessionIdentity, notification: AccountNotification): Boolean
}
