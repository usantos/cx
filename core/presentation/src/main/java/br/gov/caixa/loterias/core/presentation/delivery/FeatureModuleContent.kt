package br.gov.caixa.loterias.core.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState

/** Renderiza somente a entrada tipada instalada; os proprietários da jornada continuam na base. */
@Composable
fun <T : FeatureUiEntry> FeatureModuleContent(
    module: AppModule,
    entryType: Class<T>,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    access: FeatureEntryAccess = FeatureEntryAccess.OPTIONAL,
    preserveActive: Boolean = false,
    embedded: Boolean = false,
    content: @Composable (T) -> Unit,
) {
    val backAction = rememberFeatureModuleBackAction(onBack)
    val dependencies = LocalFeatureModules.current
    val model = viewModel(key = "module-${module.key}-$access-$preserveActive") {
        FeatureModuleViewModel(dependencies.configuration, dependencies.installation, module, access, preserveActive)
    }
    val state by model.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val entry = remember(state, context, dependencies, entryType) {
        if (state == ModuleDeliveryState.Ready) dependencies.entries.load(module, entryType, context) else null
    }
    if (state == ModuleDeliveryState.Ready && entry != null) {
        content(entry)
    } else {
        FeatureModuleStateScreen(
            module,
            if (state ==
                ModuleDeliveryState.Ready
            ) {
                ModuleDeliveryState.Failed
            } else {
                state
            },
            backAction,
            model::retry,
            embedded = embedded,
            modifier = modifier
        )
        if (state == ModuleDeliveryState.Ready) LaunchedEffect(model) { model.entryUnavailable() }
    }
    (state as? ModuleDeliveryState.Confirmation)?.let { confirmation ->
        val callback = remember(model, confirmation) { model.confirmationHandler(confirmation.sessionId) }
        key(confirmation) { FeatureModuleConfirmation(confirmation.sessionId, callback) }
    }
}
