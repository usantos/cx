package br.gov.caixa.loterias.feature.onboarding.di

import android.content.Context
import br.gov.caixa.loterias.feature.onboarding.data.repository.FirebaseLauncherIconConfigurationRepositoryImpl
import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconCalendarConfiguration
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetLauncherIconPresetUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveLauncherIconCalendarUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.LauncherIconViewModel
import com.google.firebase.FirebaseOptions

fun createLauncherIconViewModel(context: Context, options: FirebaseOptions): LauncherIconViewModel {
    val repository = createLauncherIconCalendarConfiguration(context, options)
    return LauncherIconViewModel(GetLauncherIconPresetUseCase(repository), ObserveLauncherIconCalendarUseCase(repository))
}

fun createLauncherIconCalendarConfiguration(context: Context, options: FirebaseOptions): LauncherIconCalendarConfiguration =
    FirebaseLauncherIconConfigurationRepositoryImpl(context.applicationContext, options)
