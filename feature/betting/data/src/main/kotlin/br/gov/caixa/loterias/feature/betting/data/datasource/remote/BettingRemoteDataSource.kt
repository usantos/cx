package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.betting.AddBetRequestDto
import br.gov.caixa.loterias.core.network.dto.betting.AddComboRequestDto
import br.gov.caixa.loterias.core.network.dto.betting.BetComboDto
import br.gov.caixa.loterias.core.network.dto.betting.BettingParametersDto
import br.gov.caixa.loterias.core.network.dto.betting.LotteryModalityDto
import br.gov.caixa.loterias.core.network.dto.betting.SportsTeamShieldDto
import br.gov.caixa.loterias.core.network.dto.cart.CartDto

interface BettingRemoteDataSource {
    suspend fun modalities(): List<LotteryModalityDto>?
    suspend fun bettingParameters(): BettingParametersDto?
    suspend fun combos(): List<BetComboDto>?
    suspend fun addBet(request: AddBetRequestDto): CartDto?
    suspend fun addCombo(request: AddComboRequestDto): CartDto?
    suspend fun sportsTeamShields(): List<SportsTeamShieldDto>?
}
