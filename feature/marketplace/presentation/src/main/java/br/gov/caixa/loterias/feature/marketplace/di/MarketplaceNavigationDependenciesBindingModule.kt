package br.gov.caixa.loterias.feature.marketplace.di

import br.gov.caixa.loterias.feature.marketplace.navigation.MarketplaceNavigationDependencies
import br.gov.caixa.loterias.feature.marketplace.navigation.MarketplaceNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MarketplaceNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindMarketplaceNavigationDependenciesDependencies(implementation: MarketplaceNavigationDependenciesAdapter): MarketplaceNavigationDependencies
}
