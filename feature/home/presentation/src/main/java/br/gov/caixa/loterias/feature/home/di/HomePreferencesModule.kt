package br.gov.caixa.loterias.feature.home.di

import android.content.Context
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.data.repository.HomeLayoutRepositoryImpl
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HomePreferencesModule {
    @Provides
    fun resaleOffers(clock: br.gov.caixa.loterias.core.common.time.ServerTimeSource):
        br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffersUseCase =
        br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(clock)

    @Provides @Singleton
    fun layout(@ApplicationContext context: Context): HomeLayoutRepository = HomeLayoutRepositoryImpl(context)

    @Provides
    fun preferences(layout: HomeLayoutRepository, shake: ShakeSettingsRepository) = HomePreferencesUseCases(layout, shake)
}
