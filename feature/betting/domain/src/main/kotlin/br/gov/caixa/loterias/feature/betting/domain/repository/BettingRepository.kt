package br.gov.caixa.loterias.feature.betting.domain.repository

import br.gov.caixa.loterias.core.domain.contract.betting.model.BettingParameters
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryModality
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft

interface BettingRepository {
    suspend fun modalities(): List<LotteryModality>
    suspend fun parameters(): BettingParameters
    suspend fun combos(): List<BetCombo>
    suspend fun addBet(draft: BetDraft): ShoppingCart
    suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart
}
