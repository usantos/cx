package br.gov.caixa.loterias.feature.cart.presentation.state

import java.math.BigDecimal

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
