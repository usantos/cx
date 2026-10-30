// Literais numéricos descrevem geometria fixa de teste, IDs dos dados de teste, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.apostas

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.apostas.di.AppInitializationBindings
import br.gov.caixa.loterias.apostas.di.AppNetworkBindings
import br.gov.caixa.loterias.apostas.di.HostFeatureConfigurationModule
import br.gov.caixa.loterias.apostas.environment.runtime.ProcessEnvironmentSettings
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshExecutor
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider
import br.gov.caixa.loterias.core.network.api.SessionLogoutApiProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider
import br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider
import br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import java.time.Duration
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowNetworkCapabilities
import org.robolectric.shadows.ShadowNetworkInfo

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HostCompositionBindingsJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test
    fun hostConfigurationUsesOneProcessEnvironmentForAuthenticationAndBackendRouting() {
        val settings = ProcessEnvironmentSettings(compose.activity)
        val configuration = HostFeatureConfigurationModule.hostFeatureConfiguration(settings)
        assertEquals(BuildConfig.VERSION_NAME, configuration.appVersionName)
        assertEquals(settings.effective.apiBaseUrl, configuration.backendApiBaseUrl)
        assertEquals(settings.effective.sessionNamespace, configuration.backendSessionNamespace)
        assertEquals(settings.effective.ssoAccountUrl, configuration.ssoAccountUrl)
        assertEquals(settings.effective.ssoAuthUrl, configuration.ssoAuthUrl)
        assertEquals(settings.effective.ssoTokenUrl, configuration.ssoTokenUrl)
        assertEquals(settings.effective.ssoLogoutUrl, configuration.ssoLogoutUrl)
        assertEquals(settings.effective.ssoClientId, configuration.ssoClientId)
        assertEquals(settings.effective.ssoRedirectUri, configuration.ssoRedirectUri)
        assertEquals(BuildConfig.FLAVOR == "prd", configuration.productionBuild)
        assertEquals(settings.selectorVisible, configuration.environmentSelectorVisible)
        assertEquals(BuildConfig.ENABLE_MOCK_BACKEND, configuration.mockBackendEnabled)
        var selected: br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment? = null
        val changer = ChangeEnvironment {
            selected = it
            true
        }
        val account = HostFeatureConfigurationModule.accountEnvironmentDependencies(settings, changer)
        assertSame(settings.effective, account.effectiveEnvironment)
        assertEquals(settings.selected, account.selectedEnvironment)
        assertEquals(settings.selectorVisible, account.selectorVisible)
        assertSame(changer, account.changeEnvironment)
        runBlocking { assertTrue(account.changeEnvironment(settings.selected)) }
        assertEquals(settings.selected, selected)
    }

    @Test
    fun networkBindingInstallsAllStableProvidersAndHostRuntimeInitializesOnlyOnce() {
        val context = compose.activity.applicationContext
        val settings = ProcessEnvironmentSettings(context)
        val tokens = SessionTokenStore()
        val api = LoteriasApiProvider()
        val legacy = BackendApiProvider()
        val history = BetsHistoryApiProvider()
        val payment = PaymentApiProvider()
        val purchase = PurchaseBackendApiProvider()
        val prize = PrizeBackendApiProvider()
        val verification = AccountVerificationBackendProvider()
        val notification = NotificationReadApiProvider()
        val inbox = NotificationInboxApiProvider()
        val logout = SessionLogoutApiProvider()
        val clock = AppNetworkBindings.serverClock()
        assertSame(clock, AppNetworkBindings.serverTimeSource(clock))
        val clients = AppNetworkBindings.networkClients(
            context,
            settings,
            tokens,
            RegularSessionRefreshExecutor { error("Client installation must not refresh credentials") },
            clock,
            AppNetworkBindings.networkApiProviders(
                api, legacy, history, payment, purchase, prize, verification, notification, inbox, logout
            )
        )
        assertSame(clients.networkSecurity, AppNetworkBindings.networkSecurity(clients))
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("Initialization cannot authorize")
            override suspend fun completeAuthorization(result: Intent?) = error("Initialization cannot exchange credentials")
            override fun cancelAuthorization() = error("Initialization cannot cancel a session")
            override fun logout() = error("Initialization cannot log out")
        }
        val runtime = HostFeatureConfigurationModule.appHostRuntime(
            settings,
            authentication,
            tokens,
            dagger.Lazy { clients },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        val installed = api.current()
        listOf(
            legacy.current(),
            history.current(),
            payment.current(),
            purchase.current(),
            prize.current(),
            verification.current(),
            notification.current(),
            inbox.current(),
            logout.current()
        ).forEach { assertNotNull(it) }
        runtime.initialize()
        assertSame("Host initialization cannot replace clients twice", installed, api.current())
        val change = HostFeatureConfigurationModule.changeEnvironment(runtime)
        runBlocking { assertFalse(change(settings.selected)) }
        assertEquals(settings.effective, ProcessEnvironmentSettings(context).effective)
    }

    @Test
    fun initializationDistinguishesUnvalidatedNetworkFromDisconnectedDevice() {
        val context = compose.activity
        val manager = requireNotNull(context.getSystemService(ConnectivityManager::class.java))
        val shadow = shadowOf(manager)
        shadow.setActiveNetworkInfo(
            ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED, ConnectivityManager.TYPE_WIFI, 0, true, true)
        )
        val active = requireNotNull(manager.activeNetwork)
        val capabilities = ShadowNetworkCapabilities.newInstance()
        shadow.setNetworkCapabilities(active, capabilities)
        val availability = AppInitializationBindings.networkAvailability(context)
        assertEquals(ConnectionAvailability.DISCONNECTED, availability.current())
        shadowOf(capabilities).addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        assertEquals(ConnectionAvailability.UNKNOWN, availability.current())
        shadowOf(capabilities).addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        assertEquals(ConnectionAvailability.CONNECTED, availability.current())
        shadowOf(capabilities).removeCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        assertEquals(ConnectionAvailability.UNKNOWN, availability.current())
        shadow.setActiveNetworkInfo(null)
        assertEquals(ConnectionAvailability.DISCONNECTED, availability.current())
        val first = AppInitializationBindings.foregroundSessionBackgroundState()
        val second = AppInitializationBindings.foregroundSessionBackgroundState()
        first.pending = true
        assertNotSame(first, second)
        assertFalse(second.pending)
        assertSame(DefaultCoroutineDispatchers, AppInitializationBindings.dispatchers())
        assertTrue(Duration.between(Instant.now(), AppInitializationBindings.localClock().instant()).abs() < Duration.ofSeconds(2))
    }

    @Test
    fun startupLoginAndCartPresentersForwardTheirRetryCallbacks() {
        val mode = mutableStateOf(0)
        var retries = 0
        val network = NetworkAvailability { ConnectionAvailability.CONNECTED }
        val startup = HostFeatureConfigurationModule.startupStatusPresentation()
        val offline = HostFeatureConfigurationModule.loginConnectionPresentation(network)
        val cart = HostFeatureConfigurationModule.cartRecoveryStatusPresentation()
        compose.setContent {
            CaixaDSTheme {
                when (mode.value) {
                    0 -> startup.Content(StartupStatusKind.UNAVAILABLE, { retries++ }, network, false)
                    1 -> offline.Offline({ retries++ }, false)
                    else -> cart.Content(false, { retries++ }, network)
                }
            }
        }
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed().performClick()
        compose.runOnIdle { mode.value = 1 }
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed().performClick()
        compose.runOnIdle { mode.value = 2 }
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(3, retries) }
    }
}
