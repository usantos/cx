package br.gov.caixa.loterias.core.presentation.delivery

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/** Preserva o retorno explícito do coordenador; quando omitido, usa a navegação AndroidX do hospedeiro. */
@Composable
internal fun rememberFeatureModuleBackAction(onBack: (() -> Unit)?): () -> Unit {
    if (onBack != null) return onBack
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    return remember(dispatcher) { { dispatcher?.onBackPressed() } }
}
