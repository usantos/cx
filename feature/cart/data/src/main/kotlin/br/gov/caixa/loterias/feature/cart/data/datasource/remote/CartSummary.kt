package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.network.dto.cart.CartDto
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText

/** CarrinhoSingleton.setCarrinho recalcula a partir de apostas, e não pela soma de suas projeções agrupadas. */
internal fun CartDto.summaryTotal(groupedItems: List<CartItem>): BigDecimal = bets?.fold(BigDecimal.ZERO) { total, bet ->
    val amount = bet.poolReservation?.let {
        requireNotNull(it.total) { messageText("cota_de_bolao_sem_valor_total_29f4a7") }
    } ?: bet.amount ?: BigDecimal.ZERO
    total + amount
} ?: total ?: groupedItems.fold(BigDecimal.ZERO) { total, item -> total + item.amount }
