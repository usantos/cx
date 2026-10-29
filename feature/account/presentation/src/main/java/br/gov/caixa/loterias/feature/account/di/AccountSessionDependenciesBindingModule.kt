package br.gov.caixa.loterias.feature.account.di

import br.gov.caixa.loterias.feature.account.navigation.AccountSessionDependencies
import br.gov.caixa.loterias.feature.account.navigation.AccountSessionDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountSessionDependenciesBindingModule {
    @Binds
    abstract fun bindAccountSessionDependencies(
        implementation: AccountSessionDependenciesAdapter,
    ): AccountSessionDependencies
}
