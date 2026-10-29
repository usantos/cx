package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardPurchaseContext
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PaymentCardSavedCardsCoordinator(
    private val mutableState: ReduxStateStore<PaymentCardUiState, PaymentCardAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val paymentMethodId: Long,
    private val savedCardId: String?,
    private val initialStage: PaymentCardStage,
    private val getCards: GetSavedCardsUseCase,
    private val purchase: PaymentCardPurchaseContext,
    private val fail: (AppError) -> Unit,
) {
    fun load() = scope.launch(dispatchers.io) {
        if (mutableState.value.stage == PaymentCardStage.PROCESSING || mutableState.value.redirect != null ||
            mutableState.value.redirectPaused ||
            mutableState.value.registrationPending ||
            mutableState.value.notice != null ||
            mutableState.value.purchaseReview != null ||
            purchase.trackedPurchase != null
        ) {
            return@launch
        }
        if (savedCardId.isNullOrBlank()) {
            if (initialStage == PaymentCardStage.FORM) {
                mutableState.dispatch(PaymentCardAction.FormStarted)
                return@launch
            }
            loadFirstSavedCardForLegacyEntryPoint()
            return@launch
        }
        when (val result = getCards(paymentMethodId)) {
            is AppResult.Failure -> fail(result.error)

            is AppResult.Success -> {
                val card = result.value.firstOrNull { it.id == savedCardId }
                if (card == null) {
                    fail(AppError.Validation(messageText("cartao_salvo_nao_encontrado_aa07a0")))
                } else {
                    mutableState.dispatch(PaymentCardAction.SavedCardPreviewed(card))
                }
            }
        }
    }

    /**
     * Algumas atividades legadas podiam ser acessadas de forma independente, embora agora sejam estados de um único
     * fluxo Compose. Suas rotas de compatibilidade restauram o primeiro cartão real salvo em vez de renderizar dados
     * locais de um cartão de exemplo.
     */
    private suspend fun loadFirstSavedCardForLegacyEntryPoint() {
        when (val result = getCards(paymentMethodId)) {
            is AppResult.Failure -> fail(result.error)

            is AppResult.Success -> {
                val card = result.value.firstOrNull()
                if (card == null) {
                    mutableState.dispatch(PaymentCardAction.SavedCardMissing())
                } else {
                    mutableState.dispatch(PaymentCardAction.SavedCardLoaded(initialStage, card))
                }
            }
        }
    }
}
