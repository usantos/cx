package br.gov.caixa.loterias.core.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberLoggedDetailContext(owner: String?): LoggedDetailContext {
    // Só owner, tipo e ID da recuperação entram no Bundle; os demais detalhes permanecem em memória.
    val context = rememberSaveable(saver = PurchaseRecoverySaver) { LoggedDetailContext(owner) }
    context.reconcile(owner)
    return context
}
