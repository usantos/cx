package br.gov.caixa.loterias.core.queue.android

import android.app.Activity
import android.content.Intent
import br.gov.caixa.loterias.core.queue.parser.QueueConsoleEvent
import com.filabra.adapter.android.FilaBRAAndroidService

fun broadcastQueueEvent(activity: Activity, event: QueueConsoleEvent) {
    val intent = Intent().setAction("${activity.packageName}.com.filabra.adapter.FILABRA_BROADCAST")
        .setPackage(activity.packageName)
    when (event) {
        is QueueConsoleEvent.Join -> {
            FilaBRAAndroidService.setPreference(activity, "mostRecentRequestNumber", event.request.toString())
            intent.putExtra("result", "JOIN").putExtra("request", event.request)
        }
        is QueueConsoleEvent.Pass -> intent.putExtra("result", "SUCCESS")
            .putExtra("target", event.target)
            .putExtra("passType", event.passType)
            .putExtra("when", event.delayMillis)
            .putExtra("pl", event.passedLifetime)
        is QueueConsoleEvent.Error -> intent.putExtra("result", "ERROR").putExtra("message", event.message)
        QueueConsoleEvent.Ignore -> return
    }
    activity.sendBroadcast(intent)
}
