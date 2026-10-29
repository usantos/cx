package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality

enum class BettingCatalogMode { MODALITIES, COMBOS, COMBO_DETAIL }

data class BettingCatalogUiState(
    val loading: Boolean = true,
    val modalities: List<LotteryModality> = emptyList(),
    val parameters: BettingParameters? = null,
    val combos: List<BetCombo> = emptyList(),
    val selectedCombo: BetCombo? = null,
    val comboQuantity: Int = 1,
    val surprise: Boolean = true,
    val addedToCart: Boolean = false,
    val errorMessage: String? = null,
)
