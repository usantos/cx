package br.gov.caixa.loterias.feature.account

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.core.app.ActivityOptionsCompat
import br.gov.caixa.loterias.feature.account.presentation.permission.loginNotificationPermissionReady
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountPermissionUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private var ready = false
    private val registry = object : ActivityResultRegistry(), ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry get() = this
        var code = 0
        var launches = 0
        var fail = false
        var requested = emptyList<String>()
        override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
            launches++
            code = requestCode
            requested = (input as Array<*>).filterIsInstance<String>()
            if (fail) throw IllegalStateException("Host no longer accepts permission request")
        }
    }
    private fun show(simulated: Boolean) {
        val application = RuntimeEnvironment.getApplication()

        @Suppress("DEPRECATION")
        val info = application.packageManager.getPackageInfo(application.packageName, 0)
        info.requestedPermissions =
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        shadowOf(application.packageManager).installPackage(info)
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registry) {
                ready =
                    loginNotificationPermissionReady(simulated)
            }
        }
        flush()
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }

    @Test fun `simulated backend requires no platform permission collection`() {
        show(true)
        compose.runOnIdle {
            assertTrue(ready)
            assertEquals(0, registry.launches)
        }
    }

    @Test fun `runtime collection permission denial never blocks authentication`() {
        shadowOf(
            RuntimeEnvironment.getApplication()
        ).denyPermissions(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        show(false)
        compose.runOnIdle {
            assertFalse(ready)
            assertEquals(1, registry.launches)
            assertTrue(registry.requested.isNotEmpty())
            registry.dispatchResult(registry.code, registry.requested.associateWith { false })
        }
        flush()
        compose.runOnIdle {
            assertTrue(ready)
            assertEquals(1, registry.launches)
        }
    }

    @Test fun `host permission launcher failure still releases login entry`() {
        shadowOf(
            RuntimeEnvironment.getApplication()
        ).denyPermissions(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        registry.fail = true
        show(false)
        compose.runOnIdle {
            assertTrue(ready)
            assertEquals(1, registry.launches)
        }
    }
}
