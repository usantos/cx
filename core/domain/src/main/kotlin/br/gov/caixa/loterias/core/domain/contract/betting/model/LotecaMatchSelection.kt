package br.gov.caixa.loterias.core.domain.contract.betting.model

import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsMatch

data class LotecaMatchSelection(
    val match: SportsMatch,
    val homeWin: Boolean,
    val draw: Boolean,
    val awayWin: Boolean,
)
