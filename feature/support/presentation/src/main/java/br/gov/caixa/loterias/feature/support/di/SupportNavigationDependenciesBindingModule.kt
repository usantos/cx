package br.gov.caixa.loterias.feature.support.di

import br.gov.caixa.loterias.feature.support.navigation.SupportNavigationDependencies
import br.gov.caixa.loterias.feature.support.navigation.SupportNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SupportNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindSupportNavigationDependenciesDependencies(implementation: SupportNavigationDependenciesAdapter): SupportNavigationDependencies
}
