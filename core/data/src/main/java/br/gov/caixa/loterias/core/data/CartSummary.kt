package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.network.CartDto
import java.math.BigDecimal

/** CarrinhoSingleton.setCarrinho recomputes from apostas, not by adding its grouped projections. */
internal fun CartDto.summaryTotal(groupedItems: List<CartItem>): BigDecimal =
    bets?.fold(BigDecimal.ZERO) { total, bet ->
        val amount = bet.poolReservation?.let {
            requireNotNull(it.total) { "Cota de bolão sem valor total" }
        } ?: bet.amount ?: BigDecimal.ZERO
        total + amount
    } ?: total ?: groupedItems.fold(BigDecimal.ZERO) { total, item -> total + item.amount }
