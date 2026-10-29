package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/** Retorna ao mesmo hospedeiro após o lançamento em curso deixar de poder sobrepor a solicitação. */
internal fun returnToLoginActivity(activity: Activity?, stillRequired: () -> Boolean = { true }): () -> Unit {
    if (activity == null || activity.isFinishing || activity.isDestroyed) return {}
    val lifecycle = (activity as? LifecycleOwner)?.lifecycle
    val handler = Handler(Looper.getMainLooper())
    var pending = true
    var observer: LifecycleEventObserver? = null
    val returnAction = Runnable {
        if (pending) {
            pending = false
            observer?.let { lifecycle?.removeObserver(it) }
            if (stillRequired()) bringLoginActivityForward(activity)
        }
    }
    val cancel: () -> Unit = {
        pending = false
        handler.removeCallbacks(returnAction)
        observer?.let { lifecycle?.removeObserver(it) }
    }
    if (lifecycle?.currentState == Lifecycle.State.STARTED) {
        observer = LifecycleEventObserver { _, event ->
            when (event) {
                // No intervalo PAUSED → STOPPED, o navegador ainda pode assumir o primeiro plano.
                // O post aguarda o término do callback de parada antes de reutilizar o hospedeiro.
                Lifecycle.Event.ON_STOP -> handler.post(returnAction)

                Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_DESTROY -> cancel()

                else -> Unit
            }
        }
        lifecycle.addObserver(requireNotNull(observer))
    } else {
        returnAction.run()
    }
    return cancel
}

private fun bringLoginActivityForward(activity: Activity) {
    if (activity.isFinishing || activity.isDestroyed) return
    try {
        activity.startActivity(
            Intent(activity, activity.javaClass)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
    } catch (_: SecurityException) {
        // Uma restrição do sistema mantém o estado sem conexão para a próxima retomada do mesmo hospedeiro.
    }
}
