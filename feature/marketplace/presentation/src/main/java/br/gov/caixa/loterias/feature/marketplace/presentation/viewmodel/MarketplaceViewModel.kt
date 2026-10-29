package br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.coordinator.MarketplaceCartCoordinator
import br.gov.caixa.loterias.feature.marketplace.presentation.coordinator.MarketplaceFavoriteRetailersCoordinator
import br.gov.caixa.loterias.feature.marketplace.presentation.coordinator.MarketplaceLoadingCoordinator
import br.gov.caixa.loterias.feature.marketplace.presentation.coordinator.MarketplacePoolFavoriteCoordinator
import br.gov.caixa.loterias.feature.marketplace.presentation.coordinator.MarketplaceSharesCoordinator
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.reduceMarketplaceState
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import kotlinx.coroutines.flow.StateFlow

class MarketplaceViewModel(
    private val searchMarketplace: SearchMarketplaceUseCase,
    private val filter: MarketplaceFilter,
    private val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase,
    private val getFavoriteRetailers: GetFavoriteRetailersUseCase,
    private val setRetailerFavorite: SetRetailerFavoriteUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val getBettingParameters: br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase? = null,
    private val filteredSearch: Boolean = false,
    private val getSpecialContests: br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase? = null,
    initialTab: MarketplaceTab = MarketplaceTab.POOLS,
) : ViewModel() {
    private var lastChildRevision: Int? = null
    private val mutableState = reduxStateStore<MarketplaceUiState, MarketplaceStateAction>(
        MarketplaceUiState(
            selectedTab = initialTab,
            isLoading = true,
            lotteryFiltered = filter.lottery.isNotBlank(),
            showModalityInList = filter.lottery.isNotBlank() && filter.modalityId == null
        ),
        ::reduceMarketplaceState
    ).also { addCloseable(it) }
    val state: StateFlow<MarketplaceUiState> = mutableState.state

    private val loading = MarketplaceLoadingCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        searchMarketplace,
        filter,
        getBettingParameters,
        filteredSearch,
        getSpecialContests
    )
    private val favoriteRetailers = MarketplaceFavoriteRetailersCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        getFavoriteRetailers,
        setRetailerFavorite
    )
    private val poolFavorites = MarketplacePoolFavoriteCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        setRetailerFavorite,
        favoriteRetailers::onPoolFavoriteChanged
    )
    private val shares = MarketplaceSharesCoordinator(mutableState)
    private val cart = MarketplaceCartCoordinator(mutableState, viewModelScope, dispatchers, addMarketplacePoolToCart)

    init {
        search()
        loadFavoriteRetailers()
        loadContestHeader()
    }

    fun observeChildRevision(revision: Int) {
        val previous = lastChildRevision
        lastChildRevision = revision
        if (previous != null && previous != revision) {
            retry()
            loadFavoriteRetailers()
        }
    }

    fun retry() {
        search()
        loadContestHeader()
    }

    private fun loadContestHeader() = loading.loadContestHeader()

    fun onAction(action: MarketplaceAction) = when (action) {
        MarketplaceAction.Retry -> retry()
        MarketplaceAction.LoadMore -> loadMore()
        is MarketplaceAction.ToggleExpanded -> toggleExpanded(action.poolCode)
        is MarketplaceAction.IncreaseShares -> changeShares(action.poolCode, 1)
        is MarketplaceAction.DecreaseShares -> changeShares(action.poolCode, -1)
        is MarketplaceAction.RequestFavorite -> requestFavorite(action.poolCode)
        MarketplaceAction.ConfirmFavorite -> confirmFavorite()
        MarketplaceAction.CancelFavorite -> cancelFavorite()
        is MarketplaceAction.AddToCart -> addToCart(action.poolCode)
        MarketplaceAction.DismissActionError -> dismissActionError()
        is MarketplaceAction.SelectTab -> selectTab(action.tab)
        is MarketplaceAction.RequestFavoriteRetailerRemoval -> requestFavoriteRetailerRemoval(action.retailerId)
        MarketplaceAction.ConfirmFavoriteRetailerRemoval -> confirmFavoriteRetailerRemoval()
        MarketplaceAction.CancelFavoriteRetailerRemoval -> cancelFavoriteRetailerRemoval()
    }

    fun selectTab(tab: MarketplaceTab) {
        mutableState.dispatch(MarketplaceStateAction.TabSelected(tab))
    }

    fun requestFavoriteRetailerRemoval(retailerId: Long) = favoriteRetailers.requestFavoriteRetailerRemoval(retailerId)

    fun cancelFavoriteRetailerRemoval() = favoriteRetailers.cancelFavoriteRetailerRemoval()

    fun confirmFavoriteRetailerRemoval() = favoriteRetailers.confirmFavoriteRetailerRemoval()

    fun loadMore() = loading.loadMore()

    fun toggleExpanded(poolCode: String) {
        mutableState.dispatch(MarketplaceStateAction.PoolExpanded(poolCode))
    }

    fun increaseShares(poolCode: String) = changeShares(poolCode, 1)

    fun decreaseShares(poolCode: String) = changeShares(poolCode, -1)

    private fun changeShares(poolCode: String, delta: Int) = shares.changeShares(poolCode, delta)

    fun requestFavorite(poolCode: String) = poolFavorites.requestFavorite(poolCode)

    fun cancelFavorite() = poolFavorites.cancelFavorite()

    fun confirmFavorite() = poolFavorites.confirmFavorite()

    fun addToCart(poolCode: String) = cart.addToCart(poolCode)

    fun consumeConfirmation() = cart.consumeConfirmation()

    fun dismissActionError() {
        mutableState.dispatch(MarketplaceStateAction.ActionErrorDismissed)
    }

    private fun search() = loading.search()

    fun refreshFavoriteRetailers() = favoriteRetailers.loadFavoriteRetailers(synchronizePools = true)

    private fun loadFavoriteRetailers() = favoriteRetailers.loadFavoriteRetailers()
}
