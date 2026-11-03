package br.gov.caixa.loterias.feature.support.navigation

import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntryLoader

/** Dependências explícitas para disponibilidade, instalação e entrada do FAQ. */
interface SupportModuleNavigationDependencies {
    val modules: AppModulesConfigurationUseCase
    val delivery: SupportModuleDelivery
    val entryLoader: SupportFaqEntryLoader
}
