package br.gov.caixa.loterias.feature.responsiblegaming.di

import br.gov.caixa.loterias.feature.responsiblegaming.navigation.ResponsibleGamingNavigationDependencies
import br.gov.caixa.loterias.feature.responsiblegaming.navigation.ResponsibleGamingNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ResponsibleGamingNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindResponsibleGamingNavigationDependenciesDependencies(implementation: ResponsibleGamingNavigationDependenciesAdapter): ResponsibleGamingNavigationDependencies
}
