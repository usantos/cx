package br.gov.caixa.loterias.feature.notifications.data.push

import android.Manifest
import android.R
import android.app.Activity
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.os.Bundle
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.data.repository.PushRegistrationLocalRepositoryImpl
import com.google.firebase.messaging.RemoteMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class PushAndroidContractTest {
    private val context get() = RuntimeEnvironment.getApplication()
    private val manager get() = context.getSystemService(NotificationManager::class.java)
    private fun grant() {
        shadowOf(context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    @Test fun `permission denial creates no notification and grant delivers private immutable launch intent`() {
        shadowOf(context).denyPermissions(Manifest.permission.POST_NOTIFICATIONS)
        PushNotifications.show(context, "title", "body", Activity::class.java, R.drawable.ic_dialog_info)
        assertTrue(shadowOf(manager).allNotifications.isEmpty())
        grant()
        PushNotifications.show(context, "title", "body", Activity::class.java, R.drawable.ic_dialog_info)
        val notice = shadowOf(manager).allNotifications.single()
        assertEquals("title", notice.extras.getString(Notification.EXTRA_TITLE))
        assertEquals("body", notice.extras.getString(Notification.EXTRA_TEXT))
        assertTrue(notice.flags and Notification.FLAG_AUTO_CANCEL != 0)
        val intent = shadowOf(notice.contentIntent).savedIntent
        assertEquals(Activity::class.java.name, intent.component!!.className)
        assertTrue(notice.contentIntent.isImmutable)
        val channel = manager.getNotificationChannel("CanalLoteriasId")
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
        assertEquals("Canal Loterias", channel.description)
        assertNull(channel.sound)
    }

    @Test fun `remote payload routes data and notification messages and ignores a bodyless notification`() {
        grant()
        val data = RemoteMessage.Builder("test").addData("title", "data title").addData("message", "data body").build()
        showRemoteMessage(context, data, Activity::class.java, R.drawable.ic_dialog_info)
        assertEquals("data body", shadowOf(manager).allNotifications.single().extras.getString(Notification.EXTRA_TEXT))
        val payload = Bundle().apply {
            putString("gcm.n.e", "1")
            putString("gcm.n.title", "banner")
            putString("gcm.n.body", "notification body")
        }
        showRemoteMessage(context, RemoteMessage(payload), Activity::class.java, R.drawable.ic_dialog_info)
        assertEquals("notification body", shadowOf(manager).allNotifications.single().extras.getString(Notification.EXTRA_TEXT))
        payload.remove("gcm.n.body")
        showRemoteMessage(context, RemoteMessage(payload), Activity::class.java, R.drawable.ic_dialog_info)
        assertEquals("notification body", shadowOf(manager).allNotifications.single().extras.getString(Notification.EXTRA_TEXT))
    }

    @Test fun `registration remembers only latest confirmed marker across instances`() = kotlinx.coroutines.test.runTest {
        val identity = AuthenticatedSessionIdentity("c".repeat(DIGEST_WIDTH))
        val memory = object : SecureRecordStore {
            var snapshot = SecureSnapshot(0, emptyMap())
            override suspend fun read() = snapshot
            override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
                snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
                return snapshot
            }
        }
        val factory = SecureStoreFactory { memory }
        fun createRepository() = PushRegistrationLocalRepositoryImpl(context, { "installation" }, { it === identity }, factory)
        val repository = createRepository()
        assertFalse(repository.contains(identity, "a".repeat(DIGEST_WIDTH)))
        repository.remember(identity, "a".repeat(DIGEST_WIDTH))
        assertTrue(createRepository().contains(identity, "a".repeat(DIGEST_WIDTH)))
        repository.remember(identity, "b".repeat(DIGEST_WIDTH))
        assertFalse(repository.contains(identity, "a".repeat(DIGEST_WIDTH)))
        assertTrue(repository.contains(identity, "b".repeat(DIGEST_WIDTH)))
        assertEquals(mapOf("confirmed" to "b".repeat(DIGEST_WIDTH)), memory.snapshot.values)
        assertTrue(context.getSharedPreferences("push_registration", 0).all.isEmpty())
        assertEquals("project", PushFirebaseOptions("app", "key", "project", "sender").projectId)
    }
    private companion object {
        const val DIGEST_WIDTH = 64
    }
}
