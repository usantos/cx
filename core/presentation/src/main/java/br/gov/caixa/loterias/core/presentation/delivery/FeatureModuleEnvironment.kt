package br.gov.caixa.loterias.core.presentation.delivery

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase

interface FeatureUiEntryLoader {
    fun <T : FeatureUiEntry> load(module: AppModule, type: Class<T>, context: Context): T?
}

class FeatureModuleUiDependencies(
    val configuration: AppModulesConfigurationUseCase,
    val installation: InstallAppModuleUseCase,
    val entries: FeatureUiEntryLoader,
)

val LocalFeatureModules = staticCompositionLocalOf<FeatureModuleUiDependencies> {
    error("Ambiente de entrega de funcionalidades não fornecido")
}

@Composable
fun featureModuleEnabled(module: AppModule): Boolean =
    LocalFeatureModules.current.configuration.configuration.collectAsStateWithLifecycle().value.isEnabled(module)

/** A escrita consulta o valor atual no início do efeito, sem capturar um booleano antigo da tela. */
@Composable
fun rememberFeatureWritePermission(module: AppModule): () -> Boolean {
    val configuration = LocalFeatureModules.current.configuration
    return remember(configuration, module) { { configuration.isEnabled(module) } }
}
