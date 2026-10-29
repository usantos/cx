package br.gov.caixa.loterias.feature.cart.navigation

import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAddedUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface CartNavigationDependencies {
    val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase
    val authenticatedSessionContent: br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
    val authenticatedSessionIdentity: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
    val bettingParameters: GetBettingParametersUseCase
    val checkCheckoutLocation: br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
    val clearCart: ClearCartUseCase
    val currentCart: GetCurrentCartUseCase
    val currentUserIdentity: CurrentUserIdentityProvider
    val deleteFavoriteCart: DeleteFavoriteCartUseCase
    val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase
    val dispatchers: CoroutineDispatchers
    val favoriteCartBets: GetFavoriteCartBetsUseCase
    val favoriteCarts: GetFavoriteCartsUseCase
    val hasRecentPurchase: br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase
    val legacyCartTransfer: br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
    val localClock: java.time.Clock
    val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase
    val networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability
    val recordCheckoutLocationRequest: br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
    val removeCartItem: RemoveCartItemUseCase
    val removeExpiredPoolShare: br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase
    val saveCurrentCartFavorite: SaveCurrentCartFavoriteUseCase
    val serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource
    val validateCart: ValidateCartUseCase
    val validateFavoriteCartName: ValidateFavoriteCartNameUseCase
    val verifyCartPaymentAccount: br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase
    val visitorCart: br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository
    val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase
    val sessionIdentityChanges: kotlinx.coroutines.flow.Flow<br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?>

    @androidx.compose.runtime.Composable
    fun SimulationRecoveryPanel(loading: Boolean, message: String?, onRetry: () -> Unit,
        onBack: () -> Unit, onDismissMessage: () -> Unit,
        networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability,
    )
}
