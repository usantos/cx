package br.gov.caixa.loterias.core.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState

/** Uma flag não remove o proprietário de uma jornada já admitida. */
@Composable
fun FeatureModuleAdmission(
    module: AppModule,
    onBack: (() -> Unit)? = null,
    access: FeatureEntryAccess = FeatureEntryAccess.OPTIONAL,
    content: @Composable () -> Unit,
) {
    val backAction = rememberFeatureModuleBackAction(onBack)
    val enabled = featureModuleEnabled(module)
    var admitted by rememberSaveable(module) { mutableStateOf(enabled || access != FeatureEntryAccess.OPTIONAL) }
    if (enabled || access != FeatureEntryAccess.OPTIONAL) admitted = true
    if (admitted) {
        content()
    } else {
        FeatureModuleStateScreen(module, ModuleDeliveryState.Disabled, backAction, {})
    }
}
