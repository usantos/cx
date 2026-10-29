package br.gov.caixa.loterias.feature.home.di

import br.gov.caixa.loterias.feature.home.navigation.HomeNavigationDependencies
import br.gov.caixa.loterias.feature.home.navigation.HomeNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindHomeNavigationDependenciesDependencies(implementation: HomeNavigationDependenciesAdapter): HomeNavigationDependencies
}
