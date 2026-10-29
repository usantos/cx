package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.LatestNotificationReadRemoteDataSource
import br.gov.caixa.loterias.feature.account.data.repository.LatestNotificationReadRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.LatestNotificationReadRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.domain.repository.LatestNotificationReadRepository
import br.gov.caixa.loterias.feature.account.domain.model.LatestNotificationSession
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotification
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LatestNotificationModule {
    @Provides
    fun latestNotificationSession(tokenStore: SessionTokenStore): LatestNotificationSession = tokenStore

    @Provides
    @Singleton
    fun latestNotificationReadDataSource(
        provider: NotificationReadApiProvider,
        sessions: AccountVerificationSessionBinding,
    ): LatestNotificationReadRemoteDataSource = LatestNotificationReadRemoteDataSourceImpl(provider, sessions)

    @Provides
    @Singleton
    fun latestNotificationReadRepository(remote: LatestNotificationReadRemoteDataSource): LatestNotificationReadRepository =
        LatestNotificationReadRepositoryImpl(remote)

    @Provides
    fun readLatestNotification(
        session: LatestNotificationSession,
        remote: LatestNotificationReadRepository,
    ): ReadLatestNotificationUseCase = ReadLatestNotification(session, remote)
}
