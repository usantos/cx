package br.gov.caixa.loterias.feature.marketplace.di

import br.gov.caixa.loterias.feature.account.domain.repository.AccountMunicipalityRepository
import br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSource
import br.gov.caixa.loterias.feature.marketplace.data.location.AccountMarketplaceLocationProvider
import br.gov.caixa.loterias.feature.marketplace.data.repository.MarketplaceRepositoryImpl
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceDetailRepository
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplacePurchaseRepository
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCart
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetail
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplace
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarketplaceModule {
    @Provides
    fun marketplaceSpecialContests(
        configuration: br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository,
    ): br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase =
        GetMarketplaceSpecialContests(configuration)

    @Provides
    fun marketplaceDataSource(
        source: br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSourceImpl,
    ): MarketplaceRemoteDataSource = source

    @Provides @Singleton
    fun marketplaceRepository(source: MarketplaceRemoteDataSource) = MarketplaceRepositoryImpl(source)

    @Provides
    fun marketplaceContract(repository: MarketplaceRepositoryImpl): MarketplaceRepository = repository

    @Provides
    fun marketplaceDetailContract(repository: MarketplaceRepositoryImpl): MarketplaceDetailRepository = repository

    @Provides
    fun marketplacePurchaseContract(repository: MarketplaceRepositoryImpl): MarketplacePurchaseRepository = repository

    @Provides
    fun searchMarketplace(
        repository: MarketplaceRepository,
        account: AccountMunicipalityRepository,
        sessions: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider,
    ): SearchMarketplaceUseCase = SearchMarketplace(repository, AccountMarketplaceLocationProvider(account, sessions))

    @Provides
    fun marketplaceDetail(repository: MarketplaceDetailRepository): GetMarketplaceDetailUseCase = GetMarketplaceDetail(repository)

    @Provides
    fun addMarketplacePoolToCart(repository: MarketplacePurchaseRepository): AddMarketplacePoolToCartUseCase =
        AddMarketplacePoolToCart(repository)
}
