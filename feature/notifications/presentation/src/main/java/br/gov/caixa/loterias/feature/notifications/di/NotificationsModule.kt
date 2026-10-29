package br.gov.caixa.loterias.feature.notifications.di

import br.gov.caixa.loterias.feature.notifications.data.repository.NotificationRepositoryImpl
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    // Caixa local de exemplo: nova instância do destino, sem espelhar a leitura remota de IDs sintéticos.
    @Provides
    fun notificationRepository(): NotificationRepository = NotificationRepositoryImpl()
}
