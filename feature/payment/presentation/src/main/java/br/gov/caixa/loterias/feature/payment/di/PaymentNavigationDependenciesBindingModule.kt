package br.gov.caixa.loterias.feature.payment.di

import br.gov.caixa.loterias.feature.payment.navigation.PaymentNavigationDependencies
import br.gov.caixa.loterias.feature.payment.navigation.PaymentNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindPaymentNavigationDependenciesDependencies(implementation: PaymentNavigationDependenciesAdapter): PaymentNavigationDependencies
}
