package br.gov.caixa.loterias.feature.onboarding.di

import br.gov.caixa.loterias.feature.onboarding.navigation.OnboardingNavigationDependencies
import br.gov.caixa.loterias.feature.onboarding.navigation.OnboardingNavigationDependenciesAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingNavigationDependenciesBindingModule {
    @Binds
    abstract fun bindOnboardingNavigationDependenciesDependencies(implementation: OnboardingNavigationDependenciesAdapter): OnboardingNavigationDependencies
}
