package br.gov.caixa.loterias.feature.results.di

import br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsRemoteDataSourceImpl
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteAdapterModule {
    @Provides @Singleton
    fun remoteAdapter(api: LoteriasApiProvider, legacy: BackendApiProvider): ResultsRemoteDataSourceImpl = ResultsRemoteDataSourceImpl(api, legacy)
}
