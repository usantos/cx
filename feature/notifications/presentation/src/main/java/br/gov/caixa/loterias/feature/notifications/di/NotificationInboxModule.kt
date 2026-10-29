package br.gov.caixa.loterias.feature.notifications.di

import br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.notifications.data.datasource.remote.NotificationInboxRemoteDataSource
import br.gov.caixa.loterias.feature.notifications.data.datasource.remote.NotificationInboxRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.notifications.data.datasource.remote.NotificationInboxSessionBinding
import br.gov.caixa.loterias.feature.notifications.data.repository.NotificationInboxRepositoryImpl
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationInboxRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationInboxModule {
    @Provides @Singleton
    fun inboxSessions(accountSessions: AccountVerificationSessionBinding): NotificationInboxSessionBinding =
        NotificationInboxSessionBinding(accountSessions::bind)

    @Provides @Singleton
    fun inboxRemote(provider: NotificationInboxApiProvider, sessions: NotificationInboxSessionBinding): NotificationInboxRemoteDataSource =
        NotificationInboxRemoteDataSourceImpl(provider, sessions)

    @Provides @Singleton
    fun inboxRepository(remote: NotificationInboxRemoteDataSource): NotificationInboxRepository = NotificationInboxRepositoryImpl(remote)
}
