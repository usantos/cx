package br.gov.caixa.loterias.feature.cart.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAddedUseCase
import javax.inject.Inject

class CartNavigationDependenciesAdapter @Inject constructor(
    private val contents: CartContentDependencies,
    private val favorites: CartFavoriteDependencies,
    private val sessions: CartSessionDependencies,
    private val presentation: CartPresentationDependencies,
) : CartNavigationDependencies {
    override val currentCart: GetCurrentCartUseCase get() = contents.currentCart
    override val removeCartItem: RemoveCartItemUseCase get() = contents.removeCartItem
    override val clearCart: ClearCartUseCase get() = contents.clearCart
    override val removeExpiredPoolShare: RemoveExpiredPoolShareUseCase get() = contents.removeExpiredPoolShare
    override val bettingParameters: GetBettingParametersUseCase get() = contents.bettingParameters
    override val validateCart: ValidateCartUseCase get() = contents.validateCart
    override val verifyCartPaymentAccount: VerifyCartPaymentAccountUseCase get() = contents.verifyCartPaymentAccount
    override val hasRecentPurchase: HasRecentPurchaseUseCase get() = contents.hasRecentPurchase
    override val checkCheckoutLocation: CheckCheckoutLocationUseCase get() = contents.checkCheckoutLocation
    override val recordCheckoutLocationRequest: RecordCheckoutLocationRequestUseCase get() = contents.recordCheckoutLocationRequest
    override val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase get() = favorites.addFavoriteCartToCurrent
    override val deleteFavoriteCart: DeleteFavoriteCartUseCase get() = favorites.deleteFavoriteCart
    override val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase get() = favorites.deleteFavoriteCartBet
    override val favoriteCartBets: GetFavoriteCartBetsUseCase get() = favorites.favoriteCartBets
    override val favoriteCarts: GetFavoriteCartsUseCase get() = favorites.favoriteCarts
    override val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase get() = favorites.markFavoriteCartAdded
    override val saveCurrentCartFavorite: SaveCurrentCartFavoriteUseCase get() = favorites.saveCurrentCartFavorite
    override val validateFavoriteCartName: ValidateFavoriteCartNameUseCase get() = favorites.validateFavoriteCartName
    override val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase get() = favorites.wasFavoriteCartAdded
    override val authenticatedSessionContent: AuthenticatedSessionContentRepository get() = sessions.authenticatedSessionContent
    override val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider get() = sessions.authenticatedSessionIdentity
    override val currentUserIdentity: CurrentUserIdentityProvider get() = sessions.currentUserIdentity
    override val legacyCartTransfer: CartTransferRepository get() = sessions.legacyCartTransfer
    override val visitorCart: VisitorCartRepository get() = sessions.visitorCart
    private val sessionTokenStore: SessionTokenStore get() = sessions.sessionTokenStore
    override val appModulesConfiguration: AppModulesConfigurationUseCase get() = presentation.appModulesConfiguration
    override val dispatchers: CoroutineDispatchers get() = presentation.dispatchers
    override val localClock: java.time.Clock get() = presentation.localClock
    override val networkAvailability: NetworkAvailability get() = presentation.networkAvailability
    private val serverClock: ServerTimeSource get() = presentation.serverClock
    private val startupStatusPresentation: CartRecoveryStatusPresentation get() = presentation.startupStatusPresentation

    override val serverTime: ServerTimeSource get() = serverClock
    override val sessionIdentityChanges:
        kotlinx.coroutines.flow.Flow<AuthenticatedSessionIdentity?> get() = sessionTokenStore.observeIdentities()

    @Composable
    override fun SimulationRecoveryPanel(
        loading: Boolean,
        message: String?,
        onRetry: () -> Unit,
        onBack: () -> Unit,
        onDismissMessage: () -> Unit,
        networkAvailability: NetworkAvailability,
    ) {
        val uiStrings = androidx.compose.ui.platform.LocalResources.current
        BackHandler { if (!loading) onBack() }
        startupStatusPresentation.Content(loading, onRetry, networkAvailability)
        if (message != null) {
            LoteriasCaixaDSAlertDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = message,
                confirmText = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
                onConfirm = onDismissMessage
            )
        }
    }
}

class CartContentDependencies @Inject constructor(
    val currentCart: GetCurrentCartUseCase,
    val removeCartItem: RemoveCartItemUseCase,
    val clearCart: ClearCartUseCase,
    val removeExpiredPoolShare: RemoveExpiredPoolShareUseCase,
    val bettingParameters: GetBettingParametersUseCase,
    val validateCart: ValidateCartUseCase,
    val verifyCartPaymentAccount: VerifyCartPaymentAccountUseCase,
    val hasRecentPurchase: HasRecentPurchaseUseCase,
    val checkCheckoutLocation: CheckCheckoutLocationUseCase,
    val recordCheckoutLocationRequest: RecordCheckoutLocationRequestUseCase,
)

class CartFavoriteDependencies @Inject constructor(
    val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase,
    val deleteFavoriteCart: DeleteFavoriteCartUseCase,
    val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase,
    val favoriteCartBets: GetFavoriteCartBetsUseCase,
    val favoriteCarts: GetFavoriteCartsUseCase,
    val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase,
    val saveCurrentCartFavorite: SaveCurrentCartFavoriteUseCase,
    val validateFavoriteCartName: ValidateFavoriteCartNameUseCase,
    val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase,
)

class CartSessionDependencies @Inject constructor(
    val authenticatedSessionContent: AuthenticatedSessionContentRepository,
    val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider,
    val currentUserIdentity: CurrentUserIdentityProvider,
    val legacyCartTransfer: CartTransferRepository,
    val visitorCart: VisitorCartRepository,
    val sessionTokenStore: SessionTokenStore,
)

class CartPresentationDependencies @Inject constructor(
    val appModulesConfiguration: AppModulesConfigurationUseCase,
    val dispatchers: CoroutineDispatchers,
    val localClock: java.time.Clock,
    val networkAvailability: NetworkAvailability,
    val serverClock: ServerTimeSource,
    val startupStatusPresentation: CartRecoveryStatusPresentation,
)
