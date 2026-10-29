package br.gov.caixa.loterias.feature.responsiblegaming.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.responsibleGamingUserMessage
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingStateAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class ResponsibleGamingSocialTransfers(
    private val mutableState: ReduxStateStore<ResponsibleGamingUiState, ResponsibleGamingStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getSocialTransfers: GetSocialTransfersUseCase
) {

    fun loadSocialTransfers() = scope.launch(dispatchers.io) {
        mutableState.dispatch(ResponsibleGamingStateAction.LoadStarted)
        when (val result = getSocialTransfers()) {
            is AppResult.Success -> mutableState.dispatch(ResponsibleGamingStateAction.SocialTransfersLoaded(result.value))
            is AppResult.Failure -> mutableState.dispatch(ResponsibleGamingStateAction.Failed(result.error.responsibleGamingUserMessage()))
        }
    }
}
