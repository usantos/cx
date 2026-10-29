package br.gov.caixa.loterias.feature.cart.di

import android.content.Context
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.FavoriteCartRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.repository.CartRepositoryImpl
import br.gov.caixa.loterias.feature.cart.data.repository.FavoriteCartRepositoryImpl
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrent
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartUsageRepository
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBets
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCarts
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAdded
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItem
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavorite
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartName
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAdded
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.data.repository.FavoriteCartUsageRepositoryImpl
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CartModule {
    private fun guestScope(configuration: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration) =
        br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope(
            br.gov.caixa.loterias.core.domain.persistence.secureEnvironmentKey(
                configuration.backendSessionNamespace,
                configuration.backendApiBaseUrl
            ),
            "guest",
            "cart-v1"
        )

    private fun accountScope(
        configuration: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration,
        session: SessionTokenStore,
    ): br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope {
        val identity = checkNotNull(session.captureIdentity()) { "Authenticated persistence requires a current session" }
        check(session.isCurrent(identity)) { "Authenticated persistence lease expired" }
        return br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope(
            br.gov.caixa.loterias.core.domain.persistence.secureEnvironmentKey(
                configuration.backendSessionNamespace,
                configuration.backendApiBaseUrl
            ),
            identity.ownerKey,
            "account-cart-v1"
        )
    }

    @Provides @Singleton
    fun checkoutLocationSource(
        @ApplicationContext context: Context,
        dispatchers: CoroutineDispatchers,
    ): br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource =
        br.gov.caixa.loterias.feature.cart.data.location.AndroidCheckoutLocationSource(context, dispatchers)

    @Provides
    fun verifyCartPaymentAccount(
        identity: CurrentUserIdentityProvider,
        verifyAccess: VerifyAccountAccessUseCase,
    ): br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase =
        br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccount(
            isAuthenticated = { identity.currentUser() != null },
            verifyAccess = verifyAccess
        )

    @Provides @Singleton
    fun favoriteCartUsageRepository(
        @ApplicationContext context: Context,
        configuration: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration,
        session: SessionTokenStore,
    ): FavoriteCartUsageRepository =
        FavoriteCartUsageRepositoryImpl.fromContext(context, captureLease = session::captureLocalPersistenceLease) {
            if (session.captureIdentity() == null &&
                session.token() == null
            ) {
                guestScope(configuration)
            } else {
                accountScope(configuration, session)
            }
        }

    @Provides
    fun cartDataSource(source: br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl): CartRemoteDataSource =
        source

    @Provides
    fun expiredPoolShareRepository(
        source: br.gov.caixa.loterias.feature.cart.data.datasource.remote.BoundCartRemovalRemoteDataSource,
        binding: br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding,
    ): br.gov.caixa.loterias.feature.cart.domain.repository.ExpiredPoolShareRepository =
        br.gov.caixa.loterias.feature.cart.data.repository.ExpiredPoolShareRepositoryImpl(source, binding::bind)

    @Provides
    fun removeExpiredPoolShare(
        repository: br.gov.caixa.loterias.feature.cart.domain.repository.ExpiredPoolShareRepository,
    ): br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase =
        br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShare(repository)

    @Provides
    fun favoriteCartDataSource(
        source: br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl,
    ): FavoriteCartRemoteDataSource = source

    @Provides @Singleton
    fun cartRepository(
        source: CartRemoteDataSource,
        local: br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository,
        tokenStore: SessionTokenStore,
    ): CartRepository = br.gov.caixa.loterias.feature.cart.data.repository.SessionCartRepositoryImpl(CartRepositoryImpl(source), local) {
        tokenStore.token() !=
            null
    }

    @Provides @Singleton
    fun favoriteCartRepository(source: FavoriteCartRemoteDataSource): FavoriteCartRepository = FavoriteCartRepositoryImpl(source)

    @Provides
    fun currentCart(repository: CartRepository): GetCurrentCartUseCase = GetCurrentCart(repository)

    @Provides
    fun removeCartItem(repository: CartRepository): RemoveCartItemUseCase = RemoveCartItem(repository)

    @Provides
    fun clearCart(repository: CartRepository, favoriteUsage: FavoriteCartUsageRepository): ClearCartUseCase =
        ClearCart(repository, favoriteUsage)

    @Provides
    fun validateCart(repository: CartRepository): ValidateCartUseCase = ValidateCart(repository)

    @Provides
    fun validateFavoriteCartName(repository: CartRepository): ValidateFavoriteCartNameUseCase = ValidateFavoriteCartName(repository)

    @Provides
    fun saveCurrentCartFavorite(repository: CartRepository): SaveCurrentCartFavoriteUseCase = SaveCurrentCartFavorite(repository)

    @Provides
    fun favoriteCarts(repository: FavoriteCartRepository): GetFavoriteCartsUseCase = GetFavoriteCarts(repository)

    @Provides
    fun favoriteCartBets(repository: FavoriteCartRepository): GetFavoriteCartBetsUseCase = GetFavoriteCartBets(repository)

    @Provides
    fun deleteFavoriteCart(repository: FavoriteCartRepository): DeleteFavoriteCartUseCase = DeleteFavoriteCart(repository)

    @Provides
    fun deleteFavoriteCartBet(repository: FavoriteCartRepository): DeleteFavoriteCartBetUseCase = DeleteFavoriteCartBet(repository)

    @Provides
    fun addFavoriteCartToCurrent(repository: FavoriteCartRepository): AddFavoriteCartToCurrentUseCase = AddFavoriteCartToCurrent(repository)

    @Provides
    fun wasFavoriteCartAdded(repository: FavoriteCartUsageRepository): WasFavoriteCartAddedUseCase = WasFavoriteCartAdded(repository)

    @Provides
    fun markFavoriteCartAdded(repository: FavoriteCartUsageRepository): MarkFavoriteCartAddedUseCase = MarkFavoriteCartAdded(repository)

    @Provides @Singleton
    fun recentPurchaseRepository(
        @ApplicationContext context: Context,
        configuration: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration,
        session: SessionTokenStore,
    ): br.gov.caixa.loterias.feature.cart.domain.repository.RecentPurchaseRepository =
        br.gov.caixa.loterias.feature.cart.data.repository.RecentPurchaseRepositoryImpl.fromContext(
            context,
            captureLease = session::captureLocalPersistenceLease
        ) {
            if (session.captureIdentity() == null && session.token() == null) {
                guestScope(configuration)
            } else {
                accountScope(configuration, session)
            }
        }

    @Provides
    fun checkCheckoutLocation(
        source: br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource,
    ): br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase =
        br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocation(source)

    @Provides
    fun recordCheckoutLocationRequest(
        source: br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource,
    ): br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase =
        br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequest(source)

    @Provides
    fun hasRecentPurchase(
        repository: br.gov.caixa.loterias.feature.cart.domain.repository.RecentPurchaseRepository,
    ): br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase =
        br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchase(repository, java.time.Clock.systemDefaultZone())

    @Provides
    fun recordRecentPurchase(
        repository: br.gov.caixa.loterias.feature.cart.domain.repository.RecentPurchaseRepository,
    ): br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase =
        br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchase(repository, java.time.Clock.systemDefaultZone())

    @Provides @Singleton
    fun visitorCart(
        @ApplicationContext context: Context,
        configuration: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration,
    ): br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository =
        br.gov.caixa.loterias.feature.cart.data.repository.VisitorCartRepositoryImpl(context, guestScope(configuration))

    @Provides @Singleton
    fun legacyCartTransfer(
        @ApplicationContext context: Context,
        configuration: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration,
    ): br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository =
        br.gov.caixa.loterias.feature.cart.data.repository.CartTransferRepositoryImpl.fromContext(context, guestScope(configuration))
}
