package br.gov.caixa.loterias.feature.notifications

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.auth.fixture.SyntheticSessionFixture
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationLocalRepository
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationRepository
import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.di.PushRuntime
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PushRuntimeIntegrationTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun mockHostCreatesTheChannelButDoesNotRegisterAnAuthenticatedDevice() {
        val attempts = CountDownLatch(1)
        val runtime = runtime(context, attempts)
        val notifications = context.getSystemService(NotificationManager::class.java)
        runtime.start(context.applicationContext as Application)
        runtime.start(context.applicationContext as Application)
        runtime.onNewToken("mock-device-token")

        val channel = notifications.getNotificationChannel("CanalLoteriasId")
        assertNotNull(channel)
        assertEquals("Loterias", channel.name)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
        assertNull(channel.sound)
        assertFalse("mock host must suppress backend registration", attempts.await(1, TimeUnit.SECONDS))
    }

    @Test fun remoteMessageRespectsPermissionAndDeliversItsTitleAndBody() {
        val notifications = context.getSystemService(NotificationManager::class.java)
        val message = RemoteMessage.Builder("sender").setMessageId("adapter-integration")
            .setData(mapOf("title" to "Resultado disponível", "message" to "Confira seu bilhete")).build()
        val deniedContext = object : ContextWrapper(context) {
            override fun checkSelfPermission(permission: String): Int = if (permission == Manifest.permission.POST_NOTIFICATIONS) {
                PackageManager.PERMISSION_DENIED
            } else {
                super.checkSelfPermission(permission)
            }
        }
        notifications.cancel(0)
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                runtime(deniedContext).showRemoteMessage(message, Activity::class.java, android.R.drawable.ic_dialog_info)
                assertTrue(notifications.activeNotifications.none { it.id == 0 })
                // Este APK de teste da biblioteca é temporário. Revogar a permissão encerraria seu processo de instrumentação.
                instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.POST_NOTIFICATIONS)
            }
            runtime(context).showRemoteMessage(message, Activity::class.java, android.R.drawable.ic_dialog_info)
            val deadline = SystemClock.elapsedRealtime() + 5000
            while (notifications.activeNotifications.none { it.id == 0 } && SystemClock.elapsedRealtime() < deadline) {
                SystemClock.sleep(25)
            }
            val delivered = notifications.activeNotifications.single { it.id == 0 }.notification
            assertEquals("Resultado disponível", delivered.extras.getCharSequence(Notification.EXTRA_TITLE).toString())
            assertEquals("Confira seu bilhete", delivered.extras.getCharSequence(Notification.EXTRA_TEXT).toString())
            assertEquals("CanalLoteriasId", delivered.channelId)
            assertEquals(android.R.drawable.ic_dialog_info, delivered.smallIcon.resId)
            assertNotNull(delivered.contentIntent)
        } finally {
            notifications.cancel(0)
        }
    }

    private fun runtime(context: Context, attempts: CountDownLatch = CountDownLatch(1)): PushRuntime {
        val sessions = SessionTokenStore()
        assertTrue(SyntheticSessionFixture.install(sessions))
        assertNotNull(sessions.captureIdentity())
        val register = RegisterPushDevice(
            sessions = sessions,
            cpfFor = { "00000000000" },
            repository = PushRegistrationRepository { _, _, _ ->
                attempts.countDown()
                true
            },
            store = object : PushRegistrationLocalRepository {
                override suspend fun contains(identity: AuthenticatedSessionIdentity, marker: String) = false
                override suspend fun remember(identity: AuthenticatedSessionIdentity, marker: String) = Unit
            }
        )
        return PushRuntime(
            context,
            sessions,
            register,
            HostFeatureConfiguration(
                appVersionName = "test", accountReferrerUri = "test://referrer",
                ssoAccountUrl = "https://example.test/account", ssoClientId = "test-client",
                environmentSelectorVisible = false, homeNavigationEnvironmentLabel = null,
                mockBackendEnabled = true, firebaseAppId = "test-app-id", firebaseApiKey = "test-api-key",
                firebaseProjectId = "test-project", firebaseSenderId = "123",
                backendApiBaseUrl = "https://example.test/", backendSessionNamespace = "test",
                ssoAuthUrl = "https://example.test/auth", ssoTokenUrl = "https://example.test/token",
                ssoLogoutUrl = "https://example.test/logout", ssoRedirectUri = "test://redirect",
                productionBuild = false
            )
        )
    }
}
