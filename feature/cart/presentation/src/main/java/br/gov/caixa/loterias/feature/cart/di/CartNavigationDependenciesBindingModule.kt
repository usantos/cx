package br.gov.caixa.loterias.feature.cart.di

import br.gov.caixa.loterias.feature.cart.navigation.CartNavigationDependencies
import br.gov.caixa.loterias.feature.cart.navigation.CartNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CartNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindCartNavigationDependenciesDependencies(implementation: CartNavigationDependenciesAdapter): CartNavigationDependencies
}
