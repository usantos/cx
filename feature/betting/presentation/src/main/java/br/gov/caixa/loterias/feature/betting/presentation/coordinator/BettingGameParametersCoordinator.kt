package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.availableNumberQuantities
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class BettingGameParametersCoordinator(
    private val modalityId: Int?,
    private val getParameters: GetBettingParametersUseCase,
    private val initialContestTypeId: Long,
    private val seed: br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameSeed,
    private val expectedContestNumber: Int?,
    private val viewModelScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val dispatch: (BettingGameAction) -> Unit,
    private val applyFailure: (AppError) -> Unit,
) {

    fun load() = viewModelScope.launch(dispatchers.io) {
        dispatch(BettingGameAction.ParametersLoading)
        when (val result = getParameters()) {
            is AppResult.Failure -> {
                dispatch(BettingGameAction.GameUnavailable)
                applyFailure(result.error)
            }

            is AppResult.Success -> applyParameters(result.value)
        }
    }

    fun applyParameters(parameters: BettingParameters) {
        val game = selectGame(parameters)
        if (game == null) {
            dispatch(BettingGameAction.GameUnavailable)
            applyFailure(AppError.Validation(messageText("nenhuma_modalidade_disponivel_pa_88a68e")))
        } else {
            dispatch(BettingGameAction.ParametersLoaded(game, parameters, seed))
        }
    }

    fun selectGame(parameters: BettingParameters): LotteryGameParameters? {
        val requestedContestType = when (initialContestTypeId) {
            1L -> "NORMAL"
            2L -> "ESPECIAL"
            else -> null
        }
        val candidates = parameters.games.filter {
            (modalityId == null || it.modality.id == modalityId) &&
                (requestedContestType == null || it.contestType.equals(requestedContestType, true)) &&
                (expectedContestNumber == null || it.contestNumber == expectedContestNumber)
        }
        return if (modalityId == null) {
            candidates.firstOrNull { it.open } ?: candidates.firstOrNull()
        } else {
            candidates.firstOrNull()
        }
    }
}
