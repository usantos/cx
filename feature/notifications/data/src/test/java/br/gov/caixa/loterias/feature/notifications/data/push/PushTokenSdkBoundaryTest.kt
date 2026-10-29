package br.gov.caixa.loterias.feature.notifications.data.push

import android.content.Context
import android.os.Looper
import br.gov.caixa.loterias.feature.notifications.data.datasource.push.PushTokenDataSourceImpl
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

/** Inicialização do SDK Firebase/transporte de tokens isolados; nenhuma comunicação de rede Firebase é iniciada. */
@Implements(FirebaseApp::class)
class PushFirebaseAppShadow {
    @Implementation fun getName(): String = FirebaseApp.DEFAULT_APP_NAME
    companion object {
        var existing = false
        var options: FirebaseOptions? = null

        @JvmStatic @Implementation
        fun getApps(context: Context): List<FirebaseApp> {
            require(context.packageName.isNotBlank())
            return if (existing) listOf(Shadow.newInstanceOf(FirebaseApp::class.java)) else emptyList()
        }

        @JvmStatic @Implementation
        fun initializeApp(context: Context, supplied: FirebaseOptions): FirebaseApp {
            require(context.packageName.isNotBlank())
            options = supplied
            existing = true
            return Shadow.newInstanceOf(FirebaseApp::class.java)
        }
    }
}

@Implements(FirebaseMessaging::class)
class PushMessagingShadow {
    @Implementation fun getToken(): Task<String> = token
    companion object {
        var token: Task<String> = Tasks.forResult("token")

        @JvmStatic @Implementation
        fun getInstance(): FirebaseMessaging = Shadow.newInstanceOf(FirebaseMessaging::class.java)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [PushFirebaseAppShadow::class, PushMessagingShadow::class])
class PushTokenSdkBoundaryTest {
    private val configuration = PushFirebaseOptions("app-id", "api-key", "project", "sender")

    @Before fun reset() {
        PushFirebaseAppShadow.existing = false
        PushFirebaseAppShadow.options = null
        PushMessagingShadow.token =
            Tasks.forResult("token")
    }

    @Test fun `initialization uses host options and successful token retrieval returns SDK token`() = runBlocking {
        val fetch = PushTokenDataSourceImpl.initialize(RuntimeEnvironment.getApplication(), configuration)
        assertEquals("app-id", PushFirebaseAppShadow.options!!.applicationId)
        assertEquals("api-key", PushFirebaseAppShadow.options!!.apiKey)
        assertEquals("project", PushFirebaseAppShadow.options!!.projectId)
        assertEquals("sender", PushFirebaseAppShadow.options!!.gcmSenderId)
        val token = async(start = CoroutineStart.UNDISPATCHED) { fetch() }
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("token", token.await())
    }

    @Test fun `existing SDK application is reused and failed token task returns no fabricated token`() = runBlocking {
        PushFirebaseAppShadow.existing = true
        PushMessagingShadow.token = Tasks.forException(IllegalStateException("offline"))
        val fetch = PushTokenDataSourceImpl.initialize(RuntimeEnvironment.getApplication(), configuration)
        assertNull(PushFirebaseAppShadow.options)
        val token = async(start = CoroutineStart.UNDISPATCHED) { fetch() }
        shadowOf(Looper.getMainLooper()).idle()
        assertNull(token.await())
    }

    @Test fun `cancelled caller ignores later successful token completion`() = runBlocking {
        val source = TaskCompletionSource<String>()
        PushMessagingShadow.token = source.task
        val fetch = PushTokenDataSourceImpl.initialize(RuntimeEnvironment.getApplication(), configuration)
        val token = async(start = CoroutineStart.UNDISPATCHED) { fetch() }
        token.cancel()
        source.setResult("late-token")
        shadowOf(Looper.getMainLooper()).idle()
        token.join()
        assertTrue(token.isCancelled)
    }
}
