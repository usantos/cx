package br.gov.caixa.loterias.apostas.runtime.launcher

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class LauncherIconCalendarObserver(private val scope: CoroutineScope, private val refresh: suspend () -> Unit) :
    DefaultLifecycleObserver {
    private var paused = false

    override fun onPause(owner: LifecycleOwner) {
        paused = true
    }

    override fun onResume(owner: LifecycleOwner) {
        // A primeira seleção já pertence à inicialização; só o retorno exige nova leitura.
        if (!paused) return
        paused = false
        scope.launch {
            try {
                refresh()
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
            }
        }
    }
    private fun logFailure(failure: Exception) {
        Log.w("LauncherIconCalendar", "Não foi possível reavaliar o ícone ao retomar", failure)
    }
}
