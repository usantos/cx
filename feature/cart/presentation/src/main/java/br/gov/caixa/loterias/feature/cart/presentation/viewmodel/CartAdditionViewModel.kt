package br.gov.caixa.loterias.feature.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.presentation.mapper.reservationExpirationInstant
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionKind
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionUiState
import br.gov.caixa.loterias.feature.cart.presentation.state.CountdownTicker
import br.gov.caixa.loterias.feature.cart.presentation.state.reduceCartAdditionState
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartAdditionViewModel(
    kind: CartAdditionKind,
    private val serverTime: ServerTimeSource,
    expiresAt: String = "",
    modality: String = "",
    contest: Int = 0,
    drawDate: String = "",
    amount: BigDecimal = BigDecimal.ZERO,
    minimumCartAmount: BigDecimal = BigDecimal.ZERO,
    private val ticker: CountdownTicker = CountdownTicker { delay(1_000) }
) : ViewModel() {
    private val expiration = reservationExpirationInstant(expiresAt)
    private val poolSeconds = if (kind == CartAdditionKind.POOL_SHARE) reservationRemainingSeconds(expiresAt, serverTime.now()) else 0
    private val mutableState = reduxStateStore<CartAdditionUiState, CartAdditionAction>(
        CartAdditionUiState(
            kind = kind,
            remainingSeconds = if (kind == CartAdditionKind.POOL_SHARE) poolSeconds.coerceAtLeast(0) else 0,
            modality = modality,
            contest = contest,
            drawDate = drawDate,
            amount = amount,
            minimumCartAmount = minimumCartAmount,
            expired = kind == CartAdditionKind.POOL_SHARE && poolSeconds <= 0
        ),
        ::reduceCartAdditionState
    ).also { addCloseable(it) }
    val state = mutableState.state

    init {
        if (kind == CartAdditionKind.POOL_SHARE && mutableState.value.remainingSeconds > 0) {
            viewModelScope.launch {
                while (mutableState.value.remainingSeconds > 0) {
                    ticker.awaitTick()
                    refreshReservation()
                }
            }
        }
    }

    /** Os pulsos apenas agendam uma atualização; o tempo decorrido do servidor é a autoridade, inclusive após a retomada. */
    fun refreshReservation() {
        if (state.value.kind != CartAdditionKind.POOL_SHARE || state.value.expired) return
        val remaining = remainingSecondsUntil(expiration, serverTime.now())
        mutableState.dispatch(CartAdditionAction.ReservationRefreshed(remaining))
    }
}

internal fun formatRemainingTime(totalSeconds: Int): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    return "%02d:%02d".format(safeSeconds / 60, safeSeconds % 60)
}

internal fun reservationRemainingSeconds(expiresAt: String, now: Instant): Int =
    remainingSecondsUntil(reservationExpirationInstant(expiresAt), now)

private fun remainingSecondsUntil(expiration: Instant?, now: Instant): Int = expiration?.let {
    Duration.between(now, it).seconds.coerceIn(0, Int.MAX_VALUE.toLong()).toInt()
} ?: 0
