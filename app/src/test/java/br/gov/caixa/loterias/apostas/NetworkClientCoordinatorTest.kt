package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment
import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSource
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.keymanagement.model.ResolvedPublicKeySet
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider
import br.gov.caixa.loterias.core.network.api.SessionLogoutApiProvider
import br.gov.caixa.loterias.core.network.config.BackendClientFactory
import br.gov.caixa.loterias.core.network.config.NetworkApiProviders
import br.gov.caixa.loterias.core.network.config.NetworkClientCoordinator
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider
import br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider
import br.gov.caixa.loterias.core.network.routing.CloudBackendRouting
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NetworkClientCoordinatorTest {
    @Test fun `initialization installs every API on the existing stable providers`() {
        val fixture = Fixture()
        fixture.coordinator.initialize()
        assertEquals(INSTALLED_CLIENT_COUNT, fixture.clients().size)
        val history = fixture.history.current("https://history.example/api/")
        assertNotNull(history)
        assertSame(history, fixture.history.current("https://history.example/api"))
        assertNull(fixture.history.current("https://unverified.example/api/"))
        assertNull(fixture.history.current("http://history.example/api/"))
    }

    @Test fun `verified rotation replaces all APIs and invalidates dynamic clients and old hosts`() = runTest {
        val fixture = Fixture(resolve = { resolved("renewed.example") })
        fixture.coordinator.initialize()
        val previous = fixture.clients()
        val oldHistory = fixture.history.current("https://history.example/api/")
        assertEquals(AppResult.Success(Unit), fixture.coordinator.networkSecurity())
        previous.zip(fixture.clients()).forEach { (before, after) -> assertNotSame(before, after) }
        assertNull(fixture.history.current("https://history.example/api/"))
        assertNotNull(fixture.history.current("https://renewed.example/api/"))
        assertNotSame(oldHistory, fixture.history.current("https://renewed.example/api/"))
        assertEquals(listOf(KeySetSource.REMOTE), fixture.loggedSources)
    }

    @Test fun `rotation clears cached clients even when the verified hosts are unchanged`() = runTest {
        val fixture = Fixture(resolve = { resolved("history.example") })
        fixture.coordinator.initialize()
        val previous = fixture.history.current("https://history.example/api/")
        assertEquals(AppResult.Success(Unit), fixture.coordinator.networkSecurity())
        assertNotSame(previous, fixture.history.current("https://history.example/api/"))
    }

    @Test fun `parameter updates reach the clients shared cloud router before and after rotation`() = runTest {
        val requests = mutableListOf<okhttp3.Request>()
        val fixture = Fixture(
            debugOptions = NetworkDebugOptions(
                enabled = true,
                inspector = okhttp3.Interceptor { chain ->
                    requests += chain.request()
                    Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                        .code(200).message("OK").body("{}".toResponseBody()).build()
                }
            )
        )
        fixture.coordinator.initialize()
        fixture.api.updateParameters(
            AppParametersDto(
                productionCartBaseUrl = "https://cloud.example/cart/",
                cloudSubscriptionKey = "first-key"
            )
        )
        fixture.api.current().lotteryModalities()
        assertEquals("https://cloud.example/cart/publico/modalidades", requests.last().url.toString())
        assertEquals("first-key", requests.last().header("ocp-apim-subscription-key"))
        assertEquals(AppResult.Success(Unit), fixture.coordinator.networkSecurity())
        fixture.api.current().lotteryModalities()
        assertEquals("first-key", requests.last().header("ocp-apim-subscription-key"))
        fixture.api.updateParameters(
            AppParametersDto(
                productionCartBaseUrl = "https://next-cloud.example/v2/",
                cloudSubscriptionKey = "next-key"
            )
        )
        fixture.api.current().lotteryModalities()
        assertEquals("https://next-cloud.example/v2/publico/modalidades", requests.last().url.toString())
        assertEquals("next-key", requests.last().header("ocp-apim-subscription-key"))
    }

    @Test fun `payment construction failure preserves earlier installations in the original order`() {
        val fixture = Fixture(mercadoPagoBaseUrl = "http://invalid.example/")
        assertThrows(IllegalArgumentException::class.java) { fixture.coordinator.initialize() }
        assertEquals(INSTALLED_BACKEND_CLIENT_COUNT, fixture.backendClients().size)
        assertNotNull(fixture.history.current("https://history.example/api/"))
        assertThrows(IllegalStateException::class.java) { fixture.clients() }
    }

    @Test fun `mock refresh reinstalls fallback without resolving or logging remote keys`() = runTest {
        val fixture = Fixture(mock = true, resolve = { error("Mock must not fetch keys") })
        fixture.coordinator.initialize()
        val previous = fixture.clients()
        assertEquals(AppResult.Success(Unit), fixture.coordinator.networkSecurity())
        previous.zip(fixture.clients()).forEach { (before, after) -> assertNotSame(before, after) }
        assertNotNull(fixture.history.current("https://history.example/api/"))
        assertTrue(fixture.loggedSources.isEmpty())
    }

    @Test fun `missing key repository refreshes with fallback`() = runTest {
        val fixture = Fixture(resolve = { null })
        fixture.coordinator.initialize()
        val previous = fixture.api.current()
        assertEquals(AppResult.Success(Unit), fixture.coordinator.networkSecurity())
        assertNotSame(previous, fixture.api.current())
        assertNotNull(fixture.history.current("https://history.example/api/"))
        assertEquals(listOf<KeySetSource?>(null), fixture.loggedSources)
    }

    @Test fun `resolution failure retains every current client and permits retry`() = runTest {
        var failed = true
        val fixture = Fixture(resolve = { if (failed) throw IOException("offline") else resolved("renewed.example") })
        fixture.coordinator.initialize()
        val previous = fixture.clients()
        val history = fixture.history.current("https://history.example/api/")
        assertEquals(AppResult.Failure(AppError.Network), fixture.coordinator.networkSecurity())
        previous.zip(fixture.clients()).forEach { (before, after) -> assertSame(before, after) }
        assertSame(history, fixture.history.current("https://history.example/api/"))
        assertTrue(fixture.loggedSources.isEmpty())
        failed = false
        assertEquals(AppResult.Success(Unit), fixture.coordinator.networkSecurity())
        assertNull(fixture.history.current("https://history.example/api/"))
    }

    @Test fun `cancelled resolution propagates without replacing clients and releases the lock`() = runTest {
        var cancelled = true
        val fixture = Fixture(resolve = { if (cancelled) throw CancellationException("cancelled") else resolved("renewed.example") })
        fixture.coordinator.initialize()
        val previous = fixture.clients()
        try {
            fixture.coordinator.networkSecurity()
            fail("Expected cancellation")
        } catch (_: CancellationException) { }
        previous.zip(fixture.clients()).forEach { (before, after) -> assertSame(before, after) }
        cancelled = false
        assertEquals(AppResult.Success(Unit), fixture.coordinator.networkSecurity())
    }

    @Test fun `overlapping refreshes resolve and install in sequence`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val events = mutableListOf<String>()
        var calls = 0
        val fixture = Fixture(resolve = {
            val call = ++calls
            events += "resolve-$call"
            if (call == 1) gate.await()
            resolved("generation-$call.example")
        }, onResolved = { events += "installed-$calls" })
        fixture.coordinator.initialize()
        val first = async { fixture.coordinator.networkSecurity() }
        runCurrent()
        val second = async { fixture.coordinator.networkSecurity() }
        runCurrent()
        assertEquals(listOf("resolve-1"), events)
        gate.complete(Unit)
        assertEquals(AppResult.Success(Unit), first.await())
        assertEquals(AppResult.Success(Unit), second.await())
        assertEquals(listOf("resolve-1", "installed-1", "resolve-2", "installed-2"), events)
        assertNull(fixture.history.current("https://generation-1.example/api/"))
        assertNotNull(fixture.history.current("https://generation-2.example/api/"))
    }

    private class Fixture(
        mock: Boolean = false,
        resolve: suspend () -> ResolvedPublicKeySet? = { resolved("history.example") },
        onResolved: () -> Unit = {},
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mercadoPagoBaseUrl: String = "https://payment.example/",
    ) {
        val api = LoteriasApiProvider()
        private val legacy = BackendApiProvider()
        val history = BetsHistoryApiProvider()
        private val payment = PaymentApiProvider()
        private val purchase = PurchaseBackendApiProvider()
        private val prize = PrizeBackendApiProvider()
        private val account = AccountVerificationBackendProvider()
        private val notifications = NotificationReadApiProvider()
        private val inbox = NotificationInboxApiProvider()
        private val logout = SessionLogoutApiProvider()
        val loggedSources = mutableListOf<KeySetSource?>()
        private val environment = EffectiveEnvironment(
            apiBaseUrl = "https://backend.example/rest/", ssoAuthUrl = "https://sso.example/auth",
            ssoTokenUrl = "https://sso.example/token", ssoLogoutUrl = "https://sso.example/logout",
            ssoAccountUrl = "https://sso.example/account", ssoClientId = "client", ssoRedirectUri = "app://return",
            mercadoPagoBaseUrl = mercadoPagoBaseUrl, recargaPayBaseUrl = "https://recharge.example/",
            queueAccount = "account", queueSystem = "system"
        )
        private val gson = Gson()
        private val factory = BackendClientFactory(
            environment = environment,
            tokenProvider = SessionTokenStore(),
            gson = { gson },
            cloudRouting = CloudBackendRouting(environment.apiBaseUrl.toHttpUrl(), production = true),
            debugOptions = debugOptions,
            mockOptions = MockBackendOptions(enabled = mock),
            sessionRefreshHandler = null
        )
        val coordinator = NetworkClientCoordinator(
            factory = factory,
            providers = NetworkApiProviders(
                apiProvider = api, legacyApiProvider = legacy, betsHistoryApiProvider = history,
                paymentApiProvider = payment, purchaseApiProvider = purchase, prizeApiProvider = prize,
                accountVerificationProvider = account, notificationReadProvider = notifications,
                notificationInboxProvider = inbox, sessionLogoutProvider = logout
            ),
            localFallback = keySet("history.example"),
            mockBackend = mock,
            resolveKeySet = resolve,
            onKeySetResolved = {
                loggedSources += it?.source
                onResolved()
            }
        )

        fun backendClients(): List<Any> = listOf(
            api.current(),
            notifications.current(),
            inbox.current(),
            logout.current(),
            account.current(),
            prize.current(),
            purchase.current(),
            legacy.current(),
            history.current()
        )

        fun clients(): List<Any> = backendClients() + listOf(payment.current().mercadoPago, payment.current().recargaPay)
    }

    companion object {
        private const val INSTALLED_CLIENT_COUNT = 11
        private const val INSTALLED_BACKEND_CLIENT_COUNT = 9
        private fun keySet(historyHost: String) = PublicKeySet(
            version = 1,
            issuedAtEpochSeconds = 0,
            expiresAtEpochSeconds = Long.MAX_VALUE,
            hosts = listOf("backend.example", "payment.example", "recharge.example", historyHost).map { host ->
                HostPublicKeyPins(host, listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="))
            }
        )

        private fun resolved(historyHost: String) = ResolvedPublicKeySet(keySet(historyHost), KeySetSource.REMOTE)
    }
}
