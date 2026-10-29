package br.gov.caixa.loterias.feature.notifications.navigation

import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel

fun interface NotificationsViewModelProvider {
    fun create(): NotificationsViewModel
}
