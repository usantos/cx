package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment

data class EnvironmentUiState(val pending: TestEnvironment? = null, val saving: Boolean = false,
    val failed: Boolean = false, val persisted: Boolean = false,
)
