package br.gov.caixa.loterias.feature.retail.di

import br.gov.caixa.loterias.feature.retail.data.datasource.remote.PrizeRedemptionRemoteDataSource
import br.gov.caixa.loterias.feature.retail.data.repository.RetailRepositoryImpl
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailRemoteDataSource
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemption
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKey
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceipt
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceipt
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailers
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.repository.PrizeRedemptionRepository
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrize
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.domain.repository.RetailRepository
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailers
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavorite
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RetailModule {
    @Provides @Singleton
    fun retailCountrySource(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
        dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers,
    ): br.gov.caixa.loterias.feature.retail.domain.port.RetailCountrySource =
        br.gov.caixa.loterias.feature.retail.data.location.AndroidRetailCountrySource(context, dispatchers)

    @Provides
    fun checkRetailLocation(
        source: br.gov.caixa.loterias.feature.retail.domain.port.RetailCountrySource,
    ): br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocationUseCase =
        br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocation(source)

    @Provides @Singleton
    fun retrofitRetailDataSource(
        provider: BackendApiProvider,
        prizeProvider: br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider,
    ) = RetailRemoteDataSourceImpl(provider, prizeProvider)

    @Provides
    fun retailDataSource(source: RetailRemoteDataSourceImpl): RetailRemoteDataSource = source

    @Provides
    fun prizeRedemptionDataSource(source: RetailRemoteDataSourceImpl): PrizeRedemptionRemoteDataSource = source

    @Provides @Singleton
    fun retailRepository(
        source: RetailRemoteDataSource,
        prizeSource: PrizeRedemptionRemoteDataSource,
    ): RetailRepositoryImpl = RetailRepositoryImpl(source, prizeSource)

    @Provides fun retailContract(repository: RetailRepositoryImpl): RetailRepository = repository

    @Provides fun prizeRedemptionContract(repository: RetailRepositoryImpl): PrizeRedemptionRepository = repository

    @Provides fun searchRetailers(repository: RetailRepository): SearchRetailersUseCase = SearchRetailers(repository)

    @Provides fun favoriteRetailers(repository: RetailRepository): GetFavoriteRetailersUseCase = GetFavoriteRetailers(repository)

    @Provides fun setRetailerFavorite(repository: RetailRepository): SetRetailerFavoriteUseCase = SetRetailerFavorite(repository)

    @Provides fun prizeDetails(repository: PrizeRedemptionRepository): GetPrizeDetailsUseCase = GetPrizeDetails(repository)

    @Provides fun lotteryRedemptionCode(repository: PrizeRedemptionRepository): GenerateLotteryRedemptionCodeUseCase =
        GenerateLotteryRedemptionCode(repository)

    @Provides fun consultPixRedemptionKey(repository: PrizeRedemptionRepository): ConsultPixRedemptionKeyUseCase =
        ConsultPixRedemptionKey(repository)

    @Provides fun redeemPrize(repository: PrizeRedemptionRepository): RedeemPrizeUseCase = RedeemPrize(repository)

    @Provides fun cancelPixRedemption(repository: PrizeRedemptionRepository): CancelPixRedemptionUseCase =
        CancelPixRedemption(repository)

    @Provides fun downloadBetReceipt(repository: PrizeRedemptionRepository): DownloadBetReceiptUseCase =
        DownloadBetReceipt(repository)

    @Provides fun downloadPrizeReceipt(repository: PrizeRedemptionRepository): DownloadPrizeReceiptUseCase =
        DownloadPrizeReceipt(repository)
    @Provides fun mercadoPagoRegistrationContract(repository: RetailRepositoryImpl): br.gov.caixa.loterias.feature.payment.domain.repository.MercadoPagoRegistrationRepository = repository
}
