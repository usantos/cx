package br.gov.caixa.loterias.core.presentation.delivery

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [MODULE_PRESENTATION_TEST_SDK], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class FeatureModulePresentationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun disabledAdmissionDoesNotCreateOwnerInstallOrResolveEntryAndKeepsBackAvailable() {
        val dependencies = Dependencies(enabled = false)
        val owners = mutableListOf<JourneyOwner>()
        var backs = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies.value) {
                    Journey(dependencies.module, owners, preserveActive = false, onBack = { backs++ })
                }
            }
        }
        compose.onNodeWithText(DISABLED_MESSAGE).assertIsDisplayed()
        compose.onNodeWithText("UI instalada: Inicial").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(0, owners.size)
            assertEquals(emptyList<AppModule>(), dependencies.installations)
            assertEquals(emptyList<Pair<AppModule, Class<*>>>(), dependencies.resolutions)
        }
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun enablingAnInstalledModuleCreatesOwnerAndLoadsTheRequestedTypedEntry() {
        val dependencies = Dependencies(enabled = false)
        val owners = mutableListOf<JourneyOwner>()
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies.value) {
                    Journey(dependencies.module, owners, preserveActive = false, onBack = {})
                }
            }
        }
        compose.onNodeWithText(DISABLED_MESSAGE).assertIsDisplayed()
        compose.runOnIdle { dependencies.enable(true) }
        compose.onNodeWithText("UI instalada: Inicial").assertIsDisplayed()
        compose.onNodeWithText("Editar rascunho").performClick()
        compose.onNodeWithText("UI instalada: Editado").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, owners.size)
            assertEquals(listOf(dependencies.module), dependencies.installations)
            assertEquals(listOf(dependencies.module to TestEntry::class.java), dependencies.resolutions)
            assertEquals("Editado", owners.single().draft)
        }
    }

    @Test fun anAdmittedJourneyPreservesItsOwnerDraftAndBackAfterDisabling() {
        val dependencies = Dependencies(enabled = true)
        val owners = mutableListOf<JourneyOwner>()
        val inJourney = mutableStateOf(true)
        var backs = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies.value) {
                    if (inJourney.value) {
                        Journey(dependencies.module, owners, preserveActive = true, onBack = {
                            backs++
                            inJourney.value = false
                        })
                    } else {
                        Text("Destino anterior")
                    }
                }
            }
        }
        compose.onNodeWithText("Editar rascunho").performClick()
        compose.onNodeWithText("UI instalada: Editado").assertIsDisplayed()
        compose.runOnIdle { dependencies.enable(false) }
        compose.onNodeWithText("UI instalada: Editado").assertIsDisplayed()
        compose.onNodeWithText(DISABLED_MESSAGE).assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, owners.size)
            assertEquals("Editado", owners.single().draft)
            assertEquals(listOf(dependencies.module), dependencies.installations)
            assertEquals(1, dependencies.resolutions.size)
        }
        compose.onNodeWithText("Retornar da jornada").performClick()
        compose.onNodeWithText("Destino anterior").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun disabledContinuationUsesTheRecoveredOwnersAccessAndKeepsItsBackNavigation() {
        val dependencies = Dependencies(enabled = false)
        val inJourney = mutableStateOf(true)
        var backs = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies.value) {
                    // O contrato recebe a concessão local do proprietário de recuperação, não da configuração remota.
                    val owner = viewModel(key = "recovered-owner") { JourneyOwner(FeatureEntryAccess.CONTINUATION) }
                    val back: () -> Unit = {
                        backs++
                        inJourney.value = false
                    }
                    if (inJourney.value) {
                        FeatureModuleAdmission(dependencies.module, back, access = owner.access) {
                            FeatureModuleContent(
                                dependencies.module,
                                TestEntry::class.java,
                                onBack = back,
                                access = owner.access
                            ) { entry ->
                                entry.Content(owner.draft, owner::edit, back)
                            }
                        }
                    } else {
                        Text("Destino anterior")
                    }
                }
            }
        }
        compose.onNodeWithText("UI instalada: Inicial").assertIsDisplayed()
        compose.onNodeWithText(DISABLED_MESSAGE).assertDoesNotExist()
        compose.onNodeWithText("Retornar da jornada").performClick()
        compose.onNodeWithText("Destino anterior").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, backs)
            assertEquals(listOf(dependencies.module), dependencies.installations)
            assertEquals(listOf(dependencies.module to TestEntry::class.java), dependencies.resolutions)
        }
    }

    @Test fun missingInstalledEntryShowsFailureAndExplicitRetryResolvesTheRealTypedContract() {
        val dependencies = Dependencies(enabled = true).apply { entry = null }
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies.value) {
                    FeatureModuleContent(dependencies.module, TestEntry::class.java) { entry ->
                        entry.Content("Recuperado", {}, {})
                    }
                }
            }
        }
        compose.onNodeWithText(FAILED_MESSAGE).assertIsDisplayed()
        compose.onNodeWithText("UI instalada: Recuperado").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, dependencies.installations.size)
            assertEquals(1, dependencies.resolutions.size)
            dependencies.entry = InstalledEntry()
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("UI instalada: Recuperado").assertIsDisplayed()
        compose.onNodeWithText(FAILED_MESSAGE).assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, dependencies.installations.size)
            assertEquals(2, dependencies.resolutions.size)
        }
    }

    @Composable
    private fun Journey(module: AppModule, owners: MutableList<JourneyOwner>, preserveActive: Boolean, onBack: () -> Unit) {
        FeatureModuleAdmission(module, onBack) {
            val owner = viewModel(key = "journey-owner") { JourneyOwner().also(owners::add) }
            FeatureModuleContent(module, TestEntry::class.java, onBack = onBack, preserveActive = preserveActive) { entry ->
                entry.Content(owner.draft, owner::edit, onBack)
            }
        }
    }

    private class JourneyOwner(val access: FeatureEntryAccess = FeatureEntryAccess.OPTIONAL) : ViewModel() {
        var draft by mutableStateOf("Inicial")
            private set
        fun edit() {
            draft = "Editado"
        }
    }

    private interface TestEntry : FeatureUiEntry {
        @Composable
        fun Content(draft: String, onEdit: () -> Unit, onBack: () -> Unit)
    }

    private class InstalledEntry : TestEntry {
        @Composable
        override fun Content(draft: String, onEdit: () -> Unit, onBack: () -> Unit) {
            Column {
                Text("UI instalada: $draft")
                Button(onClick = onEdit) { Text("Editar rascunho") }
                Button(onClick = onBack) { Text("Retornar da jornada") }
            }
        }
    }

    private class Dependencies(enabled: Boolean) {
        val module = AppModule.RESULTS
        private val configuration = object : AppModulesConfigurationRepository {
            override val configuration = MutableStateFlow(AppModulesConfiguration(if (enabled) setOf(module) else emptySet()))
            override suspend fun refresh() = Unit
        }
        val installations = mutableListOf<AppModule>()
        val resolutions = mutableListOf<Pair<AppModule, Class<*>>>()
        var entry: TestEntry? = InstalledEntry()
        private val installed = MutableStateFlow<ModuleDeliveryState>(ModuleDeliveryState.Ready)
        val value = FeatureModuleUiDependencies(
            AppModulesConfigurationUseCase(configuration),
            InstallAppModuleUseCase(object : ModuleInstallationRepository {
                override fun install(module: AppModule) = installed.also { installations += module }
            }),
            object : FeatureUiEntryLoader {
                override fun <T : FeatureUiEntry> load(module: AppModule, type: Class<T>, context: Context): T? {
                    resolutions += module to type
                    return entry?.let(type::cast)
                }
            }
        )

        fun enable(enabled: Boolean) {
            configuration.configuration.value = AppModulesConfiguration(if (enabled) setOf(module) else emptySet())
        }
    }
}

private const val MODULE_PRESENTATION_TEST_SDK = 35
private const val DISABLED_MESSAGE = "Este conteúdo não está disponível no momento."
private const val FAILED_MESSAGE = "Não foi possível carregar o conteúdo. Tente novamente."
