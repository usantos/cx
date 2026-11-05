package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.contract.account.model.PostLoginDestination

data class AccountCompletionOutcome(val identity: AuthenticatedSessionIdentity, val destination: PostLoginDestination)

data class AccountCompletionState(val checking: Boolean = false, val error: String? = null, val outcome: AccountCompletionOutcome? = null)
