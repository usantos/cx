package br.gov.caixa.loterias.feature.betting.di

import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.repository.BettingRepositoryImpl
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppBettingBindings {
    @Provides @Singleton
    fun bettingRepository(source: BettingRemoteDataSource,
        local: br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository,
        tokenStore: SessionTokenStore,
        hostConfiguration: HostFeatureConfiguration,
    ): BettingRepository = br.gov.caixa.loterias.feature.cart.data.repository.SessionBettingRepositoryImpl(
        br.gov.caixa.loterias.feature.betting.data.repository.SessionBettingParametersRepositoryImpl(
            BettingRepositoryImpl(source),
            { hostConfiguration.backendSessionNamespace + "|" + hostConfiguration.backendApiBaseUrl },
            tokenStore::captureIdentity
        ),
        local
    ) { tokenStore.token() != null }
}
