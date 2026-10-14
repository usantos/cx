package br.gov.caixa.loterias.feature.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.marketplace.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.core.domain.marketplace.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolOrder
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketplaceDetailViewModel(
    private val poolCode: String,
    private val getMarketplaceDetail: GetMarketplaceDetailUseCase,
    private val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val setRetailerFavorite: br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase? = null,
    private val initialFavorite: Boolean? = null,
) : ViewModel() {
    private val mutableState = MutableStateFlow(MarketplaceDetailUiState(favoriteLottery = initialFavorite))
    val state: StateFlow<MarketplaceDetailUiState> = mutableState.asStateFlow()

    init {
        load()
    }

    // init performs the first native onResume GET; later resumes refresh server availability.
    private var firstResume = true

    fun onResume() {
        if (firstResume) {
            firstResume = false
        } else {
            load()
        }
    }

    fun retry() = load()

    fun requestFavorite() {
        val current = mutableState.value
        val favorite = current.favoriteLottery ?: return
        if (setRetailerFavorite == null || current.isUpdatingFavorite || (current.detail?.lotteryId ?: 0) <= 0) return
        mutableState.value = current.copy(pendingFavorite = !favorite, favoriteError = null)
    }

    fun confirmFavorite() {
        val current = mutableState.value
        val target = current.pendingFavorite ?: return
        val detail = current.detail ?: return
        val service = setRetailerFavorite ?: return
        mutableState.value = current.copy(pendingFavorite = null, isUpdatingFavorite = true, favoriteError = null)
        viewModelScope.launch(dispatchers.io) {
            mutableState.value = when (service(detail.lotteryId, target)) {
                is AppResult.Success -> mutableState.value.copy(favoriteLottery = target, isUpdatingFavorite = false)
                is AppResult.Failure -> mutableState.value.copy(isUpdatingFavorite = false,
                    favoriteError = "Não foi possível concluir esta operação. Por favor, tente novamente.")
            }
        }
    }

    fun cancelFavorite() { mutableState.value = mutableState.value.copy(pendingFavorite = null) }
    fun dismissFavoriteError() { mutableState.value = mutableState.value.copy(favoriteError = null) }

    fun onAction(action: MarketplaceDetailAction) = when (action) {
        MarketplaceDetailAction.Retry -> retry()
        MarketplaceDetailAction.IncreaseShares -> increaseShares()
        MarketplaceDetailAction.DecreaseShares -> decreaseShares()
        MarketplaceDetailAction.AddToCart -> addToCart()
    }

    fun increaseShares() {
        val state = mutableState.value
        if (state.canIncrease) mutableState.value = state.copy(selectedShares = state.selectedShares + 1)
    }

    fun decreaseShares() {
        val state = mutableState.value
        if (state.canDecrease) mutableState.value = state.copy(selectedShares = state.selectedShares - 1)
    }

    fun addToCart() {
        val state = mutableState.value
        val detail = state.detail ?: return
        if (state.isAddingToCart || state.selectedShares !in 1..detail.availableShares) return
        mutableState.value = state.copy(isAddingToCart = true, addToCartError = null)
        viewModelScope.launch(dispatchers.io) {
            val order = MarketplacePoolOrder(
                code = detail.code,
                modality = detail.modality,
                contest = detail.contest,
                contestType = detail.contestType,
                drawDate = detail.drawDate,
                availableShares = detail.availableShares,
                requestedShares = state.selectedShares,
                serviceFeePerShare = detail.serviceFeePerShare,
                firstSharePriceWithFee = detail.lastSharePriceWithFeeValue,
                regularSharePriceWithFee = detail.regularSharePriceWithFee,
            )
            when (val outcome = addMarketplacePoolToCart(order)) {
                is AppResult.Success -> {
                    val reservation = outcome.value.items.firstOrNull { item ->
                        item.kind == CartItemKind.POOL_SHARE && item.poolShare?.code == detail.code
                    }?.poolShare
                    mutableState.value = if (reservation?.expiresAt.isNullOrBlank()) {
                        mutableState.value.copy(
                            isAddingToCart = false,
                            addToCartError = "Não foi possível confirmar a reserva da cota. Tente novamente.",
                        )
                    } else {
                        mutableState.value.copy(
                            isAddingToCart = false,
                            confirmation = MarketplacePoolConfirmation(
                                poolCode = detail.code,
                                modality = detail.modality,
                                expiresAt = requireNotNull(reservation).expiresAt,
                                contest = detail.contest,
                                drawDate = detail.drawDate.ifBlank { detail.drawDateAndTime },
                                amount = order.totalAmount,
                            ),
                        )
                    }
                }
                is AppResult.Failure -> mutableState.value = mutableState.value.copy(
                    isAddingToCart = false,
                    addToCartError = "Não foi possível adicionar a cota ao carrinho. Tente novamente.",
                )
            }
        }
    }

    fun consumeConfirmation() {
        mutableState.value = mutableState.value.copy(confirmation = null)
    }

    private fun load() {
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (val outcome = getMarketplaceDetail(poolCode)) {
                is AppResult.Success -> {
                    val detail = outcome.value
                    mutableState.value = MarketplaceDetailUiState(
                        detail = detail,
                        selectedShares = if (detail.availableShares > 0) 1 else 0,
                        isLoading = false,
                        favoriteLottery = mutableState.value.favoriteLottery,
                    )
                }
                is AppResult.Failure -> {
                    mutableState.value = MarketplaceDetailUiState(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar os detalhes do bolão. Tente novamente.",
                        favoriteLottery = mutableState.value.favoriteLottery,
                    )
                }
            }
        }
    }
}
