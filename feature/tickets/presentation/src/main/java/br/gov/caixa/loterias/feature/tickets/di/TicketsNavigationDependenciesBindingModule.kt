package br.gov.caixa.loterias.feature.tickets.di

import br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependencies
import br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependenciesAdapter
import br.gov.caixa.loterias.core.navigation.fixture.VisualBetNavigationFixtures
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TicketsNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindTicketsNavigationDependenciesDependencies(implementation: TicketsNavigationDependenciesAdapter): TicketsNavigationDependencies

    @Binds
    abstract fun bindVisualBetNavigationFixtures(implementation: TicketsNavigationDependenciesAdapter): VisualBetNavigationFixtures
}
