package br.gov.caixa.loterias.feature.notifications.navigation

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface NotificationsNavigationDependencies {
    val notificationViewModelFactory: br.gov.caixa.loterias.feature.notifications.navigation.NotificationsViewModelProvider
    val centralNotificationsFactory: br.gov.caixa.loterias.feature.notifications.di.CentralNotificationsViewModelFactory
}
