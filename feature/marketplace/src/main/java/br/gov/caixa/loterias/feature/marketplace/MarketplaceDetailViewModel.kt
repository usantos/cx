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
) : ViewModel() {
    private val mutableState = MutableStateFlow(MarketplaceDetailUiState())
    val state: StateFlow<MarketplaceDetailUiState> = mutableState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

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
                    )
                }
                is AppResult.Failure -> {
                    mutableState.value = MarketplaceDetailUiState(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar os detalhes do bolão. Tente novamente.",
                    )
                }
            }
        }
    }
}
