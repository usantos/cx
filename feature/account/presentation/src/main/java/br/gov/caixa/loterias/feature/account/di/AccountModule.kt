package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSource
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSourceImpl
import br.gov.caixa.loterias.feature.account.data.repository.AccountRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.repository.BlockedAccountVerificationRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.repository.PostLoginAccountRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.repository.SessionAccountRepositoryFactory
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepositoryFactory
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.repository.BlockedAccountVerificationRepository
import br.gov.caixa.loterias.feature.account.domain.repository.PostLoginAccountRepository
import br.gov.caixa.loterias.feature.account.domain.usecase.AccountFormUseCasesFactory
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
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccess
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {
    @Provides
    fun accountDataSource(source: AccountRemoteDataSourceImpl): AccountRemoteDataSource = source

    @Provides @Singleton
    fun postLoginAccountDataSource(
        provider: AccountVerificationBackendProvider,
        sessions: AccountVerificationSessionBinding,
    ): PostLoginAccountDataSource = PostLoginAccountDataSourceImpl(provider, sessions)

    @Provides @Singleton
    fun accountRepository(source: AccountRemoteDataSource): AccountRepository = AccountRepositoryImpl(source)

    @Provides @Singleton
    fun accountRepositoryFactory(provider: LoteriasApiProvider, sessions: AccountVerificationSessionBinding): AccountRepositoryFactory =
        SessionAccountRepositoryFactory(provider, sessions)

    @Provides
    fun accountForms(repositories: AccountRepositoryFactory): AccountFormUseCasesFactory = AccountFormUseCasesFactory(repositories)

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
    fun blockedAccountVerification(provider: BackendApiProvider): BlockedAccountVerificationRepository =
        BlockedAccountVerificationRepositoryImpl(provider)

    @Provides @Singleton
    fun verifyPostLoginAccess(
        blocked: BlockedAccountVerificationRepository,
        account: PostLoginAccountRepository,
        sessions: AuthenticatedSessionIdentityProvider,
    ): VerifyPostLoginAccessUseCase = VerifyPostLoginAccess(blocked, account, sessions)

    @Provides @Singleton
    fun postLoginAccountRepository(
        remote: PostLoginAccountDataSource,
        content: AuthenticatedSessionContentRepository,
        transfer: CartTransferRepository,
        sessions: AuthenticatedSessionIdentityProvider,
    ): PostLoginAccountRepository = PostLoginAccountRepositoryImpl(remote, content, transfer, sessions::isCurrent)

    @Provides
    fun recalculateAuthorizedLimit(repository: AccountRepository): RecalculateAuthorizedLimitUseCase =
        RecalculateAuthorizedLimit(repository)
}
