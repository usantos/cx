package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import kotlinx.coroutines.delay
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import kotlinx.coroutines.launch
import br.gov.caixa.loterias.feature.account.presentation.state.AccountStatusEffect

class AccountStatusViewModel(
    val route: String,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private var started = false
    private val mutableEffects = SingleConsumerEventQueue<AccountStatusEffect>()
    val effects = mutableEffects.events

    fun start() {
        if (started || route !in confirmationRoutes) return
        started = true
        viewModelScope.launch(dispatchers.main) {
            delay(CONFIRMATION_DURATION_MILLIS)
            mutableEffects.emit(AccountStatusEffect.Completed)
        }
    }

    private companion object {
        const val CONFIRMATION_DURATION_MILLIS = 3_000L
        val confirmationRoutes = setOf(
            "account/register-confirmation",
            "account/personal-data-confirmation",
        )
    }
}
