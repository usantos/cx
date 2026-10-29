package br.gov.caixa.loterias.core.queue.gateway

import android.app.Activity
import com.filabra.adapter.android.FilaBRAClient
import com.filabra.adapter.android.FilaBRAClientListener

interface QueueListener {
    fun onPass(passType: String)
    fun onNoInternet()
    fun onError(message: String)
    fun onShow()
    fun onAbandon(cause: String)
    fun onJoin(request: Int)
}

class FilaBraQueueGateway(
    private val account: String,
    private val queue: String,
) {
    fun start(activity: Activity, queueActivityClass: Class<out Activity>, listener: QueueListener) {
        com.filabra.adapter.FilaBRAConfig.debug = false
        val client = FilaBRAClient(
            activity,
            null,
            account,
            queue,
            null,
            object : FilaBRAClientListener {
                override fun onPass(passType: String) = listener.onPass(passType)
                override fun onNoInternet() = listener.onNoInternet()
                override fun onError(errorMessage: String) = listener.onError(errorMessage)
                override fun onShow() = listener.onShow()
                override fun onAbandon(cause: String) = listener.onAbandon(cause)
                override fun onJoin(request: Int) = listener.onJoin(request)
            },
        )
        client.activityClass = queueActivityClass
        client.go()
    }
}
