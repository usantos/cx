package br.gov.caixa.loterias.feature.notifications.presentation.state

sealed interface NotificationText {
    data class Resource(val id: Int) : NotificationText
    data class Plain(val value: String) : NotificationText
}
