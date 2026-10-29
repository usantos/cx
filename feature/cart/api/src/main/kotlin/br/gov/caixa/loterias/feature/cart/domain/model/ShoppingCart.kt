package br.gov.caixa.loterias.feature.cart.domain.model

import java.math.BigDecimal

data class ShoppingCart(
    val id: Long,
    val total: BigDecimal,
    val items: List<CartItem>,
    val expiredPoolShareRemoved: Boolean = false,
    /** Contagem a partir do retrato completo, independente do número de cartões agrupados. */
    val totalBetCount: Int? = null,
) {
    val betCount: Int
        get() = totalBetCount ?: items.sumOf { if (it.kind == CartItemKind.COMBO) it.comboBetCount else 1 }
}
