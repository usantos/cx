package br.gov.caixa.loterias.feature.responsiblegaming.di

import br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSourceImpl
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
    fun remoteAdapter(api: LoteriasApiProvider): ResponsibleGamingRemoteDataSourceImpl = ResponsibleGamingRemoteDataSourceImpl(api)
}
