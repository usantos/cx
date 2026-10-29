package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2ModalityOrder

data class HomePreferencesUiState(
    val loaded: Boolean = false,
    val cardOrder: List<String> = HomeV2ModalityOrder.DEFAULT_ORDER,
    val shakeEnabled: Boolean = false,
    val shakeWarningHidden: Boolean = false,
    val shakeTooltipPending: Boolean = false,
    val shakeTutorialCompleted: Boolean = false,
    val errorMessage: String? = null,
)
