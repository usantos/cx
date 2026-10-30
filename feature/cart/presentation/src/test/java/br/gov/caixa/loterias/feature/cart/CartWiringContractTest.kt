// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.auth.fixture.SyntheticSessionFixture
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.BoundCartRemovalRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.di.CartModule
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationAccess
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartUsageRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.RecentPurchaseRepository
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase
import br.gov.caixa.loterias.feature.cart.navigation.CartNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.cart.navigation.CartRecoveryStatusPresentation
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** Contratos tipados de composição; repositórios registram apenas operações locais sintéticas. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CartWiringContractTest {
    @Test fun `cart adapter retains dependencies and provided use cases enforce validation before forwarding`() = runBlocking {
        val fixture = CartWiringFixture()
        val adapter = fixture.adapter()
        assertEquals(AppResult.Success(fixture.cart), adapter.currentCart())
        assertTrue(adapter.removeCartItem(fixture.item.copy(id = 0)) is AppResult.Failure)
        assertEquals(AppResult.Success(fixture.cart), adapter.removeCartItem(fixture.item))
        assertEquals(AppResult.Success(CartValidationResult.Accepted), adapter.validateCart())
        assertTrue(adapter.validateFavoriteCartName(" ") is AppResult.Failure)
        assertEquals(AppResult.Success(Unit), adapter.validateFavoriteCartName("  Synthetic  "))
        assertEquals(AppResult.Success(Unit), adapter.saveCurrentCartFavorite("  Synthetic  "))
        adapter.markFavoriteCartAdded(4)
        assertTrue(adapter.wasFavoriteCartAdded(4))
        assertFalse(adapter.wasFavoriteCartAdded(0))
        assertEquals(AppResult.Success(listOf(fixture.favorite)), adapter.favoriteCarts())
        assertEquals(AppResult.Success(listOf(fixture.bet)), adapter.favoriteCartBets(4))
        assertEquals(AppResult.Success(emptyList<FavoriteCartBet>()), adapter.deleteFavoriteCartBet(4, 8))
        assertEquals(AppResult.Success(emptyList<FavoriteCart>()), adapter.deleteFavoriteCart(4))
        assertTrue(adapter.addFavoriteCartToCurrent(4, 3) is AppResult.Failure)
        assertEquals(AppResult.Success(fixture.cart), adapter.addFavoriteCartToCurrent(4, 2))
        assertEquals(AppResult.Success(fixture.cart), adapter.clearCart())
        assertFalse(adapter.wasFavoriteCartAdded(4))
        assertEquals(
            listOf(
                "current",
                "remove:7",
                "validate",
                "name:Synthetic",
                "save:Synthetic",
                "favorites",
                "bets:4",
                "deleteBet:4:8",
                "delete:4",
                "add:4:2",
                "clear",
                "usage.clear"
            ),
            fixture.calls
        )
        assertSame(fixture.dispatchers, adapter.dispatchers)
        assertSame(fixture.network, adapter.networkAvailability)
        assertSame(fixture.store, adapter.authenticatedSessionIdentity)
        assertSame(fixture.store, adapter.authenticatedSessionContent)
        assertNull(adapter.sessionIdentityChanges.first())
        assertNull(adapter.authenticatedSessionIdentity.captureIdentity())
        assertNull(adapter.authenticatedSessionContent.observe().first())
        assertEquals(fixture.now, adapter.serverTime.now())
        assertEquals(fixture.now, adapter.localClock.instant())
        assertEquals("synthetic-cpf", adapter.currentUserIdentity.currentUser()!!.cpf)
        assertEquals(AppResult.Success(CartPaymentAccountDecision.Allowed), adapter.verifyCartPaymentAccount())
        assertEquals(AppResult.Success(false), adapter.hasRecentPurchase(BigDecimal.TEN))
        assertEquals(AppResult.Success(CheckoutLocationStatus.IN_BRAZIL), adapter.checkCheckoutLocation())
        assertEquals(AppResult.Success(Unit), adapter.recordCheckoutLocationRequest())
        assertEquals(1, fixture.permissionRequests)
        assertTrue(adapter.bettingParameters() is AppResult.Success)
        assertEquals(0, adapter.visitorCart.current().betCount)
        assertSecureUnavailable(runCatching { adapter.legacyCartTransfer.prepare("a".repeat(64)) }.exceptionOrNull())
        assertSame(fixture.expired, adapter.removeExpiredPoolShare)
    }

    @Test fun `payment verification binding checks current identity before calling account service`() = runBlocking {
        var user: AuthenticatedUser? = null
        var checks = 0
        val verify = CartModule.verifyCartPaymentAccount(
            CurrentUserIdentityProvider { user },
            VerifyAccountAccessUseCase {
                checks++
                AppResult.Success(AccountAccess(PostLoginDestination.REGISTRATION, true, BigDecimal.TEN))
            }
        )
        assertEquals(AppResult.Success(CartPaymentAccountDecision.LoginRequired), verify())
        assertEquals(0, checks)
        user = AuthenticatedUser("Synthetic User", "synthetic-cpf", "fixture@example.test", "")
        assertEquals(AppResult.Success(CartPaymentAccountDecision.CompleteAccount(PostLoginDestination.REGISTRATION)), verify())
        assertEquals(1, checks)
    }

    @Test fun `provided encrypted stores preserve prior state when JVM Android Keystore is unavailable`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val session = SessionTokenStore().also { check(SyntheticSessionFixture.install(it)) }
        val usage = CartModule.favoriteCartUsageRepository(context, cartTestConfiguration(), session)
        assertFalse(CartModule.wasFavoriteCartAdded(usage)(42))
        assertSecureUnavailable(runCatching { CartModule.markFavoriteCartAdded(usage)(42) }.exceptionOrNull())
        assertFalse(CartModule.wasFavoriteCartAdded(usage)(42))
        val purchases = CartModule.recentPurchaseRepository(context, cartTestConfiguration(), session)
        assertEquals(AppResult.Success(false), CartModule.hasRecentPurchase(purchases)(BigDecimal("12.50")))
        assertTrue(CartModule.recordRecentPurchase(purchases)(BigDecimal("12.50")) is AppResult.Failure)
        assertEquals(AppResult.Success(false), CartModule.hasRecentPurchase(purchases)(BigDecimal("12.50")))
        // O histórico persistente e o comportamento real do AndroidKeyStore são exercitados pelas massas Android
        // controladas de cart/data; esta fronteira JVM nunca deve recorrer a texto sem criptografia.
    }

    @Test fun `visitor checkout can check recent purchases before login`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val purchases = CartModule.recentPurchaseRepository(context, cartTestConfiguration(), SessionTokenStore())
        assertEquals(AppResult.Success(false), CartModule.hasRecentPurchase(purchases)(BigDecimal("19.00")))
    }

    @Test fun `guest repository binding stays local even with an unconfigured remote transport`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val source = CartRemoteDataSourceImpl(
            LoteriasApiProvider()
        )
        assertSame(source, CartModule.cartDataSource(source))
        assertSame(source, CartModule.favoriteCartDataSource(source))
        val visitor = CartModule.visitorCart(context, cartTestConfiguration())
        val repository = CartModule.cartRepository(source, visitor, SessionTokenStore())
        assertEquals(visitor.current(), repository.current())
        assertEquals(0, repository.current().betCount)
        assertSecureUnavailable(runCatching { repository.clear() }.exceptionOrNull())
        assertEquals(0, repository.current().betCount)
        // Qualquer chamada acidental ao backend falharia imediatamente: o provedor não tem API configurada.
    }

    @Test fun `expired reservation factory preserves captured session binding and uses reservation id`() = runBlocking {
        val identity = AuthenticatedSessionIdentity("c".repeat(64))
        val session = AccountVerificationSession({ "synthetic-token" }, { true })
        var bound: AuthenticatedSessionIdentity? = null
        val removed = mutableListOf<Long>()
        val source = BoundCartRemovalRemoteDataSource { id, actualSession ->
            assertSame(session, actualSession)
            removed += id
            CartDto(id = 31, total = BigDecimal.ZERO)
        }
        val repository = CartModule.expiredPoolShareRepository(
            source,
            AccountVerificationSessionBinding {
                bound =
                    it
                session
            }
        )
        val remove = CartModule.removeExpiredPoolShare(repository)
        val item = CartItem(
            7,
            CartItemKind.POOL_SHARE,
            "Synthetic reservation",
            "",
            BigDecimal.TEN,
            poolShare = CartPoolShare("synthetic-pool", 1, 10, "2026-10-22T09:00:00Z")
        )
        val result = remove(item, identity) as AppResult.Success<ShoppingCart>
        assertEquals(31L, result.value.id)
        assertEquals(listOf(7L), removed)
        assertSame(identity, bound)
        assertTrue(remove(item.copy(kind = CartItemKind.INDIVIDUAL_BET), identity) is AppResult.Failure)
        assertEquals(listOf(7L), removed)
    }
}

internal class CartWiringFixture {
    val calls = mutableListOf<String>()
    val item = CartItem(7, CartItemKind.INDIVIDUAL_BET, "Synthetic", "01 02", BigDecimal.TEN)
    val cart = ShoppingCart(3, BigDecimal.TEN, listOf(item))
    val favorite = FavoriteCart(4, "Synthetic")
    val bet = FavoriteCartBet(8, "Mega-Sena", listOf(1, 2, 3, 4, 5, 6))
    val store = SessionTokenStore()
    val now = Instant.parse("2026-10-22T10:00:00Z")
    val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
    val network = NetworkAvailability { ConnectionAvailability.CONNECTED }
    var permissionRequests = 0
    val expired = RemoveExpiredPoolShareUseCase { _, _ -> error("Automatic reservation removal requires an explicit reservation") }
    private val marked = mutableSetOf<Long>()
    private val usage = object : FavoriteCartUsageRepository {
        override suspend fun wasAddedToCurrentCart(cartId: Long) = cartId in marked
        override suspend fun markAsAddedToCurrentCart(cartId: Long) {
            marked += cartId
        }
        override suspend fun clear() {
            calls += "usage.clear"
            marked.clear()
        }
    }
    private val repository = object : CartRepository {
        override suspend fun current(): ShoppingCart {
            calls += "current"
            return cart
        }
        override suspend fun remove(item: CartItem): ShoppingCart {
            calls += "remove:${item.id}"
            return cart
        }
        override suspend fun clear(): ShoppingCart {
            calls += "clear"
            return cart
        }
        override suspend fun validate(): CartValidationResult {
            calls += "validate"
            return CartValidationResult.Accepted
        }
        override suspend fun validateFavoriteName(name: String) {
            calls += "name:$name"
        }
        override suspend fun saveFavorite(name: String) {
            calls += "save:$name"
        }
    }
    private val favorites = object : FavoriteCartRepository {
        override suspend fun favorites(): List<FavoriteCart> {
            calls += "favorites"
            return listOf(favorite)
        }
        override suspend fun bets(cartId: Long): List<FavoriteCartBet> {
            calls += "bets:$cartId"
            return listOf(bet)
        }
        override suspend fun deleteCart(cartId: Long): List<FavoriteCart> {
            calls += "delete:$cartId"
            return emptyList()
        }
        override suspend fun deleteBet(cartId: Long, betId: Long): List<FavoriteCartBet> {
            calls += "deleteBet:$cartId:$betId"
            return emptyList()
        }
        override suspend fun addToCurrentCart(cartId: Long, specialSelection: Int): ShoppingCart {
            calls += "add:$cartId:$specialSelection"
            return cart
        }
    }
    fun adapter(
        presentation: CartRecoveryStatusPresentation = object : CartRecoveryStatusPresentation {
            @Composable override fun Content(loading: Boolean, onRetry: () -> Unit, networkAvailability: NetworkAvailability) = Unit
        },
    ): CartNavigationDependenciesAdapter {
        val context = RuntimeEnvironment.getApplication()
        val location = object : CheckoutLocationSource {
            override suspend fun access() = CheckoutLocationAccess(true, true, true, permissionRequests)
            override suspend fun countryCode() = "BR"
            override suspend fun recordPermissionRequest() {
                permissionRequests++
            }
        }
        val user = CurrentUserIdentityProvider { AuthenticatedUser("Synthetic User", "synthetic-cpf", "fixture@example.test", "") }
        return CartNavigationDependenciesAdapter(
            addFavoriteCartToCurrent = CartModule.addFavoriteCartToCurrent(
                favorites
            ),
            authenticatedSessionContent = store, authenticatedSessionIdentity = store,
            bettingParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 100, emptyList()))
            },
            checkCheckoutLocation = CartModule.checkCheckoutLocation(
                location
            ),
            clearCart = CartModule.clearCart(
                repository,
                usage
            ),
            currentCart = CartModule.currentCart(repository), currentUserIdentity = user,
            deleteFavoriteCart = CartModule.deleteFavoriteCart(
                favorites
            ),
            deleteFavoriteCartBet = CartModule.deleteFavoriteCartBet(favorites), dispatchers = dispatchers,
            favoriteCartBets = CartModule.favoriteCartBets(favorites), favoriteCarts = CartModule.favoriteCarts(favorites),
            hasRecentPurchase = CartModule.hasRecentPurchase(object : RecentPurchaseRepository {
                override suspend fun latestPurchaseAt(amount: BigDecimal): Instant? = null
                override suspend fun record(amount: BigDecimal, purchasedAt: Instant) = error("No history write requested")
            }),
            legacyCartTransfer = CartModule.legacyCartTransfer(
                context,
                cartTestConfiguration()
            ),
            localClock = Clock.fixed(now, ZoneOffset.UTC), markFavoriteCartAdded = CartModule.markFavoriteCartAdded(usage),
            networkAvailability = network,
            recordCheckoutLocationRequest = CartModule.recordCheckoutLocationRequest(
                location
            ),
            removeCartItem = CartModule.removeCartItem(repository), removeExpiredPoolShare = expired,
            saveCurrentCartFavorite = CartModule.saveCurrentCartFavorite(repository),
            serverClock = ServerTimeSource {
                now
            },
            validateCart = CartModule.validateCart(
                repository
            ),
            validateFavoriteCartName = CartModule.validateFavoriteCartName(repository),
            verifyCartPaymentAccount = CartModule.verifyCartPaymentAccount(
                user,
                VerifyAccountAccessUseCase {
                    AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN))
                }
            ),
            visitorCart = CartModule.visitorCart(
                context,
                cartTestConfiguration()
            ),
            wasFavoriteCartAdded = CartModule.wasFavoriteCartAdded(
                usage
            ),
            sessionTokenStore = store, startupStatusPresentation = presentation
        )
    }
}

private fun cartTestConfiguration() = HostFeatureConfiguration(
    appVersionName = "fixture", accountReferrerUri = "android-app://synthetic", ssoAccountUrl = "https://identity.invalid/account",
    ssoClientId = "synthetic-client", environmentSelectorVisible = false, homeNavigationEnvironmentLabel = null,
    mockBackendEnabled = true,
    firebaseAppId = "synthetic",
    firebaseApiKey = "synthetic",
    firebaseProjectId = "synthetic",
    firebaseSenderId = "synthetic",
    backendApiBaseUrl = "https://backend.invalid/", backendSessionNamespace = "https://identity.invalid/realm",
    ssoAuthUrl = "https://identity.invalid/auth",
    ssoTokenUrl = "https://identity.invalid/token",
    ssoLogoutUrl = "https://identity.invalid/logout",
    ssoRedirectUri = "synthetic://login", productionBuild = false
)

private fun assertSecureUnavailable(failure: Throwable?) {
    assertTrue(
        "Missing Android Keystore must fail explicitly",
        generateSequence(failure) { it.cause }
            .any { it is SecureStorageException.KeyUnavailable }
    )
}
