package br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.marketplace.presentation.state.reduceMarketplaceDetailState
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketplaceDetailViewModel(
    private val poolCode: String,
    private val getMarketplaceDetail: GetMarketplaceDetailUseCase,
    private val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val setRetailerFavorite: br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase? = null,
    private val initialFavorite: Boolean? = null,
    private val purchaseEnabled: Boolean = true,
    private val getSpecialContests: br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase? = null,
) : ViewModel() {
    private var loadJob: Job? = null
    private var loadGeneration = 0L
    private var missingCloversRetries = 0
    private val mutableState = reduxStateStore<MarketplaceDetailUiState, MarketplaceDetailStateAction>(
        MarketplaceDetailUiState(favoriteLottery = initialFavorite),
        ::reduceMarketplaceDetailState
    ).also { addCloseable(it) }
    val state: StateFlow<MarketplaceDetailUiState> = mutableState.state

    init {
        load()
    }

    // init executa o primeiro GET do onResume nativo; retomadas posteriores atualizam a disponibilidade no servidor.
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
        mutableState.dispatch(MarketplaceDetailStateAction.FavoriteRequested(!favorite))
    }

    fun confirmFavorite() {
        val current = mutableState.value
        val target = current.pendingFavorite ?: return
        val detail = current.detail ?: return
        val service = setRetailerFavorite ?: return
        mutableState.dispatch(MarketplaceDetailStateAction.FavoriteStarted)
        viewModelScope.launch(dispatchers.io) {
            when (service(detail.lotteryId, target)) {
                is AppResult.Success -> mutableState.dispatch(MarketplaceDetailStateAction.FavoriteSaved(target))

                is AppResult.Failure -> mutableState.dispatch(
                    MarketplaceDetailStateAction.FavoriteFailed(
                        messageText("nao_foi_possivel_concluir_esta_o_3c7db3")
                    )
                )
            }
        }
    }

    fun cancelFavorite() {
        mutableState.dispatch(MarketplaceDetailStateAction.FavoriteCancelled)
    }
    fun dismissFavoriteError() {
        mutableState.dispatch(MarketplaceDetailStateAction.FavoriteErrorDismissed)
    }

    fun onAction(action: MarketplaceDetailAction) = when (action) {
        MarketplaceDetailAction.Retry -> retry()
        MarketplaceDetailAction.IncreaseShares -> increaseShares()
        MarketplaceDetailAction.DecreaseShares -> decreaseShares()
        MarketplaceDetailAction.AddToCart -> addToCart()
    }

    fun increaseShares() {
        val state = mutableState.value
        if (state.canIncrease) mutableState.dispatch(MarketplaceDetailStateAction.SharesIncreased)
    }

    fun decreaseShares() {
        val state = mutableState.value
        if (state.canDecrease) mutableState.dispatch(MarketplaceDetailStateAction.SharesDecreased)
    }

    fun addToCart() {
        val state = mutableState.value
        val detail = state.detail ?: return
        if (state.isAddingToCart || state.selectedShares !in 1..detail.availableShares) return
        mutableState.dispatch(MarketplaceDetailStateAction.CartAdditionStarted)
        viewModelScope.launch(dispatchers.io) {
            val order = MarketplacePoolOrder(
                origin = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrderOrigin.DETAIL,
                code = detail.code,
                modality = detail.modality,
                contest = detail.contest,
                contestType = detail.contestType,
                drawDate = detail.drawDate,
                availableShares = detail.availableShares,
                requestedShares = state.selectedShares,
                serviceFeePerShare = detail.serviceFeePerShare,
                firstSharePriceWithFee = detail.lastSharePriceWithFeeValue,
                regularSharePriceWithFee = detail.regularSharePriceWithFee
            )
            when (val outcome = addMarketplacePoolToCart(order)) {
                is AppResult.Success -> {
                    val reservation = outcome.value.items.firstOrNull { item ->
                        item.kind == CartItemKind.POOL_SHARE && item.poolShare?.code == detail.code
                    }?.poolShare
                    if (reservation?.expiresAt.isNullOrBlank()) {
                        mutableState.dispatch(
                            MarketplaceDetailStateAction.CartAdditionFailed(messageText("nao_foi_possivel_confirmar_a_res_4afb21"))
                        )
                    } else {
                        mutableState.dispatch(
                            MarketplaceDetailStateAction.CartReservationReceived(
                                MarketplacePoolConfirmation(
                                    poolCode = detail.code,
                                    modality = detail.modality,
                                    expiresAt = requireNotNull(reservation).expiresAt,
                                    contest = detail.contest,
                                    drawDate = detail.drawDate.ifBlank { detail.drawDateAndTime },
                                    amount = order.totalAmount
                                )
                            )
                        )
                    }
                }

                is AppResult.Failure -> mutableState.dispatch(
                    MarketplaceDetailStateAction.CartAdditionFailed(messageText("nao_foi_possivel_adicionar_a_cot_6eae19"))
                )
            }
        }
    }

    fun consumeConfirmation() {
        mutableState.dispatch(MarketplaceDetailStateAction.ConfirmationConsumed)
    }

    fun consumeSoldOutNotice() {
        mutableState.dispatch(MarketplaceDetailStateAction.SoldOutNoticeConsumed)
    }

    private fun load() {
        val current = mutableState.value
        if (current.isAddingToCart || current.isUpdatingFavorite) return
        loadJob?.cancel()
        val generation = ++loadGeneration
        mutableState.dispatch(MarketplaceDetailStateAction.LoadingStarted)
        loadJob = viewModelScope.launch(dispatchers.io) {
            var outcome = getMarketplaceDetail(poolCode)
            currentCoroutineContext().ensureActive()
            if (generation != loadGeneration) return@launch
            while (outcome is AppResult.Success &&
                outcome.value.modality.replace("_", "").replace("-", "").equals("MAISMILIONARIA", true) &&
                outcome.value.bets.firstOrNull()?.cloversLoaded == false && missingCloversRetries < 3
            ) {
                missingCloversRetries++
                outcome = getMarketplaceDetail(poolCode)
                currentCoroutineContext().ensureActive()
                if (generation != loadGeneration) return@launch
            }
            when (outcome) {
                is AppResult.Success -> {
                    val detail = outcome.value
                    val specialContests = (getSpecialContests?.invoke() as? AppResult.Success)?.value
                    currentCoroutineContext().ensureActive()
                    if (generation != loadGeneration) return@launch
                    mutableState.dispatch(
                        MarketplaceDetailStateAction.DetailReceived(
                            detail = detail,
                            specialName = specialContests?.label(detail.modality, detail.contestType, detail.contest),
                            soldOutNotice = if (purchaseEnabled &&
                                detail.availableShares == 0
                            ) {
                                messageText("apostas_sem_quantidade_minina_ne_697e36")
                            } else {
                                null
                            }
                        )
                    )
                }

                is AppResult.Failure -> {
                    mutableState.dispatch(
                        MarketplaceDetailStateAction.LoadingFailed(messageText("nao_foi_possivel_carregar_os_det_245a06"))
                    )
                }
            }
        }
    }
}
