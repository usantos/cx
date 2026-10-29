package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.toggleSelectedNumber
import br.gov.caixa.loterias.feature.betting.presentation.mapper.withShake
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingSelectionWarnings
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID

internal class BettingNumberSelection(
    private val state: kotlinx.coroutines.flow.StateFlow<BettingGameUiState>,
    private val dispatch: (BettingGameAction) -> Unit,
    private val warnings: BettingSelectionWarnings,
) {
    fun fillShake(renew: Boolean) {
        val current = state.value
        val completed = current.withShake(renew)
        val increasesLoteca = current.game?.modality?.id == LOTECA_ID && completed.numberQuantity > current.numberQuantity
        val warning = if (increasesLoteca && warnings.warnedAboutAdditionalNumbers) current.warningMessage else completed.warningMessage
        if (increasesLoteca) warnings.warnedAboutAdditionalNumbers = true
        val selection = br.gov.caixa.loterias.feature.betting.presentation.state.GeneratedBettingSelection(
            completed.selectedNumbers.toSet(), completed.numberQuantity, completed.superSevenColumns.map { it.toSet() },
            completed.lotecaOutcomes.mapValues { it.value.toSet() }, completed.selectedTeam, completed.teamQuery,
            completed.selectedLuckyMonth, completed.selectedClovers.toSet(), completed.generateMirror, warning, completed.errorMessage
        )
        dispatch(BettingGameAction.ShakeSelectionGenerated(selection))
    }

    fun toggleNumber(number: Int) {
        val current = state.value
        dispatch(BettingGameAction.NumberToggled(number, !warnings.warnedAboutAdditionalNumbers))
        val game = current.game ?: return
        if (number in 1..game.maximumNumber && number !in current.selectedNumbers &&
            current.selectedNumbers.size >= current.numberQuantity && current.numberQuantity < game.maximumSelection
        ) {
            warnings.warnedAboutAdditionalNumbers = true
        }
    }

    fun selectLuckyMonth(month: LuckyMonth) = dispatch(BettingGameAction.LuckyMonthSelected(month))

    fun toggleMirror() = dispatch(BettingGameAction.MirrorToggled)

    fun toggleClover(clover: Int) = dispatch(BettingGameAction.CloverToggled(clover))

    fun chooseCloverQuantity(quantity: Int) {
        val current = state.value
        dispatch(BettingGameAction.CloverQuantitySelected(quantity, !warnings.warnedAboutAdditionalClovers))
        if (quantity in current.cloverQuantityOptions && quantity != current.manualCloverQuantity &&
            quantity > (current.game?.minimumClovers ?: quantity)
        ) {
            warnings.warnedAboutAdditionalClovers = true
        }
    }
}
