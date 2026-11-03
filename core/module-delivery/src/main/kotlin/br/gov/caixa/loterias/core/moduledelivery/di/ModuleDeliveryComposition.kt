package br.gov.caixa.loterias.core.moduledelivery.di

import android.content.Context
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import br.gov.caixa.loterias.core.moduledelivery.data.PlayModuleInstallationRepositoryImpl

fun createModuleInstallationUseCase(context: Context): InstallAppModuleUseCase =
    InstallAppModuleUseCase(createModuleInstallationRepository(context))

fun createModuleInstallationRepository(context: Context): ModuleInstallationRepository = PlayModuleInstallationRepositoryImpl(context)
