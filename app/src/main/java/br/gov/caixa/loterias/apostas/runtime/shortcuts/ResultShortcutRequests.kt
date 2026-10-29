package br.gov.caixa.loterias.apostas.runtime.shortcuts

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ResultShortcutRequest(val destination: ResultShortcutDestination, val coldStart: Boolean)

internal class ResultShortcutRequests(private val onConsumed: (ResultShortcutDestination) -> Unit = {}) {
    var pending: ResultShortcutRequest? by mutableStateOf(null)
        private set

    fun restore(intent: Intent, savedState: Bundle?) {
        pending = if (savedState == null) {
            resultShortcutDestination(intent)?.let { ResultShortcutRequest(it, coldStart = true) }
        } else {
            ResultShortcutDestination.entries.firstOrNull { it.id == savedState.getString(PENDING_ID) }
                ?.let { ResultShortcutRequest(it, savedState.getBoolean(PENDING_COLD)) }
        }
    }

    fun receive(intent: Intent) {
        resultShortcutDestination(intent)?.let { pending = ResultShortcutRequest(it, coldStart = pending?.coldStart == true) }
    }

    fun save(outState: Bundle) {
        pending?.let {
            outState.putString(PENDING_ID, it.destination.id)
            outState.putBoolean(PENDING_COLD, it.coldStart)
        }
    }

    fun consume(request: ResultShortcutRequest) {
        if (pending === request) {
            pending = null
            onConsumed(request.destination)
        }
    }
}

private const val PENDING_ID = "pending_result_shortcut"
private const val PENDING_COLD = "pending_result_shortcut_cold"
