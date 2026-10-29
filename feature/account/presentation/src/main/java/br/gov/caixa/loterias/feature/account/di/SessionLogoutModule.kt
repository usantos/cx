package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.repository.SessionSignOutRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.SessionSignOutRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.SessionSignOutRemoteDataSource
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.SessionSignOutRepository
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSession
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.core.network.api.SessionLogoutApiProvider
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SessionLogoutModule {
    @Provides @Singleton fun provider() = SessionLogoutApiProvider()

    @Provides @Singleton fun remote(provider: SessionLogoutApiProvider, sessions: AccountVerificationSessionBinding): SessionSignOutRemoteDataSource =
        SessionSignOutRemoteDataSourceImpl(provider, sessions)

    @Provides @Singleton fun repository(remote: SessionSignOutRemoteDataSource): SessionSignOutRepository = SessionSignOutRepositoryImpl(remote)

    @Provides @Singleton fun signOut(identities: AuthenticatedSessionIdentityProvider, repository: SessionSignOutRepository): SignOutSessionUseCase =
        SignOutSession(identities, repository)
}
