package br.gov.caixa.loterias.feature.account

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.LoginRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class LoginOfflineRecoveryTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun silentLoginFailureLeavesTheLoginEntryWithoutOpeningInteractiveLogin() {
        val fixture = Fixture(ConnectionAvailability.CONNECTED)
        show(fixture, silent = true)
        compose.waitUntil { fixture.launches == 1 }
        compose.runOnIdle {
            fixture.registry.dispatchResult(fixture.requestCode, Activity.RESULT_CANCELED, null)
        }
        compose.waitForIdle()
        compose.onNodeWithText("Experimente").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, fixture.cancelledNavigation)
            assertEquals(0, fixture.exchanges)
            assertEquals(1, fixture.launches)
        }
    }

    @Test fun silentLoginWithoutNetworkStaysOnEntryInsteadOfOfflineScreen() {
        val fixture = Fixture(ConnectionAvailability.DISCONNECTED)
        show(fixture, silent = true)
        compose.onNodeWithText("Experimente").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, fixture.launches) }
    }

    @Test fun unavailableSilentGatewayLeavesEntryAndAllowsManualLogin() {
        val fixture = Fixture(ConnectionAvailability.CONNECTED).apply { silentAvailable = false }
        show(fixture, silent = true)
        compose.onNodeWithText("Experimente").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, fixture.launches) }
        compose.onNodeWithText("Faça login/cadastre-se").performClick()
        compose.runOnIdle { assertEquals(1, fixture.launches) }
    }

    @Test fun silentExchangeFailureReturnsToEntryWithoutAnotherAttemptOrError() {
        val fixture = Fixture(ConnectionAvailability.CONNECTED).apply { exchangeFails = true }
        show(fixture, silent = true)
        compose.waitUntil { fixture.launches == 1 }
        compose.runOnIdle {
            fixture.registry.dispatchResult(fixture.requestCode, Activity.RESULT_OK, Intent("test.silent.failed"))
        }
        compose.waitUntil { fixture.exchanges == 1 && !fixture.model.state.value.isLoading }
        compose.onNodeWithText("Experimente").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, fixture.launches)
            assertNull(fixture.model.state.value.errorMessage)
            assertFalse(fixture.model.state.value.authenticated)
        }
    }

    @Test fun silentSuccessVerifiesTheAccountBeforeContinuing() {
        val fixture = Fixture(ConnectionAvailability.CONNECTED).apply { allowVerification = true }
        show(fixture, silent = true)
        compose.waitUntil { fixture.launches == 1 }
        compose.runOnIdle {
            fixture.registry.dispatchResult(fixture.requestCode, Activity.RESULT_OK, Intent("test.silent.success"))
        }
        compose.waitUntil { fixture.authenticated == 1 }
        compose.runOnIdle {
            assertEquals(1, fixture.exchanges)
            assertEquals(1, fixture.verifications)
            assertEquals(1, fixture.launches)
        }
    }

    @Test fun offlineBeforeLoginNeverOpensBrowserAndRetryWaitsForConnection() {
        val fixture = Fixture(ConnectionAvailability.DISCONNECTED)
        show(fixture)
        compose.onNodeWithText("OFFLINE LOGIN").assertIsDisplayed()
        assertEquals(0, fixture.launches)
        compose.onNodeWithText("RETRY LOGIN").performClick()
        assertEquals(0, fixture.launches)
        compose.runOnIdle { fixture.connection = ConnectionAvailability.CONNECTED }
        compose.onNodeWithText("RETRY LOGIN").performClick()
        compose.runOnIdle { assertEquals(1, fixture.launches) }
    }

    @Test fun networkLossReturnsToOfflineWithoutVisitorCancellationAndIgnoresLateResult() {
        val fixture = Fixture(ConnectionAvailability.CONNECTED)
        show(fixture)
        compose.waitUntil { fixture.launches == 1 }
        fixture.connection = ConnectionAvailability.DISCONNECTED
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.onNodeWithText("OFFLINE LOGIN").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, fixture.returns)
            assertEquals(0, fixture.cancelledNavigation)
            assertFalse(fixture.model.state.value.authenticated)
            fixture.connection = ConnectionAvailability.CONNECTED
        }
        compose.onNodeWithText("RETRY LOGIN").performClick()
        compose.runOnIdle {
            assertEquals(1, fixture.launches)
            fixture.registry.dispatchResult(fixture.requestCode, Activity.RESULT_OK, Intent("stale.sso.result"))
            Snapshot.sendApplyNotifications()
        }
        compose.waitForIdle()
        compose.waitUntil {
            compose.waitForIdle()
            fixture.launches == 2
        }
        compose.runOnIdle {
            assertEquals(2, fixture.launches)
            assertEquals(0, fixture.exchanges)
            assertEquals(0, fixture.cancelledNavigation)
        }
    }

    @Test fun accountSwitchKeepsInteractiveIntentAfterOfflineRetry() {
        val fixture = Fixture(ConnectionAvailability.DISCONNECTED, remembered = true)
        show(fixture, automatic = false)
        compose.onNodeWithText("Trocar usuário").performClick()
        compose.onNodeWithText("OFFLINE LOGIN").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, fixture.launches)
            fixture.connection = ConnectionAvailability.CONNECTED
        }
        compose.onNodeWithText("RETRY LOGIN").performClick()
        compose.waitUntil { fixture.launches == 1 }
        compose.runOnIdle {
            assertEquals(1, fixture.switchRequests)
            assertEquals(0, fixture.exchanges)
            assertFalse(fixture.model.state.value.authenticated)
        }
    }

    private fun show(f: Fixture, silent: Boolean = false, automatic: Boolean = true) {
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides f) {
                CaixaDSTheme {
                    LoginRoute(
                        f.model, f.auth, "test", { f.authenticated++ }, {}, {},
                        authorizeOnEnter = automatic && !silent,
                        silentAuthorizeOnEnter = silent,
                        onAuthorizationCancelled = { f.cancelledNavigation++ },
                        networkAvailability = NetworkAvailability { f.connection },
                        returnFromBrowser = { f.returns++ },
                        offlineScreen = { retry, _ ->
                            CaixaDSText("OFFLINE LOGIN")
                            CaixaDSButton(onClick = retry) { CaixaDSText("RETRY LOGIN") }
                        }
                    )
                }
            }
        }
    }

    private class Fixture(var connection: ConnectionAvailability, val remembered: Boolean = false) : ActivityResultRegistryOwner {
        var switchRequests = 0
        var launches = 0
        var returns = 0
        var exchanges = 0
        var exchangeFails = false
        var silentAvailable = true
        var allowVerification = false
        var verifications = 0
        var authenticated = 0
        var cancelledNavigation = 0
        var requestCode = 0
        val registry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?,
            ) {
                this@Fixture.requestCode = requestCode
                launches++
            }
        }
        override val activityResultRegistry get() = registry
        val auth: AuthenticationGateway = object :
            AuthenticationGateway,
            br.gov.caixa.loterias.feature.account.domain.port.BiometricLoginPreferences {
            override val biometricEnabled get() = remembered
            override val biometricAvailable get() = remembered
            override val biometricCredentialAvailable get() = remembered
            override val biometricFirstName get() = if (remembered) "João" else null
            override fun setBiometricEnabled(enabled: Boolean) = Unit
            override fun createAccountSwitchAuthorizationIntent(): Intent {
                switchRequests++
                return Intent("test.sso.switch")
            }
            override fun createAuthorizationIntent() = Intent("test.sso")
            override fun createSilentAuthorizationIntent() = if (silentAvailable) Intent("test.sso.silent") else null
            override suspend fun completeAuthorization(result: Intent?) {
                exchanges++
                if (exchangeFails) error("Synthetic silent failure")
            }
            override fun cancelAuthorization() = Unit
            override fun logout() = Unit
        }
        val model = LoginViewModel(
            auth,
            verifyAccess = VerifyPostLoginAccessUseCase {
                check(allowVerification) { "No account check expected" }
                verifications++
                br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult.Verified(
                    br.gov.caixa.loterias.feature.account.domain.model.AccountAccess(
                        br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.HOME,
                        false,
                        java.math.BigDecimal.ZERO
                    )
                )
            }
        )
    }
}
