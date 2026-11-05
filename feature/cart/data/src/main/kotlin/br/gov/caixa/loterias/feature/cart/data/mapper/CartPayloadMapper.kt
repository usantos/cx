package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemBadge
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.network.dto.cart.CartBetDto
import br.gov.caixa.loterias.core.network.dto.cart.CartDto
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.summaryTotal
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText

internal fun CartDto?.mapCartPayload(): ShoppingCart {
    val cart = requireNotNull(this) { messageText("carrinho_sem_payload_d5c84c") }
    val mappedItems = buildList {
        cart.poolShares.orEmpty().forEach { add(it.cartToCartItem(CartItemKind.POOL_SHARE)) }
        val groupedBets = cart.individualBets.orEmpty()
        val groupedIds = (groupedBets + cart.poolShares.orEmpty() + cart.combos.orEmpty().flatMap { it.bets.orEmpty() })
            .mapNotNull { it.id }.toSet()
        val individualBets = groupedBets + cart.bets.orEmpty().filter {
            it.id !in groupedIds && it.poolReservation == null && it.combo != true
        }
        individualBets.filter { it.modality?.isJsonNull == false }.forEach {
            add(it.cartToCartItem(CartItemKind.INDIVIDUAL_BET))
        }
        cart.combos.orEmpty().forEach { combo ->
            val bets = combo.bets.orEmpty()
            val comboBadges = buildSet {
                add(CartItemBadge.COMBO)
                if (bets.any(CartBetDto::cartIsMirror)) add(CartItemBadge.MIRROR)
                if (bets.any(CartBetDto::cartIsSurprise)) add(CartItemBadge.SURPRISE)
                if (bets.any { it.recurringDraws.cartOrZero() > 0 }) add(CartItemBadge.RECURRING)
            }
            add(
                CartItem(
                    id = combo.id ?: 0,
                    kind = CartItemKind.COMBO,
                    title = combo.type?.name.orEmpty().ifBlank { "Combo" },
                    description = bets.size.cartBetCountLabel(),
                    amount = bets.fold(BigDecimal.ZERO) { total, bet -> total + bet.amount.cartOrZero() },
                    modalityCode = "COMBO",
                    badges = comboBadges,
                    comboBetCount = bets.size
                )
            )
        }
    }
    return ShoppingCart(
        id = cart.id ?: 0,
        total = cart.summaryTotal(
            mappedItems + cart.individualBets.orEmpty()
                .filter { it.modality?.isJsonNull != false }
                .map { it.cartToCartItem(CartItemKind.INDIVIDUAL_BET) }
        ),
        items = mappedItems,
        expiredPoolShareRemoved = cart.expiredPoolShareRemoved == true,
        totalBetCount = cart.bets?.size
    )
}
