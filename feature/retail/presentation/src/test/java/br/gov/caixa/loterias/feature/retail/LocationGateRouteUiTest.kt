package br.gov.caixa.loterias.feature.retail

import android.Manifest
import android.content.ContextWrapper
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.retail.domain.model.RetailLocationEligibility
import br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocationUseCase
import br.gov.caixa.loterias.feature.retail.presentation.route.LocationGateRoute
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.LocationGateViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LocationGateRouteUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val intents = mutableListOf<Intent>()
    private val registry = object : ActivityResultRegistry(), ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry get() = this
        var code = 0
        var launches = 0
        var permissions: List<String> = emptyList()
        override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
            code = requestCode
            launches++
            permissions = (input as Array<*>).filterIsInstance<String>()
        }
    }
    private fun providers(enabled: Boolean) {
        val manager = compose.activity.getSystemService(LocationManager::class.java)
        shadowOf(manager).setProviderEnabled(LocationManager.GPS_PROVIDER, enabled)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, false)
    }
    private fun show(model: LocationGateViewModel, withoutActivity: Boolean = false, allowed: () -> Unit = {}) {
        val base = if (withoutActivity) RuntimeEnvironment.getApplication() else compose.activity
        val context = object : ContextWrapper(base) {
            override fun startActivity(intent: Intent) {
                intents += intent
            }
        }
        compose.setContent {
            CompositionLocalProvider(LocalContext provides context, LocalActivityResultRegistryOwner provides registry) {
                CaixaDSTheme { LocationGateRoute(model, "1.2.3", R.drawable.icon_loteria, onAllowed = allowed) }
            }
        }
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }

    @Test fun `disabled provider sends user to Android location settings without querying location`() {
        providers(false)
        val model = LocationGateViewModel(object : CheckRetailLocationUseCase {
            override suspend fun invoke(mockInsideBrazil: Boolean): RetailLocationEligibility = error("Provider disabled")
        })
        show(model)
        flush()
        compose.onNodeWithText("Ir para configurações").performClick()
        compose.runOnIdle {
            assertEquals(LocationGateStatus.PROVIDER_DISABLED, model.state.value.status)
            assertEquals(Settings.ACTION_LOCATION_SOURCE_SETTINGS, intents.single().action)
            assertEquals(0, registry.launches)
        }
    }

    @Test fun `permanent permission denial opens only own application settings`() {
        providers(true)
        shadowOf(
            RuntimeEnvironment.getApplication()
        ).denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val model = LocationGateViewModel(object : CheckRetailLocationUseCase {
            override suspend fun invoke(mockInsideBrazil: Boolean): RetailLocationEligibility = error("Permission denied")
        })
        show(model)
        flush()
        compose.runOnIdle {
            assertEquals(1, registry.launches)
            assertEquals(
                setOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                registry.permissions.toSet()
            )
            registry.dispatchResult(
                registry.code,
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to false,
                    Manifest.permission.ACCESS_COARSE_LOCATION to false
                )
            )
        }
        flush()
        compose.runOnIdle { assertEquals(LocationGateStatus.PERMISSION_PERMANENTLY_DENIED, model.state.value.status) }
        compose.onNodeWithText("Permitir acesso a localização").performClick()
        compose.runOnIdle {
            assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intents.single().action)
            assertEquals("package", intents.single().data!!.scheme)
            assertEquals(compose.activity.packageName, intents.single().data!!.schemeSpecificPart)
        }
    }

    @Test fun `permission grant checks location and unavailable result requires explicit retry before entering`() {
        providers(true)
        shadowOf(
            RuntimeEnvironment.getApplication()
        ).denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        var result = RetailLocationEligibility.UNAVAILABLE
        var checks = 0
        var allowed = 0
        val model = LocationGateViewModel(object : CheckRetailLocationUseCase {
            override suspend fun invoke(mockInsideBrazil: Boolean): RetailLocationEligibility {
                checks++
                assertFalse(mockInsideBrazil)
                return result
            }
        })
        show(model) { allowed++ }
        flush()
        compose.runOnIdle {
            shadowOf(
                RuntimeEnvironment.getApplication()
            ).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            registry.dispatchResult(
                registry.code,
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to true,
                    Manifest.permission.ACCESS_COARSE_LOCATION to true
                )
            )
        }
        flush()
        compose.onNodeWithText("Não foi possível recuperar sua localização. Por favor, tente novamente").assertExists()
        compose.runOnIdle {
            assertEquals(1, checks)
            assertEquals(0, allowed)
            result = RetailLocationEligibility.ALLOWED
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(2, checks)
            assertEquals(1, allowed)
        }
    }

    @Test fun `non activity host can retry denied permission but country outside Brazil never enters`() {
        providers(true)
        shadowOf(
            RuntimeEnvironment.getApplication()
        ).denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        var allowed = 0
        var checks = 0
        val model = LocationGateViewModel(object : CheckRetailLocationUseCase {
            override suspend fun invoke(mockInsideBrazil: Boolean): RetailLocationEligibility {
                checks++
                return RetailLocationEligibility.OUTSIDE_BRAZIL
            }
        })
        show(model, withoutActivity = true) { allowed++ }
        flush()
        compose.runOnIdle {
            registry.dispatchResult(
                registry.code,
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to false,
                    Manifest.permission.ACCESS_COARSE_LOCATION to false
                )
            )
        }
        flush()
        compose.runOnIdle {
            assertEquals(LocationGateStatus.PERMISSION_REQUIRED, model.state.value.status)
            assertEquals(0, checks)
        }
        compose.onNodeWithText("Permitir acesso a localização").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(2, registry.launches)
            shadowOf(
                RuntimeEnvironment.getApplication()
            ).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            registry.dispatchResult(
                registry.code,
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to true,
                    Manifest.permission.ACCESS_COARSE_LOCATION to true
                )
            )
        }
        flush()
        compose.onNodeWithText("As apostas de Loterias só podem ser realizadas em território brasileiro.").assertExists()
        compose.runOnIdle {
            assertEquals(1, checks)
            assertEquals(0, allowed)
            assertTrue(intents.isEmpty())
        }
    }
}
