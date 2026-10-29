package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.BettingGameDraftMapper
import br.gov.caixa.loterias.feature.betting.presentation.mapper.availableNumberQuantities
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class BettingGameCartCoordinator(
    private val state: StateFlow<BettingGameUiState>,
    private val viewModelScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val addBet: AddBetToCartUseCase,
    private val draftMapper: BettingGameDraftMapper,
    private val dispatch: (BettingGameAction) -> Unit,
    private val incompleteBetMessage: (BettingGameUiState) -> String,
    private val applyFailure: (AppError) -> Unit,
    private val beforeRemote: suspend (BettingGameUiState) -> Boolean = { true },
    private val afterRemote: suspend () -> Boolean = { true },
    private val isCurrent: () -> Boolean = { true },
) {
    private var authenticated = false
    private var cartTotal: BigDecimal? = null
    private var pending: Pair<BettingGameUiState, Boolean>? = null

    fun updateCartContext(authenticated: Boolean, total: BigDecimal?) {
        this.authenticated = authenticated
        cartTotal = total
        if (!authenticated) cancelDailyLimit()
    }

    fun cancelDailyLimit() {
        pending = null
        dispatch(BettingGameAction.DailyLimitCancelled)
    }

    fun confirmDailyLimit() {
        val requested = pending ?: return
        cancelDailyLimit()
        if (authenticated) submit(requested.first, requested.second)
    }

    fun confirmLocalDuplicate() {
        dispatch(BettingGameAction.DuplicateCancelled)
        addToCart(allowLocalDuplicate = true)
    }

    fun cancelLocalDuplicate() = dispatch(BettingGameAction.DuplicateCancelled)

    fun addToCart(allowLocalDuplicate: Boolean = false) {
        val current = state.value
        if (current.loading || current.addedToCart || current.dailyLimitConfirmation) return
        val game = current.game ?: return
        if (!game.open) {
            dispatch(BettingGameAction.ClosedContestRejected(game.contestNumber))
            return
        }
        if (!current.canAddToCart) {
            dispatch(BettingGameAction.IncompleteBetRejected(incompleteBetMessage(state.value)))
            return
        }
        // SimulaViewModel verifica o carrinho existente mais o valor integral da aposta manual,
        // estritamente acima do limite diário da simulação. A Surpresinha segue seu próprio fluxo.
        val limit = current.dailyLimit
        val total = cartTotal
        if (authenticated && current.mode == BetEntryMode.MANUAL && limit != null &&
            total != null && total.add(current.totalPrice) > limit
        ) {
            pending = current to allowLocalDuplicate
            dispatch(BettingGameAction.DailyLimitRequired)
            return
        }
        submit(current, allowLocalDuplicate)
    }

    private fun submit(current: BettingGameUiState, allowLocalDuplicate: Boolean) {
        val game = current.game ?: return
        dispatch(BettingGameAction.CartSubmissionStarted)
        viewModelScope.launch(dispatchers.io) {
            if (!isCurrent() || !beforeRemote(current)) return@launch
            val draft = draftMapper.map(current, game, allowLocalDuplicate)
            when (val result = addBet(draft)) {
                is AppResult.Success -> {
                    if (!isCurrent() || !afterRemote()) return@launch
                    dispatch(BettingGameAction.CartAdditionConfirmed(game, result.value))
                }

                is AppResult.Failure -> if (isCurrent()) applyFailure(result.error)
            }
        }
    }
}
