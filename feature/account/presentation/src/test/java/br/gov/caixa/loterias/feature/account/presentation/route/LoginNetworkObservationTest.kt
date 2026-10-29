package br.gov.caixa.loterias.feature.account.presentation.route

import android.net.ConnectivityManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetwork

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LoginNetworkObservationTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun backgroundNetworkRestrictionDoesNotInterruptTheBrowserAuthorization() {
        val host = RouteLifecycle()
        var availability = ConnectionAvailability.CONNECTED
        var interruptions = 0
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides host) {
                ObserveLoginConnection(NetworkAvailability { availability }, { true }, { interruptions++ })
            }
        }
        val manager = compose.activity.getSystemService(ConnectivityManager::class.java)
        val callback = shadowOf(manager).networkCallbacks.single()
        compose.runOnIdle {
            host.registry.currentState = Lifecycle.State.CREATED
            availability = ConnectionAvailability.DISCONNECTED
            callback.onLost(ShadowNetwork.newInstance(1))
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals("Background-only restrictions must keep the SSO request alive", 0, interruptions)
            availability = ConnectionAvailability.CONNECTED
            host.registry.currentState = Lifecycle.State.RESUMED
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, interruptions) }
    }

    @Test fun returningToTheHostWithoutInternetStillInterruptsAuthorization() {
        val host = RouteLifecycle()
        var availability = ConnectionAvailability.CONNECTED
        var interruptions = 0
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides host) {
                ObserveLoginConnection(NetworkAvailability { availability }, { true }, { interruptions++ })
            }
        }
        compose.runOnIdle {
            host.registry.currentState = Lifecycle.State.CREATED
            availability = ConnectionAvailability.DISCONNECTED
            host.registry.currentState = Lifecycle.State.RESUMED
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, interruptions) }
    }

    @Test fun losingInternetWhileTheHostIsVisibleStillInterruptsAuthorization() {
        val host = RouteLifecycle()
        var availability = ConnectionAvailability.CONNECTED
        var interruptions = 0
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides host) {
                ObserveLoginConnection(NetworkAvailability { availability }, { true }, { interruptions++ })
            }
        }
        val manager = compose.activity.getSystemService(ConnectivityManager::class.java)
        val callback = shadowOf(manager).networkCallbacks.single()
        compose.runOnIdle {
            availability = ConnectionAvailability.DISCONNECTED
            callback.onLost(ShadowNetwork.newInstance(1))
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, interruptions) }
    }

    private class RouteLifecycle : LifecycleOwner {
        val registry = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
        override val lifecycle: Lifecycle get() = registry
    }
}
