package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.repository.UnreadNotificationCountRepositoryImpl
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase
import br.gov.caixa.loterias.feature.account.domain.repository.UnreadNotificationCountRepository
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import dagger.Provides
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UnreadNotificationsModule {
    @Provides
    fun unreadNotificationRepository(
        provider: NotificationReadApiProvider,
        sessions: AccountVerificationSessionBinding,
    ): UnreadNotificationCountRepository = UnreadNotificationCountRepositoryImpl(provider, sessions)

    @Provides
    fun unreadNotificationCount(
        repository: UnreadNotificationCountRepository,
        sessions: AuthenticatedSessionIdentityProvider,
    ): GetUnreadNotificationCountUseCase = GetUnreadNotificationCountUseCase(repository, sessions)
}
