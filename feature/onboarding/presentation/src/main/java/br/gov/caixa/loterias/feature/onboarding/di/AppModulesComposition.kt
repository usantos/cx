package br.gov.caixa.loterias.feature.onboarding.di

import android.content.Context
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.feature.onboarding.data.config.FirebaseAppModulesConfigurationRepositoryImpl
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.CoroutineScope

/** Mantém a construção do adaptador Firebase na composição da funcionalidade. */
fun createAppModulesConfigurationUseCase(
    context: Context,
    options: FirebaseOptions,
    scope: CoroutineScope,
): AppModulesConfigurationUseCase = AppModulesConfigurationUseCase(FirebaseAppModulesConfigurationRepositoryImpl(context, options, scope))
