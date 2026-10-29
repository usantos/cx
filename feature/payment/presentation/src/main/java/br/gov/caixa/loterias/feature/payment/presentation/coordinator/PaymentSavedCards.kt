package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState
import kotlinx.coroutines.launch

internal class PaymentSavedCards(
    private val mutableState: ReduxStateStore<PaymentUiState, PaymentAction>,
    private val viewModelScope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val selectedPaymentMethodId: Long?,
    private val getMethods: GetPaymentMethodsUseCase,
    private val getCards: GetSavedCardsUseCase,
    private val deleteCard: DeleteSavedCardUseCase,
    private val isSessionCurrent: () -> Boolean,
    private val applyFailure: (AppError) -> Unit,
) {

    suspend fun loadCards() {
        if (!isSessionCurrent()) return
        val methods = getMethods()
        if (!isSessionCurrent()) return
        when (methods) {
            is AppResult.Failure -> applyFailure(methods.error)

            is AppResult.Success -> {
                val cardMethods = methods.value.filter { method ->
                    method.isSupportedCard && (selectedPaymentMethodId == null || method.id == selectedPaymentMethodId)
                }
                if (cardMethods.isEmpty()) {
                    return applyFailure(AppError.Validation(messageText("meio_de_pagamento_indisponivel_69edb1")))
                }
                val loaded = linkedMapOf<Long, List<SavedPaymentCard>>()
                for (method in cardMethods) {
                    if (!isSessionCurrent()) return
                    val result = getCards(method.id)
                    if (!isSessionCurrent()) return
                    when (result) {
                        is AppResult.Failure -> applyFailure(result.error)
                        is AppResult.Success -> loaded[method.id] = result.value
                    }
                }
                if (mutableState.value.errorMessage == null) {
                    mutableState.dispatch(PaymentAction.MethodsLoaded(cardMethods, loaded))
                }
            }
        }
    }

    fun confirmDelete() {
        val card = mutableState.value.deleteCandidate ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.dispatch(PaymentAction.CardDeletionStarted)
            when (val result = deleteCard(card.deletionIdentifier, card.paymentMethodId)) {
                is AppResult.Failure -> applyFailure(result.error)
                is AppResult.Success -> mutableState.dispatch(PaymentAction.CardDeleted(card.paymentMethodId, result.value))
            }
        }
    }
}
