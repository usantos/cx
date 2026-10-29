package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class TicketsManualQueryCoordinator(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val viewModelScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val fail: (String) -> Unit,
    private val checkTicket: CheckTicketUseCase,
) {
    private companion object {
        const val MINIMUM_MANUAL_TICKET_LENGTH = TicketsViewModel.MINIMUM_MANUAL_TICKET_LENGTH
        const val MINIMUM_SCANNED_TICKET_LENGTH = 23
    }

    fun changeTicketCode(value: String) {
        mutableState.dispatch(TicketsStateAction.TicketCodeChanged(value))
    }

    fun changeTicketRetailerCode(value: String) {
        mutableState.dispatch(TicketsStateAction.TicketRetailerCodeChanged(value))
    }

    fun submitScannedTicketCode(value: String, orientation: TicketScanOrientation) {
        // O analisador legado pausa durante o fluxo de resultado; quadros posteriores não devem dispensar um erro
        // nem substituir o bilhete enquanto sua requisição estiver pendente.
        val current = mutableState.value
        if (current.isLoading || current.errorMessage != null) return
        if (value.length < MINIMUM_SCANNED_TICKET_LENGTH) return
        val query = checkTicket.prepareScannedQuery(value, orientation) ?: return
        mutableState.dispatch(TicketsStateAction.ScannedTicketAccepted(value))
        checkQuery(query)
    }

    fun checkTicket(qrCode: Boolean = false) {
        val code = mutableState.value.ticketCode
        if (code.isBlank() || (!qrCode && code.length < MINIMUM_MANUAL_TICKET_LENGTH)) {
            mutableState.dispatch(TicketsStateAction.TicketCodeRejected())
            return
        }
        val retailerCode = mutableState.value.ticketRetailerCode
        checkQuery(TicketQuery(code, qrCode || retailerCode.isNotBlank(), retailerCode))
    }

    private fun checkQuery(query: TicketQuery) {
        if (mutableState.value.isLoading) return
        mutableState.dispatch(TicketsStateAction.TicketQueryStarted)
        viewModelScope.launch(dispatchers.io) {
            when (val result = checkTicket.result(query)) {
                is AppResult.Success -> mutableState.dispatch(TicketsStateAction.TicketResultReceived(result.value))
                is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_os_dad_bf517e"))
            }
        }
    }

    fun consumeTicketResultNavigation(): Boolean = synchronized(mutableState) {
        val current = mutableState.value
        if (!current.loaded || current.errorMessage != null || (current.ticketResult == null && current.bets.isEmpty())) return false
        // Mantém o resultado para o destino de detalhes, mas não repete a navegação após retornar.
        mutableState.dispatch(TicketsStateAction.TicketNavigationConsumed)
        true
    }

    fun dismissTicketError() {
        mutableState.dispatch(TicketsStateAction.TicketErrorDismissed)
    }
}
