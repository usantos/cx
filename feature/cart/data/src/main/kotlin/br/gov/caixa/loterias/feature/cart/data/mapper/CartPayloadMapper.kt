package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.data.dto.CartBetDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.summaryTotal

internal fun CartDto?.mapCartPayload(): ShoppingCart {
    val cart = requireNotNull(this) { messageText("carrinho_sem_payload_d5c84c") }
    val mappedItems = buildList {
        cart.poolShares.orEmpty().forEach { add(it.cartToCartItem(CartItemKind.POOL_SHARE)) }
        cart.individualBets.orEmpty().filter { it.modality?.isJsonNull == false }.forEach {
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
