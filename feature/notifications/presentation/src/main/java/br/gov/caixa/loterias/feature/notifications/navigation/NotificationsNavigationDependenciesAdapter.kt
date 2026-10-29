package br.gov.caixa.loterias.feature.notifications.navigation

import br.gov.caixa.loterias.feature.notifications.di.CentralNotificationsViewModelFactory
import javax.inject.Inject

class NotificationsNavigationDependenciesAdapter @Inject constructor(
    private val factory: br.gov.caixa.loterias.feature.notifications.di.NotificationViewModelFactory,
    override val centralNotificationsFactory: CentralNotificationsViewModelFactory,
) : NotificationsNavigationDependencies {
    override val notificationViewModelFactory: NotificationsViewModelProvider get() = factory
}
