package br.gov.caixa.loterias.feature.betting.di

import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSourceImpl
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
    fun remoteAdapter(api: LoteriasApiProvider, legacy: BackendApiProvider): BettingRemoteDataSourceImpl = BettingRemoteDataSourceImpl(api, legacy)
}
