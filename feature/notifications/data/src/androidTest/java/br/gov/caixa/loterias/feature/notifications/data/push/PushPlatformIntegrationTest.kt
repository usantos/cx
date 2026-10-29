package br.gov.caixa.loterias.feature.notifications.data.push

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.feature.notifications.data.datasource.push.PushTokenDataSourceImpl
import br.gov.caixa.loterias.feature.notifications.data.repository.PushRegistrationLocalRepositoryImpl
import com.google.firebase.messaging.RemoteMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PushPlatformIntegrationTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun registrationStorePersistsOnlyTheConfirmedMarker() = kotlinx.coroutines.runBlocking {
        val fixtureContext = instrumentation.context
        val identity = br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity("a".repeat(DIGEST_WIDTH))
        val environment = br.gov.caixa.loterias.core.domain.persistence.secureEnvironmentKey(
            "fixture",
            java.util.UUID.randomUUID().toString()
        )
        fun repository() = PushRegistrationLocalRepositoryImpl(fixtureContext, { environment }, { it === identity })
        val store = repository()
        assertFalse(store.contains(identity, "b".repeat(DIGEST_WIDTH)))
        store.remember(identity, "b".repeat(DIGEST_WIDTH))
        assertTrue(repository().contains(identity, "b".repeat(DIGEST_WIDTH)))
        assertFalse(store.contains(identity, "c".repeat(DIGEST_WIDTH)))
        repository().remember(identity, "c".repeat(DIGEST_WIDTH))
        assertTrue(store.contains(identity, "c".repeat(DIGEST_WIDTH)))
    }

    @Test fun firebaseTokenSourceInitializesItsDefaultAppWithoutRequestingAToken() {
        val source = PushTokenDataSourceImpl.initialize(
            context,
            PushFirebaseOptions("1:123456789:android:abcdef", "AIzaSyTestKey", "simlo-test", "123456789")
        )
        assertNotNull(source)
    }

    @Test fun notificationChannelAndMissingPermissionGuardAreAppliedToBothMessageShapes() {
        val notifications = context.getSystemService(NotificationManager::class.java)
        val guardedContext = object : ContextWrapper(context) {
            override fun checkSelfPermission(permission: String) = PackageManager.PERMISSION_DENIED
        }
        PushNotifications.createChannel(context)
        assertEquals("Loterias", notifications.getNotificationChannel("CanalLoteriasId").name)

        PushNotifications.show(guardedContext, "title", "body", Activity::class.java, android.R.drawable.ic_dialog_info)
        showRemoteMessage(
            guardedContext,
            RemoteMessage(
                Bundle().apply {
                    putString("google.message_id", "notification-message")
                    putString("from", "sender")
                    putString("gcm.n.e", "1")
                    putString("gcm.n.title", "notification title")
                    putString("gcm.n.body", "notification body")
                }
            ),
            Activity::class.java,
            android.R.drawable.ic_dialog_info
        )
        showRemoteMessage(
            guardedContext,
            RemoteMessage.Builder("sender").setMessageId("data-message")
                .setData(mapOf("title" to "data title", "message" to "data body")).build(),
            Activity::class.java,
            android.R.drawable.ic_dialog_info
        )
        assertTrue(NotificationManagerCompat.from(context).activeNotifications.none { it.id == 0 })

        val grantedContext = object : ContextWrapper(context) {
            override fun checkSelfPermission(permission: String) = PackageManager.PERMISSION_GRANTED
        }
        PushNotifications.show(grantedContext, "visible title", "visible body", Activity::class.java, android.R.drawable.ic_dialog_info)
        NotificationManagerCompat.from(context).cancel(0)
    }
    private companion object {
        const val DIGEST_WIDTH = 64
    }
}
