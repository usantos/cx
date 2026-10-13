package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.RapidBetConfiguration
import br.gov.caixa.loterias.core.domain.betting.RapidBettingRepository
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.network.CartDto
import br.gov.caixa.loterias.core.network.FavoriteCartBetDto
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.LotteryModalityDto
import br.gov.caixa.loterias.core.network.RapidConfigurationDto
import java.math.BigDecimal

interface RapidBettingRemoteDataSource {
    suspend fun configuration(): RapidConfigurationDto?
    suspend fun save(configuration: RapidConfigurationDto): RapidConfigurationDto?
    suspend fun generate(quantity: Int): CartDto?
}

class RetrofitRapidBettingRemoteDataSource(
    private val provider: LegacyBackendApiProvider,
) : RapidBettingRemoteDataSource {
    override suspend fun configuration() = provider.current().rapidConfiguration().payload
    override suspend fun save(configuration: RapidConfigurationDto) =
        provider.current().saveRapidConfiguration(configuration).payload
    override suspend fun generate(quantity: Int) = provider.current().generateRapidBets(quantity).payload
}

class RemoteRapidBettingRepository(
    private val remote: RapidBettingRemoteDataSource,
) : RapidBettingRepository {
    override suspend fun configuration(): RapidBetConfiguration =
        requireNotNull(remote.configuration()) { "Configuração do Rapidão sem payload" }.toDomain()

    override suspend fun save(configuration: RapidBetConfiguration): RapidBetConfiguration =
        (remote.save(configuration.toDto()) ?: configuration.toDto()).toDomain()

    override suspend fun generate(quantity: Int): ShoppingCart = remote.generate(quantity).toDomainCart()

    private fun RapidConfigurationDto.toDomain() = RapidBetConfiguration(
        minimumAmount = minimumAmount ?: BigDecimal.ZERO,
        maximumAmount = maximumAmount ?: BigDecimal.ZERO,
        minimumPrize = minimumPrize ?: BigDecimal.ZERO,
        requiredNumbers = requiredNumbers.orEmpty().filter { it > 0 }.toSet(),
        excludedNumbers = excludedNumbers.orEmpty().filter { it > 0 }.toSet(),
        modalities = modalities.orEmpty().map { modality ->
            LotteryModality(
                id = modality.id ?: 0,
                name = modality.name.orEmpty(),
                code = modality.managerName.orEmpty(),
                specialName = modality.specialName.orEmpty(),
                managerName = modality.managerName.orEmpty(),
            )
        }.filter { it.id > 0 },
        favoriteBetIds = favoriteBets.orEmpty().mapNotNull(FavoriteCartBetDto::id).filter { it > 0 }.toSet(),
    )

    private fun RapidBetConfiguration.toDto() = RapidConfigurationDto(
        minimumAmount = minimumAmount,
        maximumAmount = maximumAmount,
        minimumPrize = minimumPrize,
        requiredNumbers = requiredNumbers.sorted(),
        excludedNumbers = excludedNumbers.sorted(),
        modalities = modalities.map { modality ->
            LotteryModalityDto(modality.id, modality.name, modality.specialName, modality.managerName.ifBlank { modality.code })
        },
        favoriteBets = favoriteBetIds.sorted().map { FavoriteCartBetDto(id = it) },
    )
}
