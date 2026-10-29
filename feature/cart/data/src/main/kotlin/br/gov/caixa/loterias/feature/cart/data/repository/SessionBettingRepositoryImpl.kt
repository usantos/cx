package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository

class SessionBettingRepositoryImpl(
    private val remote: BettingRepository,
    private val local: VisitorCartRepository,
    private val isAuthenticated: () -> Boolean,
) : BettingRepository by remote {
    override suspend fun addBet(draft: BetDraft): ShoppingCart =
        if (isAuthenticated()) remote.addBet(draft) else local.add(draft, remote.parameters())
}
