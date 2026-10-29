package br.gov.caixa.loterias.feature.notifications.domain.model

data class NotificationPreference(val category: NotificationCategory?, val title: String, val description: String, val enabled: Boolean)
