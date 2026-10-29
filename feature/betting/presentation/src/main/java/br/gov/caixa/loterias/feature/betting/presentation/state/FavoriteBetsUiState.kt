package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText

data class FavoriteBetsUiState(
    val loading: Boolean = true,
    val loadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val dailyLimit: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    val suspensionNotice: Boolean = false,
    val dailyLimitConfirmation: Boolean = false,
    val duplicateConfirmation: Boolean = false,
    val favorites: List<FavoriteBet> = emptyList(),
    val modalities: List<LotteryModality> = emptyList(),
    val games: List<LotteryGameParameters> = emptyList(),
    val selectedModalityId: Int? = null,
    val deleteCandidate: FavoriteBet? = null,
    val addCandidate: FavoriteBet? = null,
    val contestType: Int = 1,
    val recurringDraws: Int = 0,
    val mirror: Boolean = false,
    val addedToCart: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedModalityName: String
        get() = modalities.firstOrNull { it.id == selectedModalityId }?.name ?: messageText("todas_aff4d1")
    val recurringOptions: List<Int>
        get() = addCandidate?.let { bet ->
            games.firstOrNull { game ->
                game.modality.id == bet.modalityId &&
                    game.contestType.equals(if (contestType == 2) "ESPECIAL" else "NORMAL", true)
            }?.recurringOptions
        }.orEmpty().distinct()
    val recurringSelectionIndex: Int
        get() = recurringOptions.indexOf(recurringDraws).coerceAtLeast(0)
    val specialAvailable: Boolean
        get() = addCandidate?.let { bet ->
            games.any { it.modality.id == bet.modalityId && it.contestType.equals("ESPECIAL", true) }
        } == true
    val mirrorAvailable: Boolean
        get() = addCandidate?.modalityName?.contains("lotomania", ignoreCase = true) == true
}
