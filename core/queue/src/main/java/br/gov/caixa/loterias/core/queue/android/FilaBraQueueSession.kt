package br.gov.caixa.loterias.core.queue.android

import android.app.Activity
import br.gov.caixa.loterias.core.queue.gateway.FilaBraQueueGateway
import br.gov.caixa.loterias.core.queue.gateway.QueueListener

/** Funções de retorno do SDK retêm apenas a concessão da composição atual; os ViewModels nunca recebem Activity. */
fun startFilaBraQueue(gateway: FilaBraQueueGateway, activity: Activity, queueActivityClass: Class<out Activity>, isCurrent: () -> Boolean,
    onPass: () -> Unit, onNoInternet: () -> Unit, onError: () -> Unit,
) {
    gateway.start(
        activity,
        queueActivityClass,
        object : QueueListener {
            override fun onPass(passType: String) {
                if (isCurrent()) onPass()
            }
            override fun onNoInternet() {
                if (isCurrent()) onNoInternet()
            }
            override fun onError(message: String) {
                if (isCurrent()) onError()
            }
            override fun onShow() = Unit
            override fun onAbandon(cause: String) {
                if (isCurrent() && cause == "Back") activity.finish()
            }
            override fun onJoin(request: Int) = Unit
        }
    )
}
