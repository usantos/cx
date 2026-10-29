package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingCombosLoader
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingModalitiesLoader
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingParametersLoader
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingSubmission

class BettingRepositoryImpl(
    private val remote: BettingRemoteDataSource,
) : BettingRepository {
    private val modalityLoader = BettingModalitiesLoader(remote)
    private val parameterLoader = BettingParametersLoader(remote)
    private val comboLoader = BettingCombosLoader(remote)
    private val submission = BettingSubmission(remote)

    override suspend fun modalities(): List<LotteryModality> = modalityLoader.modalities()

    override suspend fun parameters(): BettingParameters = parameterLoader.parameters()

    override suspend fun combos(): List<BetCombo> = comboLoader.combos()

    override suspend fun addBet(draft: BetDraft): ShoppingCart = submission.addBet(draft)

    override suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart = comboLoader.addCombo(comboId, quantity, surprise)
}
