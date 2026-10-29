package br.gov.caixa.loterias.feature.support.di

import br.gov.caixa.loterias.feature.support.data.datasource.remote.SupportRemoteDataSourceImpl
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
    fun remoteAdapter(api: LoteriasApiProvider): SupportRemoteDataSourceImpl = SupportRemoteDataSourceImpl(api)
}
