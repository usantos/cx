package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.notifications.AppNotification
import br.gov.caixa.loterias.core.domain.notifications.NotificationCategory
import br.gov.caixa.loterias.core.domain.notifications.NotificationPreference
import br.gov.caixa.loterias.core.domain.notifications.NotificationRepository

/**
 * Mirrors MensagemPushModel's local-only sample inbox. IDs are local keys, never backend IDs.
 * Create one instance per destination ViewModel, matching a fresh legacy screen instance.
 * The authenticated latest-notification popup uses a separate repository and protocol.
 */
class LocalNotificationRepository : NotificationRepository {
    private val messages = legacyMessages().toMutableList()
    private val enabledCategories = mutableSetOf<NotificationCategory>()
    private var allNotificationsEnabled = false

    override suspend fun getNotifications(): List<AppNotification> = synchronized(this) { messages.toList() }

    override suspend fun markAsRead(id: Long) {
        synchronized(this) {
            val index = messages.indexOfFirst { it.id == id }
            if (index >= 0) messages[index] = messages[index].copy(isRead = true)
        }
    }

    override suspend fun delete(id: Long) {
        synchronized(this) { messages.removeAll { it.id == id } }
    }

    override suspend fun deleteAll() {
        synchronized(this) { messages.clear() }
    }

    override suspend fun markAllAsRead() {
        synchronized(this) {
            messages.replaceAll { it.copy(isRead = true) }
        }
    }

    override suspend fun getPreferences(): List<NotificationPreference> = synchronized(this) {
        listOf(
            NotificationPreference(null, "Todas as notificações", "", allNotificationsEnabled),
            NotificationPreference(
                NotificationCategory.PAYMENTS_REDEMPTIONS,
                "Pagamentos e Resgates",
                "Confirmação e pendências de pagamentos e confirmação de resgates realizados",
                NotificationCategory.PAYMENTS_REDEMPTIONS in enabledCategories,
            ),
            NotificationPreference(
                NotificationCategory.LUCK_REMINDERS,
                "Lembretes da Sorte",
                "Avisos quando houver oportunidades de ganhar mais (concurso especial, prêmio acumulado)",
                NotificationCategory.LUCK_REMINDERS in enabledCategories,
            ),
            NotificationPreference(
                NotificationCategory.RESULTS_AVAILABLE,
                "Resultados Disponíveis",
                "Avisos quando resultados das suas apostas estiverem disponíveis para conferência",
                NotificationCategory.RESULTS_AVAILABLE in enabledCategories,
            ),
            NotificationPreference(
                NotificationCategory.TIPS_NEWS,
                "Dicas e Novidades",
                "Novos produtos ou serviços no App",
                NotificationCategory.TIPS_NEWS in enabledCategories,
            ),
            NotificationPreference(
                NotificationCategory.SECURITY_ALERTS,
                "Alertas de Segurança ou Disponibilidade de Serviços",
                "Avisos para proteger você e melhorar sua experiência",
                NotificationCategory.SECURITY_ALERTS in enabledCategories,
            ),
        )
    }

    override suspend fun setPreference(category: NotificationCategory?, enabled: Boolean) {
        synchronized(this) {
            if (category == null) {
                // The legacy screen has independent visual switches, not a subscription/master API.
                allNotificationsEnabled = enabled
            } else if (enabled) {
                enabledCategories += category
            } else {
                enabledCategories -= category
            }
        }
    }
}

private fun legacyMessages() = listOf(
    AppNotification(
        1L,
        NotificationCategory.PAYMENTS_REDEMPTIONS,
        "Pagamentos",
        "Aproveite e aposte ! este item dos pagamentos , aproveite aposte , aproveite aposte, aproveite aposte, aproveite aposte",
        "hoje",
        false,
    ),
    AppNotification(
        2L,
        NotificationCategory.LUCK_REMINDERS,
        "Lembretes de sorte",
        "Aproveite e aposte ! , estou continuando a mensagem para ver como fica o aproveite e aposte ",
        "ontem",
        false,
    ),
    AppNotification(
        3L,
        NotificationCategory.RESULTS_AVAILABLE,
        "Resultados disponiveis",
        "Aproveite e aposte ! esta mensagem fala sobre resultados disponiveis, aqui voce vai ganhar link para resultados disponiveis, aqui vai botao de resultados",
        "25 Mar",
        false,
    ),
    AppNotification(
        4L,
        NotificationCategory.TIPS_NEWS,
        "Dicas e Novidades",
        "Aproveite e aposte ! , estou alogando a mensagem para ver como fica o texto até o final ",
        "03 Mar",
        false,
    ),
    AppNotification(
        5L,
        NotificationCategory.SECURITY_ALERTS,
        "Alerta de segurança",
        "Conteudo de alerta de segurança da mensagem 5, esta msg fala sobre segurança no app, informacoes de como fazer a seguranca no seu celular e dar tudo o que puder",
        "01 Fev",
        false,
    ),
)
