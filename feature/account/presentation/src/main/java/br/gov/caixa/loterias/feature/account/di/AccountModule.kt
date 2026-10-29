package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource
import br.gov.caixa.loterias.feature.account.data.repository.AccountRepositoryImpl
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfile
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimit
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccount
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCode
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalData
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccess
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {
    @Provides
    fun accountDataSource(source: br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSourceImpl): AccountRemoteDataSource = source

    @Provides @Singleton
    fun postLoginAccountDataSource(
        provider: br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider,
        sessions: br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding,
    ): br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSource =
        br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSourceImpl(provider, sessions)

    @Provides @Singleton
    fun accountRepository(source: AccountRemoteDataSource): AccountRepository = AccountRepositoryImpl(source)

    @Provides
    fun accountProfile(repository: AccountRepository): GetAccountProfileUseCase = GetAccountProfile(repository)

    @Provides
    fun resolveZipCode(repository: AccountRepository): ResolveZipCodeUseCase = ResolveZipCode(repository)

    @Provides
    fun updatePersonalData(repository: AccountRepository): UpdatePersonalDataUseCase = UpdatePersonalData(repository)

    @Provides
    fun registerAccount(repository: AccountRepository): RegisterAccountUseCase = RegisterAccount(repository)

    @Provides
    fun verifyAccountAccess(repository: AccountRepository): VerifyAccountAccessUseCase = VerifyAccountAccess(repository)

    @Provides @Singleton
    fun blockedAccountVerification(provider: BackendApiProvider):
        br.gov.caixa.loterias.feature.account.domain.repository.BlockedAccountVerificationRepository =
        br.gov.caixa.loterias.feature.account.data.repository.BlockedAccountVerificationRepositoryImpl(provider)

    @Provides @Singleton
    fun verifyPostLoginAccess(
        blocked: br.gov.caixa.loterias.feature.account.domain.repository.BlockedAccountVerificationRepository,
        account: br.gov.caixa.loterias.feature.account.domain.repository.PostLoginAccountRepository,
        sessions: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider,
    ): br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase =
        br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccess(blocked, account, sessions)

    @Provides @Singleton
    fun postLoginAccountRepository(remote: br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSource,
        content: br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository,
        transfer: br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository,
        sessions: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider,
    ): br.gov.caixa.loterias.feature.account.domain.repository.PostLoginAccountRepository =
        br.gov.caixa.loterias.feature.account.data.repository.PostLoginAccountRepositoryImpl(remote, content, transfer, sessions::isCurrent)

    @Provides
    fun recalculateAuthorizedLimit(repository: AccountRepository): RecalculateAuthorizedLimitUseCase =
        RecalculateAuthorizedLimit(repository)
}
