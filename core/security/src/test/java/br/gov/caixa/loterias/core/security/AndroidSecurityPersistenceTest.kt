// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.security

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.auth.authorization.WeakBiometricAvailability
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSignature
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetEnvelope
import br.gov.caixa.loterias.core.security.authentication.loginAuthorizationRequestPolicy
import br.gov.caixa.loterias.core.security.keyset.FirebaseKeySetOptions
import br.gov.caixa.loterias.core.security.keyset.PreferencesSignedKeySetStore
import br.gov.caixa.loterias.core.security.topaz.isolateTopazMockComponents
import br.gov.caixa.loterias.core.security.topaz.topazMockComponents
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AndroidSecurityPersistenceTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Test fun `accepted signed envelope round trips and malformed cache fails closed`() {
        val store = PreferencesSignedKeySetStore(context)
        assertNull(store.read())
        val envelope = SignedKeySetEnvelope("encoded-payload", listOf(KeySetSignature("key-1", "signature")))
        store.write(envelope)
        assertEquals(envelope, PreferencesSignedKeySetStore(context).read())
        context.getSharedPreferences("signed_tls_keyset", 0).edit().putString("last_accepted_envelope", "{broken").commit()
        assertNull(store.read())
        assertEquals("sender", FirebaseKeySetOptions("app", "key", "project", "sender").senderId)
    }

    @Test fun `simulation persists original package manager settings then production restores them`() {
        val manager = context.packageManager
        topazMockComponents.forEach { shadowOf(manager).addActivityIfNotPresent(ComponentName(context.packageName, it)) }
        val special = ComponentName(context.packageName, topazMockComponents.first())
        manager.setComponentEnabledSetting(special, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        isolateTopazMockComponents(context, true)
        assertTrue(
            topazMockComponents.all {
                manager.getComponentEnabledSetting(ComponentName(context.packageName, it)) ==
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
        )
        isolateTopazMockComponents(context, true)
        isolateTopazMockComponents(context, false)
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, manager.getComponentEnabledSetting(special))
        assertEquals(
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
            manager.getComponentEnabledSetting(ComponentName(context.packageName, topazMockComponents.last()))
        )
        assertTrue(context.getSharedPreferences("topaz_mock_component_overrides", 0).all.isEmpty())
    }

    @Test fun `corrupt stored component overrides reject unknown types and invalid states`() {
        val prefs = context.getSharedPreferences("topaz_mock_component_overrides", 0)
        prefs.edit().putBoolean("saved", true).putString("state:${topazMockComponents.first()}", "invalid").commit()
        assertTrue(runCatching { isolateTopazMockComponents(context, true) }.isFailure)
        prefs.edit().clear().putBoolean("saved", true).putInt("state:${topazMockComponents.first()}", 9).commit()
        assertTrue(runCatching { isolateTopazMockComponents(context, false) }.isFailure)
    }

    @Test fun `simulated authorization never calls device suppliers and production captures their true values`() {
        var calls = 0
        val device = AuthorizationDeviceInfoProvider {
            calls++
            "device|value"
        }
        val biometric = WeakBiometricAvailability {
            calls++
            true
        }
        val simulated = loginAuthorizationRequestPolicy(true, device, biometric).capture()
        assertTrue(simulated.additionalParameters.isEmpty())
        assertNull(simulated.scope)
        assertEquals(0, calls)
        val production = loginAuthorizationRequestPolicy(false, device, biometric).capture()
        assertEquals("device-value", production.additionalParameters["deviceid"])
        assertEquals("openid profile offline_access", production.scope)
        assertEquals(2, calls)
    }
}
