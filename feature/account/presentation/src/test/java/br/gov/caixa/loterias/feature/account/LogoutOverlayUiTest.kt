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
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.LogoutOverlay
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutNotice
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LogoutViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Apenas o transporte do navegador é sintético; a orquestração do logout e o consumo do callback são reais. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LogoutOverlayUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private var serverFails = false
    private var cleanupFails = false
    private var signOuts = 0
    private var cleanups = 0
    private var localLogouts = 0
    private var completions = 0
    private var discards = 0
    private val launch = EndSessionLaunch()
    private val response = Intent("synthetic.response")
    private val gateway = object : EndSessionGateway {
        override fun prepare(): EndSessionLaunch = launch
        override fun intentFor(launch: EndSessionLaunch): Intent {
            assertSame(this@LogoutOverlayUiTest.launch, launch)
            return Intent("synthetic.logout")
        }
        override fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome {
            assertSame(this@LogoutOverlayUiTest.launch, launch)
            assertSame(response, result)
            completions++
            return EndSessionOutcome.CANCELLED
        }
        override fun discard(launch: EndSessionLaunch) {
            assertSame(this@LogoutOverlayUiTest.launch, launch)
            discards++
        }
    }
    private val registry = object : ActivityResultRegistry(), ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry get() = this
        var code = 0
        var launches = 0
        var fail = false
        override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
            assertEquals("synthetic.logout", (input as Intent).action)
            launches++
            code = requestCode
            if (fail) throw IllegalStateException("Synthetic host failure")
        }
    }
    private fun show(): LogoutViewModel {
        val model = LogoutViewModel(
            object : SessionAuthentication {
                override fun cancelAuthorization() = Unit
                override fun logout() {
                    localLogouts++
                }
            },
            gateway,
            ClearLocalSessionDataUseCase {
                cleanups++
                check(!cleanupFails) { "Synthetic persistence failure" }
            },
            SignOutSessionUseCase {
                signOuts++
                if (serverFails) {
                    SessionSignOutResult.Rejected(
                        ServiceFailure.ConnectionFailed(ServiceConnectionFailure.CONNECTION)
                    )
                } else {
                    SessionSignOutResult.Confirmed(identity)
                }
            },
            object : AuthenticatedSessionIdentityProvider {
                override fun captureIdentity() = identity
                override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === this@LogoutOverlayUiTest.identity
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registry) { CaixaDSTheme { LogoutOverlay(model, gateway) } }
        }
        compose.runOnIdle { assertTrue(model.start()) }
        flush()
        return model
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }

    @Test fun `browser cancellation is consumed once and explicit acknowledgment dismisses notice`() {
        val model = show()
        compose.runOnIdle {
            assertEquals(1, registry.launches)
            registry.dispatchResult(registry.code, Activity.RESULT_OK, response)
        }
        flush()
        compose.onNodeWithText(
            "Você saiu deste aplicativo. O encerramento da sessão no navegador foi cancelado e não pôde ser confirmado."
        ).assertExists()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertNull(model.state.value.notice)
            assertEquals(1, completions)
            assertEquals(1, discards)
            assertEquals(1, signOuts)
            assertEquals(1, localLogouts)
        }
    }

    @Test fun `launcher failure reports unconfirmed logout without replaying authorization`() {
        registry.fail = true
        val model = show()
        compose.onNodeWithText(
            "Você saiu deste aplicativo, mas não foi possível confirmar o encerramento da sessão no navegador."
        ).assertExists()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.runOnIdle {
            assertNull(model.state.value.notice)
            assertEquals(1, registry.launches)
            assertEquals(0, completions)
            assertEquals(1, discards)
        }
    }

    @Test fun `server and local cleanup failures require separate explicit retry actions`() {
        serverFails = true
        cleanupFails = true
        val model = show()
        compose.runOnIdle {
            assertEquals(LogoutNotice.SERVER_FAILURE, model.state.value.notice)
            assertEquals(0, localLogouts)
            serverFails =
                false
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(LogoutNotice.LOCAL_DATA_FAILURE, model.state.value.notice)
            assertEquals(0, registry.launches)
            cleanupFails =
                false
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        flush()
        compose.runOnIdle {
            assertNull(model.state.value.notice)
            assertTrue(model.state.value.waitingForBrowser)
            assertEquals(1, registry.launches)
            assertEquals(2, signOuts)
            assertEquals(2, cleanups)
            assertEquals(1, localLogouts)
        }
    }
}
