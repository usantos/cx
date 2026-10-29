package br.gov.caixa.loterias.feature.notifications.data.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.data.datasource.remote.NotificationInboxRemoteDataSource
import br.gov.caixa.loterias.feature.notifications.data.dto.NotificationInboxEnvelopeDto
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationInbox
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationInboxRepository
import com.google.gson.JsonElement
import java.io.IOException

class NotificationInboxRepositoryImpl(private val remote: NotificationInboxRemoteDataSource) : NotificationInboxRepository {
    override suspend fun load(identity: AuthenticatedSessionIdentity): NotificationInbox {
        val (all, unread) = remote.load(identity)
        return NotificationInbox(decode(all), decode(unread))
    }

    override suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long) = remote.markRead(identity, id)
    override suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long) = remote.remove(identity, id)
    override suspend fun clear(identity: AuthenticatedSessionIdentity) = remote.clear(identity)

    private fun decode(envelope: NotificationInboxEnvelopeDto): List<InboxNotification> {
        val payload = envelope.payload?.takeIf { it.isJsonArray }?.asJsonArray ?: invalid()
        val notifications = payload.map { element ->
            val item = element.takeIf { it.isJsonObject }?.asJsonObject ?: invalid()
            val id = item.get("id")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                ?.let { runCatching { it.asBigDecimal.longValueExact() }.getOrNull() }
                ?.takeIf { it > 0 } ?: invalid()
            fun text(name: String): String = item.get(name)
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }?.asString ?: invalid()
            InboxNotification(id, text("titulo"), text("conteudo"), item.get("dataVisualizacao").present())
        }
        if (notifications.map { it.id }.distinct().size != notifications.size) invalid()
        return notifications
    }

    private fun JsonElement?.present() = this != null && !isJsonNull
    private fun invalid(): Nothing = throw IOException("Resposta do histórico de notificações inválida")
}
