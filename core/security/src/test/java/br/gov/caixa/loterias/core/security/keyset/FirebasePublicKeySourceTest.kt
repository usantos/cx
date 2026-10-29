// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.security.keyset

import android.content.Context
import android.os.Looper
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSignature
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetEnvelope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@Implements(FirebaseRemoteConfig::class)
class RemoteKeySetConfigShadow {
    @Implementation fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings): Task<Void> {
        capturedSettings = settings
        return if (failSettings) Tasks.forException(IllegalStateException("settings failure")) else Tasks.forResult(null)
    }

    @Implementation fun fetchAndActivate(): Task<Boolean> {
        fetchCalls++
        return if (failFetch) Tasks.forException(IllegalStateException("offline")) else Tasks.forResult(false)
    }

    @Implementation fun getString(key: String): String {
        requestedKey = key
        return encoded
    }
    companion object {
        var capturedSettings: FirebaseRemoteConfigSettings? = null
        var requestedKey: String? = null
        var fetchCalls = 0
        var failSettings = false
        var failFetch = false
        var encoded = ""
        var selectedApp: FirebaseApp? = null

        @JvmStatic @Implementation
        fun getInstance(app: FirebaseApp): FirebaseRemoteConfig {
            selectedApp = app
            return Shadow.newInstanceOf(FirebaseRemoteConfig::class.java)
        }
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [RemoteKeySetConfigShadow::class, KeySetFirebaseAppShadow::class])
class FirebasePublicKeySourceTest {
    @Before fun reset() {
        RemoteKeySetConfigShadow.capturedSettings = null
        RemoteKeySetConfigShadow.requestedKey = null
        RemoteKeySetConfigShadow.fetchCalls = 0
        RemoteKeySetConfigShadow.failSettings = false
        RemoteKeySetConfigShadow.failFetch = false
        RemoteKeySetConfigShadow.encoded = ""
        KeySetFirebaseAppShadow.existing = null
        KeySetFirebaseAppShadow.options = null
        KeySetFirebaseAppShadow.initializations = 0
    }
    private fun fetch(): SignedKeySetEnvelope? = runBlocking {
        val sdk = Shadow.newInstanceOf(FirebaseRemoteConfig::class.java)
        val pending = async(Dispatchers.Unconfined) { FirebaseRemotePublicKeySetSource(sdk).fetch() }
        shadowOf(Looper.getMainLooper()).idle()
        pending.await()
    }

    @Test fun `default Firebase application uses host options and reuses the shared SDK instance`() {
        val options = FirebaseKeySetOptions("keyset-app", "api-key", "project", "sender")
        assertNotNull(FirebaseRemotePublicKeySetSource.create(RuntimeEnvironment.getApplication(), options))
        assertEquals("keyset-app", KeySetFirebaseAppShadow.options!!.applicationId)
        assertEquals("api-key", KeySetFirebaseAppShadow.options!!.apiKey)
        assertEquals("project", KeySetFirebaseAppShadow.options!!.projectId)
        assertEquals("sender", KeySetFirebaseAppShadow.options!!.gcmSenderId)
        assertSame(KeySetFirebaseAppShadow.existing, RemoteKeySetConfigShadow.selectedApp)
        assertNotNull(FirebaseRemotePublicKeySetSource.create(RuntimeEnvironment.getApplication(), options))
        assertEquals(1, KeySetFirebaseAppShadow.initializations)
    }

    @Test fun `successful Void settings allow fetching and unchanged activation still reads accepted value`() {
        RemoteKeySetConfigShadow.encoded = "  {\"payload\":\"payload\",\"signatures\":[{\"keyId\":\"key\",\"value\":\"signature\"}]}  "
        assertEquals(SignedKeySetEnvelope("payload", listOf(KeySetSignature("key", "signature"))), fetch())
        assertEquals(1, RemoteKeySetConfigShadow.fetchCalls)
        assertEquals(FirebaseRemotePublicKeySetSource.PARAMETER_NAME, RemoteKeySetConfigShadow.requestedKey)
        assertEquals(10L, RemoteKeySetConfigShadow.capturedSettings!!.fetchTimeoutInSeconds)
        assertEquals(3600L, RemoteKeySetConfigShadow.capturedSettings!!.minimumFetchIntervalInSeconds)
    }

    @Test fun `settings and fetch failures stop before reading unconfirmed configuration`() {
        RemoteKeySetConfigShadow.failSettings = true
        assertNull(fetch())
        assertEquals(0, RemoteKeySetConfigShadow.fetchCalls)
        assertNull(RemoteKeySetConfigShadow.requestedKey)
        RemoteKeySetConfigShadow.failSettings = false
        RemoteKeySetConfigShadow.failFetch = true
        assertNull(fetch())
        assertEquals(1, RemoteKeySetConfigShadow.fetchCalls)
        assertNull(RemoteKeySetConfigShadow.requestedKey)
    }

    @Test fun `empty and malformed remote values remain safe cache fallback signals`() {
        RemoteKeySetConfigShadow.encoded = "   "
        assertNull(fetch())
        RemoteKeySetConfigShadow.encoded = "{broken json"
        assertNull(fetch())
    }
}

/** Isola a inicialização do Firebase dos serviços/rede, mantendo a construção real das opções. */
@Implements(FirebaseApp::class)
class KeySetFirebaseAppShadow {
    @Implementation fun getName() = FirebaseApp.DEFAULT_APP_NAME
    companion object {
        var existing: FirebaseApp? = null
        var options: FirebaseOptions? = null
        var initializations = 0

        @JvmStatic @Implementation
        fun getApps(context: Context): List<FirebaseApp> {
            require(context.packageName.isNotBlank())
            return listOfNotNull(existing)
        }

        @JvmStatic @Implementation
        fun initializeApp(context: Context, supplied: FirebaseOptions, name: String): FirebaseApp {
            require(context.packageName.isNotBlank())
            check(name == FirebaseApp.DEFAULT_APP_NAME)
            initializations++
            options = supplied
            return Shadow.newInstanceOf(FirebaseApp::class.java).also { existing = it }
        }
    }
}
