package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartBetDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.RapidConfigurationDto
import java.math.BigDecimal

internal fun RapidConfigurationDto.toDomain() = RapidBetConfiguration(
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

internal fun RapidBetConfiguration.toDto() = RapidConfigurationDto(
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
