package br.gov.caixa.loterias.feature.support.presentation.dynamic

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.feature.support.R
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportDeliveryState
import br.gov.caixa.loterias.feature.support.navigation.SupportModuleNavigationDependencies

@Composable
fun SupportModuleGate(
    dependencies: SupportModuleNavigationDependencies,
    onBack: () -> Unit,
    embedded: Boolean = false,
    onDisabled: () -> Unit = {},
    content: @Composable (SupportFaqEntry) -> Unit,
) {
    val model = viewModel(key = "support-faq-delivery") { SupportModuleViewModel(dependencies.modules, dependencies.delivery) }
    val state by model.state.collectAsStateWithLifecycle()
    val currentOnDisabled by rememberUpdatedState(onDisabled)
    LaunchedEffect(state) {
        if (state == SupportDeliveryState.Disabled) currentOnDisabled()
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    val entry = remember(state, context) {
        if (state == SupportDeliveryState.Ready) dependencies.entryLoader.load(context) else null
    }
    if (state == SupportDeliveryState.Ready && entry != null) {
        content(entry)
    } else {
        SupportModuleStateScreen(state, onBack, model::retry, embedded = embedded)
        if (state == SupportDeliveryState.Ready) LaunchedEffect(model) { model.entryUnavailable() }
    }
    val confirmation = state as? SupportDeliveryState.Confirmation
    if (confirmation != null) {
        val callback = remember(model, confirmation) { model.confirmationHandler(confirmation.sessionId) }
        key(confirmation) { SupportModuleConfirmation(confirmation.sessionId, callback) }
    }
}

@Composable
internal fun SupportModuleStateScreen(state: SupportDeliveryState, onBack: () -> Unit, onRetry: () -> Unit, embedded: Boolean = false) {
    if (embedded) {
        SupportModuleStateContent(state, onBack, onRetry, Modifier)
    } else {
        LoteriasCaixaDSScaffold(title = stringResource(R.string.support_module_title), onBack = onBack) { padding ->
            SupportModuleStateContent(state, onBack, onRetry, Modifier.padding(padding))
        }
    }
}

@Composable
private fun SupportModuleStateContent(state: SupportDeliveryState, onBack: () -> Unit, onRetry: () -> Unit, modifier: Modifier) {
    val disabled = state == SupportDeliveryState.Disabled
    val failed = state == SupportDeliveryState.Failed || state == SupportDeliveryState.Ready
    LoteriasCaixaDSEmptyState(
        title = stringResource(R.string.support_module_title),
        message = stringResource(
            when {
                disabled -> R.string.support_module_disabled
                failed -> R.string.support_module_failed
                else -> R.string.support_module_installing
            }
        ),
        modifier = modifier.uiId("help.0050"),
        actionLabel = stringResource(if (failed) R.string.support_module_retry else R.string.support_module_back),
        onAction = if (failed) onRetry else onBack
    )
}
