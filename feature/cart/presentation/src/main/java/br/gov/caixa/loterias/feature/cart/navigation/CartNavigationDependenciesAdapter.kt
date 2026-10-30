package br.gov.caixa.loterias.feature.cart.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
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
import javax.inject.Inject

class CartNavigationDependenciesAdapter @Inject constructor(
    override val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase,
    override val authenticatedSessionContent: br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository,
    override val authenticatedSessionIdentity: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider,
    override val bettingParameters: GetBettingParametersUseCase,
    override val checkCheckoutLocation: br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase,
    override val clearCart: ClearCartUseCase,
    override val currentCart: GetCurrentCartUseCase,
    override val currentUserIdentity: CurrentUserIdentityProvider,
    override val deleteFavoriteCart: DeleteFavoriteCartUseCase,
    override val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase,
    override val dispatchers: CoroutineDispatchers,
    override val favoriteCartBets: GetFavoriteCartBetsUseCase,
    override val favoriteCarts: GetFavoriteCartsUseCase,
    override val hasRecentPurchase: br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase,
    override val legacyCartTransfer: br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository,
    override val localClock: java.time.Clock,
    override val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase,
    override val networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability,
    override val recordCheckoutLocationRequest: br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase,
    override val removeCartItem: RemoveCartItemUseCase,
    override val removeExpiredPoolShare: br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase,
    override val saveCurrentCartFavorite: SaveCurrentCartFavoriteUseCase,
    private val serverClock: br.gov.caixa.loterias.core.common.time.ServerTimeSource,
    override val validateCart: ValidateCartUseCase,
    override val validateFavoriteCartName: ValidateFavoriteCartNameUseCase,
    override val verifyCartPaymentAccount: br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase,
    override val visitorCart: br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository,
    override val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase,
    private val sessionTokenStore: br.gov.caixa.loterias.core.auth.session.SessionTokenStore,
    private val startupStatusPresentation: CartRecoveryStatusPresentation,
) : CartNavigationDependencies {
    override val serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource get() = serverClock
    override val sessionIdentityChanges:
        kotlinx.coroutines.flow.Flow<AuthenticatedSessionIdentity?> get() = sessionTokenStore.observeIdentities()

    @Composable
    override fun SimulationRecoveryPanel(
        loading: Boolean,
        message: String?,
        onRetry: () -> Unit,
        onBack: () -> Unit,
        onDismissMessage: () -> Unit,
        networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability,
    ) {
        val uiStrings = androidx.compose.ui.platform.LocalResources.current
        BackHandler { if (!loading) onBack() }
        startupStatusPresentation.Content(loading, onRetry, networkAvailability)
        if (message != null) {
            CaixaDSAlertDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = message,
                confirmText = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
                onConfirm = onDismissMessage
            )
        }
    }
}
