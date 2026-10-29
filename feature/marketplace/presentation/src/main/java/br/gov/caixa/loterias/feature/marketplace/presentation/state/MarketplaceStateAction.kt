package br.gov.caixa.loterias.feature.marketplace.presentation.state
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab

internal sealed interface MarketplaceStateAction {
    data class HeaderReceived(
        val stale: Boolean,
        val game: br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters?,
        val names: Map<Pair<String, Int>, String>,
        val configured: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests,
        val specialName: String?,
    ) : MarketplaceLoadingAction
    data class SearchStarted(val append: Boolean) : MarketplaceLoadingAction
    data class SearchReceived(
        val stale: Boolean,
        val append: Boolean,
        val result: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage,
    ) : MarketplaceLoadingAction
    data class SearchFailed(
        val stale: Boolean,
        val filteredSearch: Boolean,
        val message: String = messageText("nao_foram_encontrados_boloes_dis_ecef7d"),
    ) : MarketplaceLoadingAction
    data class SharesChanged(val poolCode: String, val delta: Int) : MarketplacePoolAction
    data class CartAdditionClaimed(val poolCode: String) : MarketplaceCartAction
    data class CartReservationReceived(
        val reservation: br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare?,
        val poolCode: String,
        val shares: Int,
        val pool: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool,
        val order: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder,
        val message: String = messageText("nao_foi_possivel_confirmar_a_res_4afb21"),
    ) : MarketplaceCartAction
    data class CartAdditionFailed(val poolCode: String, val message: String = messageText("nao_foi_possivel_adicionar_a_cot_6eae19")) :
        MarketplaceCartAction
    data object ConfirmationConsumed : MarketplaceCartAction
    data class PoolFavoriteRemoved(val retailerId: Long) : MarketplaceRetailerAction
    data class RetailerRemovalRequested(val retailerId: Long) : MarketplaceRetailerAction
    data object RetailerRemovalCancelled : MarketplaceRetailerAction
    data object RetailerRemovalClaimed : MarketplaceRetailerAction
    data class RetailerRemoved(val retailerId: Long) : MarketplaceRetailerAction
    data class RetailerRemovalFailed(val retailerId: Long, val message: String = messageText("nao_foi_possivel_concluir_esta_o_3c7db3")) :
        MarketplaceRetailerAction
    data class FavoriteRetailersLoading(val openAfterLoad: Boolean) : MarketplaceRetailerAction
    data class FavoriteRetailersReceived(
        val stale: Boolean,
        val result: AppResult<List<br.gov.caixa.loterias.feature.retail.domain.model.Retailer>>,
        val synchronizePools: Boolean,
        val openAfterLoad: Boolean,
        val message: String = messageText("nao_foi_possivel_carregar_as_lot_60820b"),
    ) : MarketplaceRetailerAction
    data class PoolFavoriteRequested(val poolCode: String) : MarketplacePoolAction
    data object PoolFavoriteCancelled : MarketplacePoolAction
    data object PoolFavoriteClaimed : MarketplacePoolAction
    data class PoolFavoriteSaved(val retailerId: Long, val favorite: Boolean) : MarketplacePoolAction
    data class PoolFavoriteFailed(val retailerId: Long, val message: String = messageText("nao_foi_possivel_concluir_esta_o_3c7db3")) :
        MarketplacePoolAction
    data class TabSelected(val tab: MarketplaceTab) : MarketplacePoolAction
    data class PoolExpanded(val poolCode: String) : MarketplacePoolAction
    data object ActionErrorDismissed : MarketplacePoolAction
}
