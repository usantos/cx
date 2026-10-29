package br.gov.caixa.loterias.feature.marketplace.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState

internal class MarketplaceSharesCoordinator(private val mutableState: ReduxStateStore<MarketplaceUiState, MarketplaceStateAction>) {
    fun changeShares(poolCode: String, delta: Int) {
        mutableState.dispatch(MarketplaceStateAction.SharesChanged(poolCode, delta))
    }
}
