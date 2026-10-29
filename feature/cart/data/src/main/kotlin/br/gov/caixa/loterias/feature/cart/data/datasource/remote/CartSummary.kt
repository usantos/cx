package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import java.math.BigDecimal

/** CarrinhoSingleton.setCarrinho recalcula a partir de apostas, e não pela soma de suas projeções agrupadas. */
internal fun CartDto.summaryTotal(groupedItems: List<CartItem>): BigDecimal = bets?.fold(BigDecimal.ZERO) { total, bet ->
    val amount = bet.poolReservation?.let {
        requireNotNull(it.total) { messageText("cota_de_bolao_sem_valor_total_29f4a7") }
    } ?: bet.amount ?: BigDecimal.ZERO
    total + amount
} ?: total ?: groupedItems.fold(BigDecimal.ZERO) { total, item -> total + item.amount }
