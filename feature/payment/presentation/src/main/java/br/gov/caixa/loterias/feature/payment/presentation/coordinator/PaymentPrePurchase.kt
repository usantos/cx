package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState
import kotlinx.coroutines.launch

internal class PaymentPrePurchase(
    private val mutableState: ReduxStateStore<PaymentUiState, PaymentAction>,
    private val viewModelScope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val validateDam: ValidateDamUseCase,
    private val applyFailure: (AppError) -> Unit,
) {
    fun acceptPrePurchase() = viewModelScope.launch(dispatchers.io) {
        mutableState.dispatch(PaymentAction.PrePurchaseAccepted)
        when (val result = validateDam()) {
            is AppResult.Failure -> applyFailure(result.error)
            is AppResult.Success -> mutableState.dispatch(PaymentAction.DamValidated(result.value.message, result.value.blocked))
        }
    }
}
