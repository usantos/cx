package br.gov.caixa.loterias.core.presentation.delivery

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [MODULE_PROVIDER_TEST_SDK], application = android.app.Application::class)
class FeatureModulesProviderTest {
    @get:Rule val compose = createComposeRule()

    @Test fun providerRefreshesOnlyWhileForegroundAndPermissionReadsTheCurrentConfiguration() {
        val owner = Owner()
        val visible = mutableStateOf(true)
        val revision = mutableStateOf("Primeira revisão")
        val module = AppModule.RESULTS
        var refreshes = 0
        var writes = 0
        var installations = 0
        var resolutions = 0
        val configuration = RefreshingConfiguration(module) { refreshes++ }
        val dependencies = providerDependencies(configuration, { installations++ }, { resolutions++ })
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                if (visible.value) {
                    LoteriasCaixaDSTheme {
                        FeatureModulesProvider(dependencies) {
                            Column {
                                Text(revision.value)
                                Text(if (featureModuleEnabled(module)) "Disponível" else "Indisponível")
                                val mayWrite = rememberFeatureWritePermission(module)
                                Button(onClick = { if (mayWrite()) writes++ }) { Text("Iniciar operação") }
                            }
                        }
                    }
                } else {
                    Text("Provedor removido")
                }
            }
        }
        compose.onNodeWithText("Indisponível").assertIsDisplayed()
        compose.onNodeWithText("Iniciar operação").performClick()
        compose.runOnIdle {
            assertEquals(0, writes)
            assertEquals(0, refreshes)
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
        }
        compose.onNodeWithText("Disponível").assertIsDisplayed()
        compose.onNodeWithText("Iniciar operação").performClick()
        compose.runOnIdle {
            assertEquals(1, writes)
            assertEquals(1, refreshes)
            revision.value = "Segunda revisão"
        }
        compose.onNodeWithText("Segunda revisão").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, refreshes)
            configuration.configuration.value = AppModulesConfiguration(emptySet())
        }
        compose.onNodeWithText("Indisponível").assertIsDisplayed()
        compose.onNodeWithText("Iniciar operação").performClick()
        compose.runOnIdle {
            assertEquals(1, writes)
            owner.lifecycle.currentState = Lifecycle.State.STARTED
        }
        compose.runOnIdle {
            assertEquals(1, refreshes)
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
        }
        compose.onNodeWithText("Disponível").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(2, refreshes)
            visible.value = false
        }
        compose.onNodeWithText("Provedor removido").assertIsDisplayed()
        compose.runOnIdle {
            owner.lifecycle.currentState = Lifecycle.State.CREATED
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
            assertEquals(2, refreshes)
            assertEquals(0, installations)
            assertEquals(0, resolutions)
        }
    }

    private fun providerDependencies(configuration: AppModulesConfigurationRepository, onInstall: () -> Unit, onResolve: () -> Unit) =
        FeatureModuleUiDependencies(
            AppModulesConfigurationUseCase(configuration),
            InstallAppModuleUseCase(object : ModuleInstallationRepository {
                override fun install(module: AppModule): Flow<ModuleDeliveryState> {
                    onInstall()
                    error("O provedor não instala conteúdo antes de uma entrada")
                }
            }),
            object : FeatureUiEntryLoader {
                override fun <T : FeatureUiEntry> load(module: AppModule, type: Class<T>, context: Context): T? {
                    onResolve()
                    return null
                }
            }
        )

    private class RefreshingConfiguration(private val module: AppModule, private val onRefresh: () -> Unit) :
        AppModulesConfigurationRepository {
        override val configuration = MutableStateFlow(AppModulesConfiguration(emptySet()))
        override suspend fun refresh() {
            onRefresh()
            configuration.value = AppModulesConfiguration(setOf(module))
        }
    }

    private class Owner : LifecycleOwner {
        override val lifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.STARTED }
    }
}

private const val MODULE_PROVIDER_TEST_SDK = 35
