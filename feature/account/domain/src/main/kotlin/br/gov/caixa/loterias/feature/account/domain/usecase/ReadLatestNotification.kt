package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.feature.account.domain.model.LatestNotificationSession
import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult
import br.gov.caixa.loterias.feature.account.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.domain.repository.LatestNotificationReadRepository

class ReadLatestNotification(private val session: LatestNotificationSession, private val remote: LatestNotificationReadRepository) :
    ReadLatestNotificationUseCase {
    override suspend fun invoke(notification: AccountNotification) = appResultOf {
        val identity = checkNotNull(session.capture(notification)) { messageText("notificacao_de_outra_sessao_7c7fdc") }
        // O legado dispensa localmente, mas não faz requisição nem limpeza quando a notificação não possui identificador.
        notification.id?.let { id ->
            require(id > 0) { messageText("identificacao_de_notificacao_inv_7398ea") }
            val result = remote.markRead(identity, id)
            if (result is NotificationReadResult.Rejected) return@appResultOf result
            check(session.clear(identity, notification)) { messageText("notificacao_ou_sessao_substituid_019255") }
        }
        NotificationReadResult.Confirmed
    }
}
