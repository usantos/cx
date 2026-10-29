package br.gov.caixa.loterias.feature.betting.domain.repository

import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo

interface BettingRepository {
    suspend fun modalities(): List<LotteryModality>
    suspend fun parameters(): BettingParameters
    suspend fun combos(): List<BetCombo>
    suspend fun addBet(draft: BetDraft): ShoppingCart
    suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart
}
