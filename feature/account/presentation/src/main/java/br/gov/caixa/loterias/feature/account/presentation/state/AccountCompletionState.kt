package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination

data class AccountCompletionOutcome(val identity: AuthenticatedSessionIdentity, val destination: PostLoginDestination)

data class AccountCompletionState(val checking: Boolean = false, val error: String? = null, val outcome: AccountCompletionOutcome? = null)
