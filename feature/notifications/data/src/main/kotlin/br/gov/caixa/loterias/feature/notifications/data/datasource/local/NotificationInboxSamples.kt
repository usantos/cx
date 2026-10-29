package br.gov.caixa.loterias.feature.notifications.data.datasource.local

import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory

/**
 * Catálogo original de MensagemPushModel.getAllMensagensMock, com os IDs locais únicos da migração.
 * Estas mensagens demonstrativas não são histórico FCM nem IDs de notificação do servidor.
 */
internal fun legacyNotificationInboxSamples() = listOf(
    AppNotification(
        1L,
        NotificationCategory.PAYMENTS_REDEMPTIONS,
        "Pagamentos",
        "Aproveite e aposte ! este item dos pagamentos , aproveite aposte , aproveite aposte, aproveite aposte, aproveite aposte",
        "hoje",
        false
    ),
    AppNotification(
        2L,
        NotificationCategory.LUCK_REMINDERS,
        "Lembretes de sorte",
        "Aproveite e aposte ! , estou continuando a mensagem para ver como fica o aproveite e aposte ",
        "ontem",
        false
    ),
    AppNotification(
        3L,
        NotificationCategory.RESULTS_AVAILABLE,
        "Resultados disponiveis",
        "Aproveite e aposte ! esta mensagem fala sobre resultados disponiveis, aqui voce vai ganhar link para " +
            "resultados disponiveis, aqui vai botao de resultados",
        "25 Mar",
        false
    ),
    AppNotification(
        4L,
        NotificationCategory.TIPS_NEWS,
        "Dicas e Novidades",
        "Aproveite e aposte ! , estou alogando a mensagem para ver como fica o texto até o final ",
        "03 Mar",
        false
    ),
    AppNotification(
        5L,
        NotificationCategory.SECURITY_ALERTS,
        "Alerta de segurança",
        "Conteudo de alerta de segurança da mensagem 5, esta msg fala sobre segurança no app, informacoes de " +
            "como fazer a seguranca no seu celular e dar tudo o que puder",
        "01 Fev",
        false
    )
)
