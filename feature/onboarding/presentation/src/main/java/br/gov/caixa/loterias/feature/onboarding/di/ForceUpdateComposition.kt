package br.gov.caixa.loterias.feature.onboarding.di

import android.content.Context
import br.gov.caixa.loterias.feature.onboarding.data.repository.FirebaseForceUpdateRepositoryImpl
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetActiveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.RefreshForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.ForceUpdateViewModel
import com.google.firebase.FirebaseOptions

fun createForceUpdateViewModel(context: Context, options: FirebaseOptions, defaultsResource: Int): ForceUpdateViewModel {
    val repository = FirebaseForceUpdateRepositoryImpl(context.applicationContext, options, defaultsResource)
    return ForceUpdateViewModel(
        GetActiveForceUpdateUseCase(repository),
        RefreshForceUpdateUseCase(repository),
        ObserveForceUpdateUseCase(repository)
    )
}
