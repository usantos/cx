package br.gov.caixa.loterias.feature.account

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks
import br.gov.caixa.loterias.core.navigation.workflow.openVisitorDrawerDestination
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.navigation.AccountNavigationDependencies
import br.gov.caixa.loterias.feature.account.navigation.accountGraph
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Grafo real da conta e callback de resultado do navegador, com controle apenas das fronteiras externas. */
class VisitorAuthorizationReturnParityTest {
    @get:Rule val compose = createComposeRule()
    private val state = AppSessionState()
    private val registry = CancelRegistry()
    private val dependencies = Dependencies(registry)
    private var identity by mutableStateOf<AuthenticatedSessionIdentity?>(null)
    private lateinit var nav: NavHostController

    @Test fun cancelledSilentPersonalDataAuthorizationLeavesLoginEntry() = cancelPrivate("account/personal-data")

    @Test fun cancelledSilentCardsAuthorizationLeavesLoginEntry() = cancelPrivate("payment/cards")

    @Test fun cancelledSilentPurchasesAuthorizationLeavesLoginEntry() = cancelPrivate("tickets/purchases")

    @Test fun cancelledSilentHomePoolAuthorizationLeavesEntryWithoutOpeningAnotherPrompt() {
        show("home/pool/2/NORMAL", openDestination = false)
        openBrowser()
        compose.runOnIdle { registry.cancel() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(LoginNavigation.pattern, nav.currentDestination?.route)
            assertNull(state.visitorLoginRequest)
            assertEquals("home/pool/2/NORMAL", state.visitorReturnRoute)
        }
    }

    @Test fun cancellationDoesNotReopenVisitorPromptWhenSessionIsAlreadyAuthenticated() {
        show("payment/cards")
        openBrowser()
        compose.runOnIdle {
            identity = AuthenticatedSessionIdentity("a".repeat(64))
            registry.cancel()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(LoginNavigation.pattern, nav.currentDestination?.route)
            assertNull(state.visitorLoginRequest)
            assertNotNull(identity)
            assertNull(state.visitorReturnOwnerKey)
        }
    }

    private fun cancelPrivate(route: String) {
        show(route)
        openBrowser()
        compose.runOnIdle { registry.cancel() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(LoginNavigation.pattern, nav.currentDestination?.route)
            assertEquals(route, state.visitorReturnRoute)
            assertNull(state.visitorReturnOwnerKey)
            assertNull("Silent failure must leave login entry without re-arming the visitor prompt", state.visitorLoginRequest)
        }
        compose.onNodeWithText("Experimente").assertIsDisplayed()
    }

    private fun openBrowser() {
        compose.onNodeWithText("Abrir").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(LoginNavigation.pattern, nav.currentDestination?.route)
            assertTrue(nav.currentBackStackEntry?.arguments?.getBoolean("silent") == true)
            assertFalse(nav.currentBackStackEntry?.arguments?.getBoolean("authorize") == true)
            assertEquals("Automatic login must reach the controlled browser gateway", 1, dependencies.browserCreations.get())
            assertNotNull("Browser launcher must use the controlled ActivityResultRegistry", registry.requestCode)
        }
    }

    private fun show(route: String, openDestination: Boolean = true) {
        val session = object : SessionNavigationActions {
            override val state = this@VisitorAuthorizationReturnParityTest.state
            override val identity: AuthenticatedSessionIdentity? get() = this@VisitorAuthorizationReturnParityTest.identity
            override val privateEntryKey = "visitor-test"
            override val isPrivateSessionCurrent: () -> Boolean = { false }
            override val onLogout: () -> Unit = {}
            override val requestVisitorLogin: (String) -> Unit = { state.visitorLoginRequest = it }
            override val recheckAccountCompletion: () -> Unit = {}
            override val onAuthenticated: (String) -> Unit = { error("Cancelled SSO cannot authenticate") }
            override val onTryNow: () -> Unit = {}
        }
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registry) {
                CaixaDSTheme {
                    nav = rememberNavController()
                    NavHost(
                        nav,
                        startDestination = "home",
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) {
                        composable("home") {
                            TextButton({
                                if (openDestination) {
                                    openVisitorDrawerDestination(route, nav, session.requestVisitorLogin)
                                } else {
                                    session.requestVisitorLogin(route)
                                }
                            }) { Text("Abrir") }
                        }
                        composable("payment/cards") { Text("Cartões cadastrados") }
                        composable("tickets/purchases") { Text("Selecione o mês e ano") }
                        accountGraph(dependencies, nav, session)
                    }
                    state.visitorLoginRequest?.let { requested ->
                        val callbacks = VisitorLoginCallbacks(
                            requested,
                            nav,
                            { state.visitorLoginRequest },
                            { state.visitorLoginRequest = null },
                            { state.visitorReturnRoute = it }
                        )
                        CaixaDSDialog("Atenção", "Efetuar login?", "Sim", callbacks::onPositive, "Não", callbacks::onNegative)
                    }
                }
            }
        }
    }

    private class CancelRegistry : ActivityResultRegistryOwner {
        var requestCode: Int? = null
        override val activityResultRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?,
            ) {
                this@CancelRegistry.requestCode = requestCode
            }
        }
        fun cancel() = activityResultRegistry.dispatchResult(requireNotNull(requestCode), Activity.RESULT_CANCELED, null)
    }

    private class Dependencies(private val registry: CancelRegistry) : AccountNavigationDependencies {
        override val loginNetworkAvailability = br.gov.caixa.loterias.core.common.network.NetworkAvailability {
            br.gov.caixa.loterias.core.common.network.ConnectionAvailability.UNKNOWN
        }
        override val loginConnectionPresentation = object : br.gov.caixa.loterias.feature.account.navigation.LoginConnectionPresentation {
            @Composable override fun Offline(onRetry: () -> Unit, loading: Boolean) = Unit
        }
        val browserCreations = AtomicInteger()
        override val authentication = object : AuthenticationGateway {
            override fun createSilentAuthorizationIntent(): Intent = createAuthorizationIntent()
            override fun createAuthorizationIntent(): Intent {
                browserCreations.incrementAndGet()
                return Intent("visitor.test.sso")
            }
            override suspend fun completeAuthorization(result: Intent?) = error("Cancelled SSO cannot exchange credentials")
            override fun cancelAuthorization() = Unit
            override fun logout() = Unit
        }
        override val accountProfile = GetAccountProfileUseCase { error("Guest profile must not load") }
        override val currentUserIdentity = CurrentUserIdentityProvider { null }
        override val dispatchers = DefaultCoroutineDispatchers
        override val environmentSelectorVisible = false
        override val accountReferrerUri = "https://example.test/return"
        override val appVersionName = "test"
        override val ssoAccountUrl = "https://example.test/account"
        override val ssoClientId = "test"
        override val recalculateAuthorizedLimit = RecalculateAuthorizedLimitUseCase { error("Unexpected request") }
        override val registerAccount = RegisterAccountUseCase { error("Unexpected request") }
        override val resolveZipCode = ResolveZipCodeUseCase { error("Unexpected request") }
        override val updatePersonalData = UpdatePersonalDataUseCase { _, _ -> error("Unexpected request") }
        override val verifyPostLoginAccess = VerifyPostLoginAccessUseCase { error("Cancelled SSO cannot verify access") }

        @Composable override fun loginNotificationPermissionReady(): Boolean {
            check(LocalActivityResultRegistryOwner.current === registry) { "Account graph must retain the controlled result registry" }
            return true
        }

        @Composable override fun LoginEnvironmentSelector(enabled: Boolean) = Unit
    }
}
