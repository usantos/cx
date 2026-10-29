package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource
import br.gov.caixa.loterias.feature.account.data.repository.AccountMunicipalityRepositoryImpl
import br.gov.caixa.loterias.feature.account.domain.repository.AccountMunicipalityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AccountMunicipalityModule {
    @Provides
    fun accountMunicipalityRepository(remote: AccountRemoteDataSource): AccountMunicipalityRepository =
        AccountMunicipalityRepositoryImpl(remote)
}
