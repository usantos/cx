package br.gov.caixa.loterias.feature.marketplace.di

import br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSourceImpl
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteAdapterModule {
    @Provides @Singleton
    fun remoteAdapter(api: LoteriasApiProvider): MarketplaceRemoteDataSourceImpl = MarketplaceRemoteDataSourceImpl(api)
}
