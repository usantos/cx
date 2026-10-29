package br.gov.caixa.loterias.apostas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.queue.android.registerPrivateQueueReceiver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.*
import org.junit.Test

class QueueReceiverSecurityTest {
    @Test fun acceptsInternalEventsButRejectsEventsFromAnotherUid() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val action = "${context.packageName}.QUEUE_RECEIVER_SECURITY_TEST"
        val internal = CountDownLatch(1)
        val calls = AtomicInteger()
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) { calls.incrementAndGet(); internal.countDown() }
        }
        registerPrivateQueueReceiver(context, receiver, IntentFilter(action))
        try {
            context.sendBroadcast(Intent(action).setPackage(context.packageName))
            assertTrue("Internal queue event was lost", internal.await(5, TimeUnit.SECONDS))
            instrumentation.uiAutomation.executeShellCommand(
                "am broadcast -a $action -p ${context.packageName}",
            ).use { android.os.ParcelFileDescriptor.AutoCloseInputStream(it).readBytes() }
            // O shell tem outro UID. Conclua a entrega na linha de execução principal antes de verificar.
            instrumentation.waitForIdleSync()
            assertEquals("External sender reached the private queue receiver", 1, calls.get())
        } finally { context.unregisterReceiver(receiver) }
    }
}
