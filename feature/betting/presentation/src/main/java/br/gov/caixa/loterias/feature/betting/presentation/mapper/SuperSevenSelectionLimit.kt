package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.feature.betting.domain.rule.LotterySelectionPolicy

internal fun superSevenMaximumPerColumn(numberQuantity: Int) =
    LotterySelectionPolicy.superSevenMaximumPerColumn(numberQuantity)

