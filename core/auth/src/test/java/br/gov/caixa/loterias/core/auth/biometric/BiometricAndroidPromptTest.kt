// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.auth.biometric

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.KeyguardManager
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import android.provider.Settings
import br.gov.caixa.loterias.core.auth.appauth.AppAuthBiometricAvailability
import br.gov.caixa.loterias.core.auth.appauth.FlowBiometricManagerShadow
import br.gov.caixa.loterias.core.auth.gateway.launchBiometricEnrollment
import java.util.concurrent.Executor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@Implements(BiometricPrompt::class)
class NativePromptShadow {
    // Cada teste controla o agendamento das funções de retorno nativas; preserva a assinatura do método do SDK.
    @Suppress("UnusedParameter")
    @Implementation
    fun authenticate(cancellation: CancellationSignal, executor: Executor, callback: BiometricPrompt.AuthenticationCallback) {
        signal = cancellation
        result = callback
    }
    companion object {
        var signal: CancellationSignal? = null
        var result: BiometricPrompt.AuthenticationCallback? = null
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [NativePromptShadow::class, FlowBiometricManagerShadow::class])
class BiometricAndroidPromptTest {
    private val context get() = RuntimeEnvironment.getApplication()
    private fun activity() = Robolectric.buildActivity(Activity::class.java).setup().get()

    @Test fun `permission denial returns cancellation and never opens the native prompt`() {
        shadowOf(context).denyPermissions(Manifest.permission.USE_BIOMETRIC)
        var calls = 0
        var response: Intent? = Intent()
        val cancel = showBiometricPrompt(activity(), Intent()) {
            calls++
            response = it
        }
        cancel()
        assertEquals(1, calls)
        assertNull(response)
    }

    @Test fun `native success preserves challenge state and all subsequent callbacks are ignored`() {
        shadowOf(context).grantPermissions(Manifest.permission.USE_BIOMETRIC)
        val results = mutableListOf<Intent?>()
        val cancel = showBiometricPrompt(activity(), Intent().putExtra(BIOMETRIC_STATE, "challenge")) { results += it }
        NativePromptShadow.result!!.onAuthenticationSucceeded(Shadow.newInstanceOf(BiometricPrompt.AuthenticationResult::class.java))
        NativePromptShadow.result!!.onAuthenticationError(1, "late failure")
        assertEquals(1, results.size)
        assertEquals("challenge", results.single()!!.getStringExtra(BIOMETRIC_STATE))
        cancel()
        assertTrue(NativePromptShadow.signal!!.isCanceled)
    }

    @Test fun `native error reports once while disposal cancellation suppresses later success`() {
        shadowOf(context).grantPermissions(Manifest.permission.USE_BIOMETRIC)
        val results = mutableListOf<Intent?>()
        showBiometricPrompt(activity(), Intent().putExtra(BIOMETRIC_STATE, "challenge")) { results += it }
        NativePromptShadow.result!!.onAuthenticationError(7, "locked out")
        NativePromptShadow.result!!.onAuthenticationError(7, "duplicate")
        assertEquals(listOf<Intent?>(null), results)
        results.clear()
        val cancel = showBiometricPrompt(activity(), Intent().putExtra(BIOMETRIC_STATE, "other")) { results += it }
        cancel()
        NativePromptShadow.result!!.onAuthenticationSucceeded(Shadow.newInstanceOf(BiometricPrompt.AuthenticationResult::class.java))
        assertTrue(results.isEmpty())
        assertTrue(runCatching { showBiometricPrompt(activity(), Intent()) {} }.isFailure)
    }

    @Test fun `modern availability fails closed without permission and keeps enrollment hardware visible`() {
        val availability = AppAuthBiometricAvailability(context)
        shadowOf(context).denyPermissions(Manifest.permission.USE_BIOMETRIC)
        assertFalse(availability.biometricAvailable)
        assertFalse(availability.biometricHardwareAvailable)
        shadowOf(context).grantPermissions(Manifest.permission.USE_BIOMETRIC)
        FlowBiometricManagerShadow.status = BiometricManager.BIOMETRIC_SUCCESS
        assertTrue(availability.biometricAvailable)
        assertTrue(availability.biometricHardwareAvailable)
        FlowBiometricManagerShadow.status = BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
        assertFalse(availability.biometricAvailable)
        assertTrue(availability.biometricHardwareAvailable)
        FlowBiometricManagerShadow.status = BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
        assertFalse(availability.biometricAvailable)
        assertFalse(availability.biometricHardwareAvailable)
        val intents = mutableListOf<Intent>()
        launchBiometricEnrollment { intents += it }
        assertEquals(Settings.ACTION_BIOMETRIC_ENROLL, intents.single().action)
        launchBiometricEnrollment {
            if (it.action ==
                Settings.ACTION_BIOMETRIC_ENROLL
            ) {
                error("missing settings activity")
            } else {
                intents += it
            }
        }
        assertEquals(Settings.ACTION_SETTINGS, intents.last().action)
    }

    @Test
    @Config(sdk = [28])
    fun `pre native prompt devices require secure keyguard and use settings enrollment fallback`() {
        val availability = AppAuthBiometricAvailability(context)
        val keyguard = shadowOf(context.getSystemService(KeyguardManager::class.java))
        keyguard.setIsDeviceSecure(false)
        assertFalse(availability.biometricAvailable)
        keyguard.setIsDeviceSecure(true)
        assertTrue(availability.biometricAvailable)
        assertTrue(availability.biometricHardwareAvailable)
        assertTrue(runCatching { showBiometricPrompt(activity(), Intent()) {} }.isFailure)
        var launched: Intent? = null
        launchBiometricEnrollment { launched = it }
        assertEquals(Settings.ACTION_SETTINGS, launched!!.action)
    }
}
