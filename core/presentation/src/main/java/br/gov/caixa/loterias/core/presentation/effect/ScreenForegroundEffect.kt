package br.gov.caixa.loterias.core.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/** Sinaliza visibilidade sem executar consultas ou conhecer regras da funcionalidade. */
@Composable
fun ScreenForegroundEffect(onActiveChanged: (Boolean) -> Unit) {
    val owner = LocalLifecycleOwner.current
    val callback = rememberUpdatedState(onActiveChanged)
    DisposableEffect(owner) {
        fun notifyState() = callback.value(owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        val observer = LifecycleEventObserver { _, _ -> notifyState() }
        owner.lifecycle.addObserver(observer)
        notifyState()
        onDispose {
            owner.lifecycle.removeObserver(observer)
            callback.value(false)
        }
    }
}
