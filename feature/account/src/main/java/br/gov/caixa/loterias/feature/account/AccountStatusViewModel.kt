package br.gov.caixa.loterias.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface AccountStatusEffect {
    data object Completed : AccountStatusEffect
}

class AccountStatusViewModel(
    val route: String,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private var started = false
    private val mutableEffects = MutableSharedFlow<AccountStatusEffect>()
    val effects = mutableEffects.asSharedFlow()

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
