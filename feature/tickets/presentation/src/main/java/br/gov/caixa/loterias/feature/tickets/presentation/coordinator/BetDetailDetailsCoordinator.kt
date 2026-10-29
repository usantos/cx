package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.toReceiptMessage
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class BetDetailDetailsCoordinator(
    private val mutableState: ReduxStateStore<BetDetailUiState, BetDetailStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val recoverBet: RecoverConfirmedBetUseCase,
    private val loadConfirmedBet: br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase?,
    private val getConfirmedBetResults: br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase?
) {
    fun loadDetails() = scope.launch(dispatchers.io) {
        val bet = mutableState.value.bet
        mutableState.dispatch(BetDetailStateAction.DetailsLoading)
        val loader = loadConfirmedBet
        if (loader == null) {
            when (val result = recoverBet(bet)) {
                is AppResult.Success -> mutableState.dispatch(BetDetailStateAction.DetailsLoaded(result.value))
                is AppResult.Failure -> mutableState.dispatch(BetDetailStateAction.RecoveryFailed(bet, result.error.toReceiptMessage()))
            }
        } else {
            when (val result = loader(bet)) {
                is br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Success -> mutableState.dispatch(
                    BetDetailStateAction.DetailsLoaded(result.value)
                )

                is br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Failed -> mutableState.dispatch(
                    BetDetailStateAction.DetailsFailed(result.error.toReceiptMessage())
                )

                is br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Rejected -> mutableState.dispatch(
                    BetDetailStateAction.ServiceRejected(result.failure)
                )
            }
        }
        val current = mutableState.value
        if (current.detailsReady && current.bet.statusId !in setOf(4L, 101L) && getConfirmedBetResults != null) {
            when (val result = getConfirmedBetResults.invoke(current.bet)) {
                is br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Success -> mutableState.dispatch(
                    BetDetailStateAction.DrawResultsReceived(result.value)
                )

                // As ações de recibo e repetição continuam disponíveis quando os resultados públicos estão temporariamente indisponíveis.
                else -> Unit
            }
        }
    }

    fun dismissServiceFailure() = mutableState.dispatch(BetDetailStateAction.ServiceFailureDismissed)
}
