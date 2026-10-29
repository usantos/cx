// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.queue

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper
import br.gov.caixa.loterias.core.queue.android.broadcastQueueEvent
import br.gov.caixa.loterias.core.queue.android.registerPrivateQueueReceiver
import br.gov.caixa.loterias.core.queue.android.startFilaBraQueue
import br.gov.caixa.loterias.core.queue.gateway.FilaBraQueueGateway
import br.gov.caixa.loterias.core.queue.parser.QueueConsoleEvent
import com.filabra.adapter.android.FilaBRAAndroidService
import com.filabra.adapter.android.FilaBRAClient
import com.filabra.adapter.android.FilaBRAClientListener
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class QueueAndroidContractTest {
    @Test fun `queue events broadcast only to own package with exact SDK wire fields`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val application = RuntimeEnvironment.getApplication()
        val count = shadowOf(application).broadcastIntents.size
        broadcastQueueEvent(activity, QueueConsoleEvent.Ignore)
        assertEquals(count, shadowOf(application).broadcastIntents.size)
        broadcastQueueEvent(activity, QueueConsoleEvent.Join(42))
        assertEquals("42", FilaBRAAndroidService.getPreference(activity, "mostRecentRequestNumber"))
        var event = shadowOf(application).broadcastIntents.last()
        assertEquals("${activity.packageName}.com.filabra.adapter.FILABRA_BROADCAST", event.action)
        assertEquals(activity.packageName, event.`package`)
        assertEquals("JOIN", event.getStringExtra("result"))
        assertEquals(42, event.getIntExtra("request", -1))
        broadcastQueueEvent(activity, QueueConsoleEvent.Pass("https://target.example", "test", 25, 5))
        event = shadowOf(application).broadcastIntents.last()
        assertEquals("SUCCESS", event.getStringExtra("result"))
        assertEquals("https://target.example", event.getStringExtra("target"))
        assertEquals("test", event.getStringExtra("passType"))
        assertEquals(25L, event.getLongExtra("when", -1))
        assertEquals(5, event.getIntExtra("pl", -1))
        broadcastQueueEvent(activity, QueueConsoleEvent.Error("queue unavailable"))
        event = shadowOf(application).broadcastIntents.last()
        assertEquals("ERROR", event.getStringExtra("result"))
        assertEquals("queue unavailable", event.getStringExtra("message"))
    }

    @Test fun `private receiver receives application event and unregister stops delivery`() {
        val application = RuntimeEnvironment.getApplication()
        var deliveries = 0
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                deliveries++
            }
        }
        registerPrivateQueueReceiver(application, receiver, IntentFilter("queue.test"))
        application.sendBroadcast(Intent("queue.test").setPackage(application.packageName))
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, deliveries)
        application.unregisterReceiver(receiver)
        application.sendBroadcast(Intent("queue.test").setPackage(application.packageName))
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, deliveries)
    }

    @Test fun `SDK callbacks act only for the current composition lease and Back abandons current activity`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        var current = true
        var passes = 0
        var offline = 0
        var errors = 0
        com.filabra.adapter.FilaBRAConfig.debug = true
        startFilaBraQueue(FilaBraQueueGateway("account", "queue"), activity, Activity::class.java, {
            current
        }, { passes++ }, { offline++ }, { errors++ })
        assertNull(FilaBRAClient.latest.url)
        assertNull(FilaBRAClient.latest.target)
        assertEquals("account", FilaBRAClient.latest.account)
        assertEquals("queue", FilaBRAClient.latest.queue)
        assertTrue(FilaBRAClient.latest.started)
        assertEquals(Activity::class.java, FilaBRAClient.latest.activityClass)
        val listener = FilaBRAClient.latest.listener
        listener.onShow()
        listener.onJoin(123)
        listener.onPass("passed")
        listener.onNoInternet()
        listener.onError("error")
        assertEquals(listOf(1, 1, 1), listOf(passes, offline, errors))
        listener.onAbandon("Other")
        assertFalse(activity.isFinishing)
        current = false
        listener.onPass("stale")
        listener.onNoInternet()
        listener.onError("stale")
        listener.onAbandon("Back")
        assertEquals(listOf(1, 1, 1), listOf(passes, offline, errors))
        assertFalse(activity.isFinishing)
        current = true
        listener.onAbandon("Back")
        assertTrue(activity.isFinishing)
        assertFalse(com.filabra.adapter.FilaBRAConfig.debug)
    }
}
