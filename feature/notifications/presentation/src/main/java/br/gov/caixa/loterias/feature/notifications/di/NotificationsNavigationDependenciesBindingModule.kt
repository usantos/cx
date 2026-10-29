package br.gov.caixa.loterias.feature.notifications.di

import br.gov.caixa.loterias.feature.notifications.navigation.NotificationsNavigationDependencies
import br.gov.caixa.loterias.feature.notifications.navigation.NotificationsNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindNotificationsNavigationDependenciesDependencies(
        implementation: NotificationsNavigationDependenciesAdapter,
    ): NotificationsNavigationDependencies
}
