package br.gov.caixa.loterias.core.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberLoggedDetailContext(owner: String?): LoggedDetailContext {
    // Detalhes privados permanecem em memória; seleções brutas e códigos Pix não devem entrar no SavedState.
    val context = remember { LoggedDetailContext(owner) }
    context.reconcile(owner)
    return context
}
