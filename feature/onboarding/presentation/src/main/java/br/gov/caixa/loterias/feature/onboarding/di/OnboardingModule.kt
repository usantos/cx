package br.gov.caixa.loterias.feature.onboarding.di

import android.content.Context
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroduction
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroduction
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.data.repository.OnboardingRepositoryImpl
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object OnboardingModule {
    @Provides @Singleton
    fun onboardingRepository(@ApplicationContext context: Context): OnboardingRepository =
        OnboardingRepositoryImpl.fromContext(context)

    @Provides
    fun shouldShowIntroduction(repository: OnboardingRepository): ShouldShowIntroductionUseCase =
        ShouldShowIntroduction(repository)

    @Provides
    fun completeIntroduction(repository: OnboardingRepository): CompleteIntroductionUseCase =
        CompleteIntroduction(repository)
}
