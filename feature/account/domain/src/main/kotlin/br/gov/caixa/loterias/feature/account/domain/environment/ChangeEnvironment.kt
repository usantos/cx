package br.gov.caixa.loterias.feature.account.domain.environment

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment

fun interface ChangeEnvironment {
    suspend operator fun invoke(environment: TestEnvironment): Boolean
}
