package br.gov.caixa.loterias.core.testing.delivery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleUiDependencies
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModulesProvider
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntryLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/** Fixture de módulos já instalados: resolve e executa os renderizadores reais de cada split. */
fun ComposeContentTestRule.setFeatureContent(content: @Composable () -> Unit) {
    setContent { InstalledFeatureTestContent(content) }
}

@Composable
fun InstalledFeatureTestContent(content: @Composable () -> Unit) {
    val dependencies = remember {
        val configuration = object : AppModulesConfigurationRepository {
            override val configuration = MutableStateFlow(AppModulesConfiguration())
            override suspend fun refresh() = Unit
        }
        FeatureModuleUiDependencies(
            AppModulesConfigurationUseCase(configuration),
            InstallAppModuleUseCase(object : ModuleInstallationRepository {
                override fun install(module: AppModule) = flowOf(ModuleDeliveryState.Ready)
            }),
            object : FeatureUiEntryLoader {
                override fun <T : FeatureUiEntry> load(module: AppModule, type: Class<T>, context: android.content.Context): T =
                    Class.forName(module.entryClassName, true, context.classLoader).asSubclass(type).getDeclaredConstructor().newInstance()
            }
        )
    }
    FeatureModulesProvider(dependencies, content)
}
