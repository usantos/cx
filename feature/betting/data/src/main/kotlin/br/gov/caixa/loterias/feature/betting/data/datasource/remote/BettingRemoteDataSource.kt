package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddBetRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddComboRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamShieldDto

interface BettingRemoteDataSource {
    suspend fun modalities(): List<LotteryModalityDto>?
    suspend fun bettingParameters(): BettingParametersDto?
    suspend fun combos(): List<BetComboDto>?
    suspend fun addBet(request: AddBetRequestDto): CartDto?
    suspend fun addCombo(request: AddComboRequestDto): CartDto?
    suspend fun sportsTeamShields(): List<SportsTeamShieldDto>?
}
