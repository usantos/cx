package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.state.AccountCompletionAction
import br.gov.caixa.loterias.feature.account.presentation.state.AccountCompletionOutcome
import br.gov.caixa.loterias.feature.account.presentation.state.AccountCompletionState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceAccountCompletionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountCompletionViewModel(
    private val identities: AuthenticatedSessionIdentityProvider,
    private val verify: VerifyPostLoginAccessUseCase,
) : ViewModel() {
    private val mutableState = reduxStateStore<AccountCompletionState, AccountCompletionAction>(
        AccountCompletionState(),
        ::reduceAccountCompletionState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private var request: Job? = null
    private var generation = 0L
    fun recheck() {
        if (state.value.checking) return
        mutableState.dispatch(AccountCompletionAction.CheckStarted)
        val current = ++generation
        request = viewModelScope.launch {
            try {
                val lease = identities.captureIdentity() ?: return@launch
                val checked = verify()
                if (current != generation || !identities.isCurrent(lease)) return@launch
                mutableState.dispatch(
                    when (checked) {
                        is PostLoginAccessResult.Verified -> AccountCompletionAction.Verified(
                            AccountCompletionOutcome(lease, checked.access.destination)
                        )

                        else -> AccountCompletionAction.Failed(messageText("nao_foi_possivel_validar_o_cadas_3b5e61"))
                    }
                )
            } finally {
                if (current == generation && state.value.outcome == null) {
                    mutableState.dispatch(AccountCompletionAction.CheckFinished)
                }
            }
        }
    }
    fun dismissError() {
        mutableState.dispatch(AccountCompletionAction.ErrorDismissed)
    }
    fun acknowledge(outcome: AccountCompletionOutcome) {
        mutableState.dispatch(AccountCompletionAction.Acknowledged(outcome))
    }
    fun reset() {
        generation++
        request?.cancel()
        mutableState.dispatch(AccountCompletionAction.Reset)
    }
}
