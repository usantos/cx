package br.gov.caixa.loterias.feature.retail.di

import br.gov.caixa.loterias.feature.retail.navigation.RetailNavigationDependencies
import br.gov.caixa.loterias.feature.retail.navigation.RetailNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RetailNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindRetailNavigationDependenciesDependencies(implementation: RetailNavigationDependenciesAdapter): RetailNavigationDependencies
}
