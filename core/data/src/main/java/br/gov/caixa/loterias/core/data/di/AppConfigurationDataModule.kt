package br.gov.caixa.loterias.core.data.di

import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource
import br.gov.caixa.loterias.core.data.repository.AppConfigurationRepositoryImpl
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSourceImpl
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupported
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/** Provedores pertencentes à camada de dados para configurações globais do app buscadas no servidor CAIXA. */
@Module
object AppConfigurationDataModule {
    @Provides
    fun configurationDataSource(api: LoteriasApiProvider): AppConfigurationRemoteDataSource =
        AppConfigurationRemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun configurationRepository(source: AppConfigurationRemoteDataSource): AppConfigurationRepository =
        AppConfigurationRepositoryImpl(source)

    @Provides
    fun isVersionSupported(repository: AppConfigurationRepository): IsVersionSupportedUseCase =
        IsVersionSupported(repository)

    @Provides
    fun filterParameters(repository: AppConfigurationRepository): GetMarketplaceFilterParametersUseCase =
        GetMarketplaceFilterParameters(repository)

    @Provides
    fun homeFeatureConfiguration(repository: AppConfigurationRepository): GetHomeFeatureConfigurationUseCase =
        GetHomeFeatureConfiguration(repository)
}
