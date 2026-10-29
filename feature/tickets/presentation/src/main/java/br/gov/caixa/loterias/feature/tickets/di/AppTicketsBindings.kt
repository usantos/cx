package br.gov.caixa.loterias.feature.tickets.di

import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppTicketsBindings {
    @Provides @Singleton
    fun ticketsRemoteDataSource(
        provider: BackendApiProvider,
        betsHistoryApiProvider: BetsHistoryApiProvider,
        configurationSource: AppConfigurationRemoteDataSource,
        currentUserIdentityProvider: CurrentUserIdentityProvider,
        hostConfiguration: HostFeatureConfiguration,
    ): TicketsRemoteDataSourceImpl = TicketsRemoteDataSourceImpl(
        apiProvider = provider,
        betsHistoryApiProvider = betsHistoryApiProvider,
        configurationSource = configurationSource,
        currentUserCpfProvider = { currentUserIdentityProvider.currentUser()?.cpf.orEmpty() },
        production = hostConfiguration.productionBuild,
    )
}
