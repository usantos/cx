package br.gov.caixa.loterias.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.ServerTimeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

enum class CartAdditionKind { BET, POOL_SHARE }

data class CartAdditionUiState(
    val kind: CartAdditionKind,
    val remainingSeconds: Int,
    val modality: String = "",
    val contest: Int = 0,
    val drawDate: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
    val expired: Boolean = false,
    val minimumCartAmount: BigDecimal = BigDecimal.ZERO,
)

fun interface CountdownTicker {
    suspend fun awaitTick()
}

class CartAdditionViewModel(
    kind: CartAdditionKind,
    private val serverTime: ServerTimeSource,
    expiresAt: String = "",
    modality: String = "",
    contest: Int = 0,
    drawDate: String = "",
    amount: BigDecimal = BigDecimal.ZERO,
    minimumCartAmount: BigDecimal = BigDecimal.ZERO,
    private val ticker: CountdownTicker = CountdownTicker { delay(1_000) },
) : ViewModel() {
    private val expiration = reservationExpirationInstant(expiresAt)
    private val poolSeconds = if (kind == CartAdditionKind.POOL_SHARE) reservationRemainingSeconds(expiresAt, serverTime.now()) else 0
    private val mutableState = MutableStateFlow(
        CartAdditionUiState(
            kind = kind,
            remainingSeconds = if (kind == CartAdditionKind.POOL_SHARE) poolSeconds.coerceAtLeast(0) else 0,
            modality = modality,
            contest = contest,
            drawDate = drawDate,
            amount = amount,
            minimumCartAmount = minimumCartAmount,
            expired = kind == CartAdditionKind.POOL_SHARE && poolSeconds <= 0,
        ),
    )
    val state = mutableState.asStateFlow()

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

    /** Ticks only schedule a refresh; elapsed server time is the authority, including after resume. */
    fun refreshReservation() {
        if (state.value.kind != CartAdditionKind.POOL_SHARE || state.value.expired) return
        val remaining = remainingSecondsUntil(expiration, serverTime.now())
        mutableState.update { it.copy(remainingSeconds = remaining, expired = remaining == 0) }
    }
}

internal fun formatRemainingTime(totalSeconds: Int): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    return "%02d:%02d".format(safeSeconds / 60, safeSeconds % 60)
}

internal fun reservationRemainingSeconds(
    expiresAt: String,
    now: Instant,
): Int = remainingSecondsUntil(reservationExpirationInstant(expiresAt), now)

private fun remainingSecondsUntil(expiration: Instant?, now: Instant): Int = expiration?.let {
    Duration.between(now, it).seconds.coerceIn(0, Int.MAX_VALUE.toLong()).toInt()
} ?: 0
