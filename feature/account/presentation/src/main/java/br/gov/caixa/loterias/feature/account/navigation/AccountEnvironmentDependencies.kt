package br.gov.caixa.loterias.feature.account.navigation

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment

/** Operações existentes de seleção de ambiente expostas à apresentação da conta. */
interface AccountEnvironmentDependencies {
    val effectiveEnvironment: br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment
    val selectedEnvironment: TestEnvironment
    val selectorVisible: Boolean
    val changeEnvironment: ChangeEnvironment
}
