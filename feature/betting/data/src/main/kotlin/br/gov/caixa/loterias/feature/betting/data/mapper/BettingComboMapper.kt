package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboDto
import java.math.BigDecimal

internal fun BetComboDto.toBettingCombo() = BetCombo(
    id = type?.id ?: 0,
    name = type?.name.orEmpty().ifBlank { "Combo" },
    description = type?.description.orEmpty(),
    new = type?.isNew == true,
    amount = amount ?: BigDecimal.ZERO,
    entries = entries.orEmpty().map { entry ->
        BetComboEntry(
            modality = entry.modality?.toBettingModality() ?: LotteryModality(0, "", ""),
            contestType = entry.contestType.orEmpty(),
            betQuantity = entry.betQuantity ?: 0,
            recurringDraws = entry.recurringDraws ?: 0,
            numberQuantity = entry.numberQuantity ?: 0,
            cloverQuantity = entry.cloverQuantity ?: 0,
            contestNumber = entry.contestNumber ?: 0,
        )
    },
)
