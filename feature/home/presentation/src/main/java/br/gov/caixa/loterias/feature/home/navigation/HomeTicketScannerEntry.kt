package br.gov.caixa.loterias.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/** Tickets controla a permissão de câmera e oferece entrada manual quando o acesso à câmera está indisponível. */
@Composable
internal fun rememberHomeTicketScannerEntry(onOpen: () -> Unit): () -> Unit {
    val currentOpen = rememberUpdatedState(onOpen)
    return remember { { currentOpen.value() } }
}
