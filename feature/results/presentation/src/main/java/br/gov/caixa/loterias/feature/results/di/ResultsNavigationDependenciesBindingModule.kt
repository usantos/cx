package br.gov.caixa.loterias.feature.results.di

import br.gov.caixa.loterias.feature.results.navigation.ResultsNavigationDependencies
import br.gov.caixa.loterias.feature.results.navigation.ResultsNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ResultsNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindResultsNavigationDependenciesDependencies(implementation: ResultsNavigationDependenciesAdapter): ResultsNavigationDependencies
}
