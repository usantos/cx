package br.gov.caixa.loterias.apostas.di

import br.gov.caixa.loterias.core.data.AccountVerificationSessionBinding
import br.gov.caixa.loterias.core.data.RemoteSessionSignOutRepository
import br.gov.caixa.loterias.core.data.RetrofitSessionSignOutRemoteDataSource
import br.gov.caixa.loterias.core.data.SessionSignOutRemoteDataSource
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.core.domain.account.SessionSignOutRepository
import br.gov.caixa.loterias.core.domain.account.SignOutSession
import br.gov.caixa.loterias.core.domain.account.SignOutSessionUseCase
import br.gov.caixa.loterias.core.network.SessionLogoutApiProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionLogoutModule {
    @Provides @Singleton fun provider() = SessionLogoutApiProvider()
    @Provides @Singleton fun remote(provider: SessionLogoutApiProvider, sessions: AccountVerificationSessionBinding): SessionSignOutRemoteDataSource =
        RetrofitSessionSignOutRemoteDataSource(provider, sessions)
    @Provides @Singleton fun repository(remote: SessionSignOutRemoteDataSource): SessionSignOutRepository = RemoteSessionSignOutRepository(remote)
    @Provides @Singleton fun signOut(identities: AuthenticatedSessionIdentityProvider, repository: SessionSignOutRepository): SignOutSessionUseCase =
        SignOutSession(identities, repository)
}
