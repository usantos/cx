package br.gov.caixa.loterias.feature.notifications.data.datasource.local

import br.gov.caixa.loterias.feature.notifications.data.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference

/** Preferências visuais locais, acessadas somente sob o monitor do repository. */
internal class LocalNotificationPreferences {
    private val enabledCategories = mutableSetOf<NotificationCategory>()
    private var allNotificationsEnabled = false

    fun snapshot(): List<NotificationPreference> = listOf(
        NotificationPreference(null, messageText("all_title"), "", allNotificationsEnabled),
        NotificationPreference(
            NotificationCategory.PAYMENTS_REDEMPTIONS,
            messageText("payments_title"),
            messageText("payments_description"),
            NotificationCategory.PAYMENTS_REDEMPTIONS in enabledCategories
        ),
        NotificationPreference(
            NotificationCategory.LUCK_REMINDERS,
            messageText("luck_title"),
            messageText("luck_description"),
            NotificationCategory.LUCK_REMINDERS in enabledCategories
        ),
        NotificationPreference(
            NotificationCategory.RESULTS_AVAILABLE,
            messageText("results_title"),
            messageText("results_description"),
            NotificationCategory.RESULTS_AVAILABLE in enabledCategories
        ),
        NotificationPreference(
            NotificationCategory.TIPS_NEWS,
            messageText("news_title"),
            messageText("news_description"),
            NotificationCategory.TIPS_NEWS in enabledCategories
        ),
        NotificationPreference(
            NotificationCategory.SECURITY_ALERTS,
            messageText("security_title"),
            messageText("security_description"),
            NotificationCategory.SECURITY_ALERTS in enabledCategories
        )
    )

    fun set(category: NotificationCategory?, enabled: Boolean) {
        if (category == null) {
            // A tela legada tem seletores visuais independentes, sem API de assinatura ou controle mestre.
            allNotificationsEnabled = enabled
        } else if (enabled) {
            enabledCategories += category
        } else {
            enabledCategories -= category
        }
    }
}
