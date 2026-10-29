package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID

internal class BettingGameDraftMapper(
    private val randomTeam: (List<SportsTeam>) -> SportsTeam?,
) {
    fun map(current: BettingGameUiState, game: LotteryGameParameters, allowLocalDuplicate: Boolean): BetDraft {
        val surprise = current.mode == BetEntryMode.SURPRISE
        return BetDraft(
            modalityId = game.modality.id,
            amount = current.totalPrice,
            surpriseTeamSelected = surprise && game.modality.id == TIMEMANIA_ID && current.selectedTeam != null,
            contestNumber = game.contestNumber,
            allowLocalDuplicate = allowLocalDuplicate,
            contestType = game.contestType,
            numbers = if (surprise) emptyList() else current.selectedNumbers.sorted(),
            clovers = if (surprise) emptyList() else current.selectedClovers.sorted(),
            recurringDraws = current.recurringDraws,
            surprise = surprise,
            quantity = if (surprise) current.surpriseQuantity else 1,
            numberQuantity = current.numberQuantity,
            favoriteTeam = current.selectedTeam ?: if (surprise && game.modality.id == TIMEMANIA_ID) {
                randomTeam(game.teams)
            } else null,
            lotecaSelections = game.matches.mapNotNull { match ->
                val outcomes = current.lotecaOutcomes[match.number].orEmpty()
                if (outcomes.isEmpty()) null else LotecaMatchSelection(
                    match = match,
                    homeWin = LotecaOutcome.HOME in outcomes,
                    draw = LotecaOutcome.DRAW in outcomes,
                    awayWin = LotecaOutcome.AWAY in outcomes,
                )
            },
            lotogolSelections = game.matches.mapNotNull { match ->
                val scores = current.lotogolScores[match.number] ?: return@mapNotNull null
                LotogolMatchSelection(
                    match = match,
                    homeScore = scores.first ?: return@mapNotNull null,
                    awayScore = scores.second ?: return@mapNotNull null,
                )
            },
            superSevenColumns = if (!surprise && game.modality.id == br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID) current.superSevenColumns.map { it.sorted() } else emptyList(),
            luckyMonth = current.selectedLuckyMonth,
            generateMirror = current.generateMirror,
            cloverQuantity = if (surprise) current.surpriseCloverQuantity else current.selectedClovers.size,
        )
    }
}
