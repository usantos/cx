package br.gov.caixa.loterias.core.domain.contract.cart.model

import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemBadge
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartLotecaSelection
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartPoolShare
import java.math.BigDecimal

data class CartItem(
    val id: Long,
    val kind: CartItemKind,
    val title: String,
    val description: String,
    val amount: BigDecimal,
    val modalityCode: String = "",
    val contestNumber: Int? = null,
    val numberQuantity: Int = 0,
    val recurringDraws: Int = 1,
    val selectedNumbers: List<List<Int>> = emptyList(),
    val selectedClovers: List<Int> = emptyList(),
    val badges: Set<CartItemBadge> = emptySet(),
    val complementaryLabel: String? = null,
    val poolShare: CartPoolShare? = null,
    val gameRows: List<String> = emptyList(),
    val comboBetCount: Int = 0,
    val lotecaSelections: List<CartLotecaSelection> = emptyList(),
)
