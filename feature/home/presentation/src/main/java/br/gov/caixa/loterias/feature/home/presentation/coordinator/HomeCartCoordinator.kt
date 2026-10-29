package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class HomeCartCoordinator(
    private val mutableState: ReduxStateStore<HomeUiState, HomeStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val cartRequestVersion: AtomicLong,
    private val getCurrentCart: GetCurrentCartUseCase
) {
    fun refreshCart() {
        if (mutableState.value.loadingCart) return
        scope.launch(dispatchers.io) {
            val requestVersion = cartRequestVersion.incrementAndGet()
            mutableState.dispatch(HomeStateAction.CartLoading)
            when (val result = getCurrentCart()) {
                is AppResult.Success -> synchronized(mutableState) {
                    mutableState.dispatch(HomeStateAction.CartReceived(requestVersion == cartRequestVersion.get(), result.value))
                }

                is AppResult.Failure -> synchronized(mutableState) {
                    mutableState.dispatch(HomeStateAction.CartFailed(requestVersion == cartRequestVersion.get()))
                }
            }
        }
    }
}
