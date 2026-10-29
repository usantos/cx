package br.gov.caixa.loterias.feature.home

import android.Manifest
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import br.gov.caixa.loterias.feature.home.navigation.rememberHomeTicketScannerEntry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeTicketScannerPermissionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun deniedCameraStillOpensDestinationSoManualEntryRemainsAvailable() {
        val registry = PermissionRegistry()
        var opened = 0
        show(false, registry) { opened++ }
        compose.onNodeWithText("Conferir bilhetes").performClick()
        compose.runOnIdle {
            assertEquals(1, opened)
            assertEquals(null, registry.requested)
        }
    }

    @Test fun grantedCameraOpensScannerWithoutAnotherPermissionRequest() {
        val registry = PermissionRegistry()
        var opened = 0
        show(true, registry) { opened++ }
        compose.onNodeWithText("Conferir bilhetes").performClick()
        compose.runOnIdle {
            assertEquals(1, opened)
            assertEquals(null, registry.requested)
        }
    }

    private fun show(granted: Boolean, registry: PermissionRegistry, onOpen: () -> Unit) {
        compose.setContent {
            val baseContext = LocalContext.current
            val context = object : ContextWrapper(baseContext) {
                override fun checkPermission(permission: String, pid: Int, uid: Int): Int =
                    if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
            }
            val owner = object : ActivityResultRegistryOwner {
                override val activityResultRegistry = registry
            }
            CompositionLocalProvider(LocalContext provides context, LocalActivityResultRegistryOwner provides owner) {
                val open = rememberHomeTicketScannerEntry(onOpen)
                Text("Conferir bilhetes", Modifier.clickable(onClick = open))
            }
        }
    }

    private class PermissionRegistry : ActivityResultRegistry() {
        var requested: Any? = null
        override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
            requested = input
        }
    }
}
