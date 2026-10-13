package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal

data class RapidBetConfiguration(
    val minimumAmount: BigDecimal,
    val maximumAmount: BigDecimal,
    val minimumPrize: BigDecimal,
    val requiredNumbers: Set<Int>,
    val excludedNumbers: Set<Int>,
    val modalities: List<LotteryModality>,
    val favoriteBetIds: Set<Long> = emptySet(),
)

interface RapidBettingRepository {
    suspend fun configuration(): RapidBetConfiguration
    suspend fun save(configuration: RapidBetConfiguration): RapidBetConfiguration
    suspend fun generate(quantity: Int): ShoppingCart
}

fun interface GetRapidBetConfigurationUseCase {
    suspend operator fun invoke(): AppResult<RapidBetConfiguration>
}

class GetRapidBetConfiguration(private val repository: RapidBettingRepository) : GetRapidBetConfigurationUseCase {
    override suspend fun invoke() = appResultOf { repository.configuration() }
}

fun interface SaveRapidBetConfigurationUseCase {
    suspend operator fun invoke(configuration: RapidBetConfiguration): AppResult<RapidBetConfiguration>
}

class SaveRapidBetConfiguration(private val repository: RapidBettingRepository) : SaveRapidBetConfigurationUseCase {
    override suspend fun invoke(configuration: RapidBetConfiguration) = appResultOf {
        require(configuration.minimumAmount >= BigDecimal.ZERO) { "Valor mínimo inválido" }
        require(configuration.maximumAmount >= configuration.minimumAmount) { "Valor máximo deve ser maior que o mínimo" }
        require(configuration.minimumPrize >= BigDecimal.ZERO) { "Prêmio mínimo inválido" }
        require(configuration.modalities.isNotEmpty()) { "Selecione pelo menos uma modalidade" }
        require(configuration.requiredNumbers.intersect(configuration.excludedNumbers).isEmpty()) {
            "Um número não pode ser obrigatório e proibido ao mesmo tempo"
        }
        repository.save(configuration)
    }
}

fun interface GenerateRapidBetsUseCase {
    suspend operator fun invoke(quantity: Int): AppResult<ShoppingCart>
}

class GenerateRapidBets(private val repository: RapidBettingRepository) : GenerateRapidBetsUseCase {
    override suspend fun invoke(quantity: Int) = appResultOf {
        require(quantity >= 0) { "Quantidade inválida" }
        repository.generate(quantity)
    }
}
