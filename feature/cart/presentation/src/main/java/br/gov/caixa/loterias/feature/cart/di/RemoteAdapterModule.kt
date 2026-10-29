package br.gov.caixa.loterias.feature.cart.di

import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl
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
    fun remoteAdapter(api: LoteriasApiProvider): CartRemoteDataSourceImpl = CartRemoteDataSourceImpl(api)

    @Provides
    fun boundCartRemoval(source: CartRemoteDataSourceImpl): br.gov.caixa.loterias.feature.cart.data.datasource.remote.BoundCartRemovalRemoteDataSource = source
}
