package br.gov.caixa.loterias.feature.responsiblegaming.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.canSubmitSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.responsibleGamingUserMessage
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingStateAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.suspensionHours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class ResponsibleGamingSuspension(
    private val mutableState: ReduxStateStore<ResponsibleGamingUiState, ResponsibleGamingStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val suspendAccount: SuspendAccountUseCase,
    private val serverTimeSource: br.gov.caixa.loterias.core.common.time.ServerTimeSource
) {

    fun confirmSuspension() {
        val current = mutableState.value
        if (canSubmitSuspension(current.selectedSuspensionPeriod, current.suspensionAcknowledged)) {
            mutableState.dispatch(ResponsibleGamingStateAction.SuspensionConfirmed)
        }
    }

    @Synchronized
    fun submitSuspension() {
        if (mutableState.value.loading || mutableState.value.suspension != null) return
        val hours = suspensionHours(mutableState.value.selectedSuspensionPeriod) ?: return
        mutableState.dispatch(ResponsibleGamingStateAction.LoadStarted)
        scope.launch(dispatchers.io) {
            when (val result = suspendAccount(hours)) {
                is AppResult.Success -> {
                    val end = serverTimeSource.now().plusSeconds(hours * 3_600L)
                        .atZone(java.time.ZoneId.of("America/Sao_Paulo"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy', às 'HH:mm'.'"))
                    mutableState.dispatch(ResponsibleGamingStateAction.Suspended(result.value, end))
                }

                is AppResult.Failure -> mutableState.dispatch(
                    ResponsibleGamingStateAction.Failed(result.error.responsibleGamingUserMessage())
                )
            }
        }
    }
}
