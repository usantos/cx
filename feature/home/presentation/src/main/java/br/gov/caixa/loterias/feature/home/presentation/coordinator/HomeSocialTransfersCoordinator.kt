package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class HomeSocialTransfersCoordinator(
    private val mutableState: ReduxStateStore<HomeUiState, HomeStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getSocialTransfers: GetSocialTransfersUseCase
) {
    private var socialTransfersRequested = false

    fun loadSocialTransfers() {
        if (socialTransfersRequested || mutableState.value.socialTransfers != null) return
        socialTransfersRequested = true
        scope.launch(dispatchers.io) {
            mutableState.dispatch(HomeStateAction.SocialTransfersLoading)
            when (val result = getSocialTransfers()) {
                is AppResult.Success -> mutableState.dispatch(HomeStateAction.SocialTransfersReceived(result.value))

                is AppResult.Failure -> {
                    socialTransfersRequested = false
                    mutableState.dispatch(HomeStateAction.SocialTransfersFailed)
                }
            }
        }
    }
}
