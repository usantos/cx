package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality

private const val MAXIMUM_COMBO_QUANTITY = 15

internal sealed interface BettingCatalogAction {
    data class LoadingStarted(val mode: BettingCatalogMode) : BettingCatalogAction
    data object QuantityIncreased : BettingCatalogAction
    data object QuantityDecreased : BettingCatalogAction
    data object SurpriseToggled : BettingCatalogAction
    data object CartAdditionStarted : BettingCatalogAction
    data class RecoveryRequired(val message: String) : BettingCatalogAction
    data object CartAdditionConsumed : BettingCatalogAction
    data object AddedToCart : BettingCatalogAction
    data class ModalitiesLoaded(val modalities: List<LotteryModality>, val parameters: BettingParameters) : BettingCatalogAction
    data class CombosLoaded(val combos: List<BetCombo>, val selected: BetCombo?, val parameters: BettingParameters?) : BettingCatalogAction
    data class Failed(val message: String) : BettingCatalogAction
}

internal fun reduceBettingCatalogState(state: BettingCatalogUiState, action: BettingCatalogAction): BettingCatalogUiState = when (action) {
    is BettingCatalogAction.LoadingStarted -> reduceBettingCatalogLoadingStarted(state, action)
    BettingCatalogAction.QuantityIncreased -> reduceBettingCatalogQuantityIncreased(state)
    BettingCatalogAction.QuantityDecreased -> reduceBettingCatalogQuantityDecreased(state)
    BettingCatalogAction.SurpriseToggled -> reduceBettingCatalogSurpriseToggled(state)
    BettingCatalogAction.CartAdditionStarted -> reduceBettingCatalogCartAdditionStarted(state)
    is BettingCatalogAction.RecoveryRequired -> reduceBettingCatalogRecoveryRequired(state, action)
    BettingCatalogAction.CartAdditionConsumed -> state.copy(addedToCart = false)
    BettingCatalogAction.AddedToCart -> reduceBettingCatalogAddedToCart(state)
    is BettingCatalogAction.ModalitiesLoaded -> reduceBettingCatalogModalitiesLoaded(state, action)
    is BettingCatalogAction.CombosLoaded -> reduceBettingCatalogCombosLoaded(state, action)
    is BettingCatalogAction.Failed -> reduceBettingCatalogFailed(state, action)
}

private fun reduceBettingCatalogLoadingStarted(
    state: BettingCatalogUiState,
    action: BettingCatalogAction.LoadingStarted,
): BettingCatalogUiState = run {
    val it = state
    it.copy(
        loading = true,
        errorMessage = null,
        selectedCombo = if (action.mode == BettingCatalogMode.COMBO_DETAIL) null else it.selectedCombo,
        parameters = if (action.mode == BettingCatalogMode.COMBO_DETAIL) null else it.parameters
    )
}

private fun reduceBettingCatalogQuantityIncreased(state: BettingCatalogUiState): BettingCatalogUiState = run {
    val it = state
    it.copy(comboQuantity = (it.comboQuantity + 1).coerceAtMost(MAXIMUM_COMBO_QUANTITY))
}

private fun reduceBettingCatalogQuantityDecreased(state: BettingCatalogUiState): BettingCatalogUiState = run {
    val it = state
    it.copy(comboQuantity = (it.comboQuantity - 1).coerceAtLeast(1))
}

private fun reduceBettingCatalogSurpriseToggled(state: BettingCatalogUiState): BettingCatalogUiState = run {
    val it = state
    it.copy(surprise = !it.surprise)
}

private fun reduceBettingCatalogCartAdditionStarted(state: BettingCatalogUiState): BettingCatalogUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null)
}

private fun reduceBettingCatalogRecoveryRequired(
    state: BettingCatalogUiState,
    action: BettingCatalogAction.RecoveryRequired,
): BettingCatalogUiState = run {
    val it = state
    it.copy(loading = false, errorMessage = action.message)
}

private fun reduceBettingCatalogAddedToCart(state: BettingCatalogUiState): BettingCatalogUiState = run {
    val it = state
    it.copy(loading = false, addedToCart = true)
}

private fun reduceBettingCatalogModalitiesLoaded(
    state: BettingCatalogUiState,
    action: BettingCatalogAction.ModalitiesLoaded,
): BettingCatalogUiState = run {
    val it = state
    it.copy(
        loading = false,
        modalities = action.modalities.filter { modality -> modality.id > 0 },
        parameters = action.parameters,
        errorMessage = null
    )
}

private fun reduceBettingCatalogCombosLoaded(
    state: BettingCatalogUiState,
    action: BettingCatalogAction.CombosLoaded,
): BettingCatalogUiState = run {
    val it = state
    it.copy(
        loading = false,
        combos = action.combos,
        selectedCombo = action.selected,
        parameters = action.parameters,
        errorMessage = null
    )
}

private fun reduceBettingCatalogFailed(state: BettingCatalogUiState, action: BettingCatalogAction.Failed): BettingCatalogUiState = run {
    val it = state
    it.copy(loading = false, errorMessage = action.message)
}
