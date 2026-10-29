package br.gov.caixa.loterias.apostas.runtime.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class LauncherIconScheduleReceiver(private val refresh: suspend (Context) -> Unit, private val dispatcher: CoroutineDispatcher) :
    BroadcastReceiver() {
    // Mantém a atualização nativa na mesma thread da inicialização e do retorno ao app.
    constructor() : this({ LauncherIconScheduleRuntime(it).refresh() }, Dispatchers.Main.immediate)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in SUPPORTED_ACTIONS) return
        val pending = goAsync()
        CoroutineScope(dispatcher).launch {
            try {
                refresh(context.applicationContext)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: IOException) {
                logFailure(failure)
            } catch (failure: IllegalArgumentException) {
                logFailure(failure)
            } catch (failure: IllegalStateException) {
                logFailure(failure)
            } catch (failure: SecurityException) {
                logFailure(failure)
            } finally {
                pending.finish()
            }
        }
    }

    private fun logFailure(failure: Exception) {
        Log.w("LauncherIconCalendar", "Não foi possível atualizar o calendário do ícone", failure)
    }

    companion object {
        const val ACTION_EVALUATE = "br.gov.caixa.loterias.apostas.action.EVALUATE_LAUNCHER_ICON"
        private val SUPPORTED_ACTIONS = setOf(
            ACTION_EVALUATE,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED
        )
    }
}
