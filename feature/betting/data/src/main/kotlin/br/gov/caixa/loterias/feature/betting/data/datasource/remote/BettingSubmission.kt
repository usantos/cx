package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.data.mapper.toAddBetRequest
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart

internal class BettingSubmission(private val remote: BettingRemoteDataSource) {
    suspend fun addBet(draft: BetDraft): ShoppingCart = remote.addBet(draft.toAddBetRequest()).toDomainCart()
}
