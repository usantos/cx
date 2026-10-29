package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingSelectionWarnings
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID

internal class BettingSelectionQuantities(
    private val state: kotlinx.coroutines.flow.StateFlow<BettingGameUiState>,
    private val dispatch: (BettingGameAction) -> Unit,
    private val warnings: BettingSelectionWarnings,
) {
    fun chooseNumberQuantity(quantity: Int) {
        val current = state.value
        dispatch(BettingGameAction.NumberQuantitySelected(quantity, !warnings.warnedAboutAdditionalNumbers))
        if (quantity !in current.numberQuantityOptions) return
        val threshold = if (current.game?.modality?.id == LOTECA_ID) {
            current.numberQuantity
        } else {
            current.numberQuantityOptions.firstOrNull() ?: 0
        }
        if (quantity > threshold) warnings.warnedAboutAdditionalNumbers = true
    }

    fun chooseRecurringDraws(draws: Int) = dispatch(BettingGameAction.RecurringDrawsSelected(draws))

    fun warnSpecialRecurringUnavailable() = dispatch(BettingGameAction.SpecialRecurringUnavailable())

    fun increaseSurpriseQuantity() = dispatch(BettingGameAction.SurpriseQuantityIncreased)

    fun decreaseSurpriseQuantity() = dispatch(BettingGameAction.SurpriseQuantityDecreased)

    fun increaseSurpriseCloverQuantity() = dispatch(BettingGameAction.SurpriseCloversIncreased)

    fun decreaseSurpriseCloverQuantity() = dispatch(BettingGameAction.SurpriseCloversDecreased)
}
