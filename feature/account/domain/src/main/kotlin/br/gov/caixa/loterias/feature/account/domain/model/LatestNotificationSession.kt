package br.gov.caixa.loterias.feature.account.domain.model

interface LatestNotificationSession {
    fun capture(notification: AccountNotification): AuthenticatedSessionIdentity?
    /** Limpa somente a mesma instância, sem sobrescrever uma notificação, carrinho ou sessão mais recente. */
    fun clear(identity: AuthenticatedSessionIdentity, notification: AccountNotification): Boolean
}
