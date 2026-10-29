package br.gov.caixa.loterias.feature.betting.di

import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependencies
import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BettingNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindBettingNavigationDependenciesDependencies(implementation: BettingNavigationDependenciesAdapter): BettingNavigationDependencies
}
