package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.flow.first

@Composable
internal fun HomeShakeDrawerEffect(pending: Boolean, homeReady: Boolean, openDrawer: suspend () -> Unit) {
    val ready by rememberUpdatedState(homeReady)
    val latestOpenDrawer by rememberUpdatedState(openDrawer)
    var opened by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(pending) {
        if (!pending) {
            opened = false
        } else if (!opened) {
            // Continua aguardando durante carregamento e repetição sem descartar a dica pendente.
            // Observa a prontidão sem cancelar a animação do menu lateral em atualizações posteriores.
            do {
                snapshotFlow { ready }.first { it }
                withFrameNanos { }
            } while (!ready)
            opened = true
            latestOpenDrawer()
        }
    }
}
