package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.feature.account.navigation.AccountNavigationDependencies
import br.gov.caixa.loterias.feature.account.navigation.AccountNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindAccountNavigationDependenciesDependencies(implementation: AccountNavigationDependenciesAdapter): AccountNavigationDependencies
}
