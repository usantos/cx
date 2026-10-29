package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

/** Compartilha o grafo legado e o recibo de transferência; o editor nunca envia nem confirma. */
interface VisitorCartRepository {
    suspend fun current(): ShoppingCart
    suspend fun add(draft: BetDraft, parameters: BettingParameters): ShoppingCart
    suspend fun remove(item: CartItem): ShoppingCart
    suspend fun clear(): ShoppingCart
}
