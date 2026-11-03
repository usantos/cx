package br.gov.caixa.loterias.core.presentation.delivery

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [EMBEDDED_TEST_SDK], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class FeatureModuleEmbeddedTest {
    @get:Rule val compose = createComposeRule()

    @Test fun embeddedStateShowsOnlyOneCapabilityTitle() {
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModuleStateScreen(AppModule.CART, ModuleDeliveryState.Disabled, {}, {}, embedded = true)
            }
        }
        compose.onAllNodesWithText("Carrinho").assertCountEquals(1)
        compose.onNodeWithText("Este conteúdo não está disponível no momento.").assertIsDisplayed()
    }

    @Test fun embeddedContentKeepsTheHostsHeaderAndOneEmptyStateTitle() {
        val dependencies = dependencies(enabled = false)
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies) {
                    LoteriasCaixaDSScaffold(title = "Hospedeiro", onBack = {}) { padding ->
                        FeatureModuleContent(
                            module = AppModule.CART,
                            entryType = SlotEntry::class.java,
                            embedded = true,
                            modifier = Modifier.padding(padding)
                        ) { it.Content() }
                    }
                }
            }
        }
        compose.onAllNodesWithText("Carrinho").assertCountEquals(1)
        compose.onNodeWithText("Hospedeiro").assertIsDisplayed()
    }

    @Test fun fallbackModifierControlsTheEmbeddedSlotWidth() {
        val dependencies = dependencies(enabled = false)
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies) {
                    FeatureModuleContent(
                        module = AppModule.CART,
                        entryType = SlotEntry::class.java,
                        embedded = true,
                        modifier = Modifier.width(REQUESTED_SLOT_WIDTH)
                    ) { it.Content() }
                }
            }
        }
        compose.onNodeWithTag("simlo.modules.0001").assertWidthIsEqualTo(REQUESTED_SLOT_WIDTH)
    }

    @Test fun installedContentKeepsItsOwnWidthWhenAFallbackModifierWasProvided() {
        val dependencies = dependencies(enabled = true)
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModulesProvider(dependencies) {
                    FeatureModuleContent(
                        module = AppModule.CART,
                        entryType = SlotEntry::class.java,
                        embedded = true,
                        modifier = Modifier.width(REQUESTED_SLOT_WIDTH)
                    ) { it.Content() }
                }
            }
        }
        compose.onNodeWithTag("installed-contract").assertWidthIsEqualTo(INSTALLED_SLOT_WIDTH)
        compose.onNodeWithText("Conteúdo instalado").assertIsDisplayed()
    }

    @Test fun omittedContentBackReachesTheAndroidXDispatcher() {
        val owner = BackOwner()
        var backs = 0
        owner.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backs++
            }
        })
        val dependencies = dependencies(enabled = false)
        compose.setContent {
            CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides owner) {
                LoteriasCaixaDSTheme {
                    FeatureModulesProvider(dependencies) {
                        FeatureModuleContent(AppModule.CART, SlotEntry::class.java) { it.Content() }
                    }
                }
            }
        }
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun omittedAdmissionBackReachesTheAndroidXDispatcher() {
        val owner = BackOwner()
        var backs = 0
        owner.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backs++
            }
        })
        val dependencies = dependencies(enabled = false)
        compose.setContent {
            CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides owner) {
                LoteriasCaixaDSTheme {
                    FeatureModulesProvider(dependencies) {
                        FeatureModuleAdmission(AppModule.CART) { error("A entrada opcional está desabilitada") }
                    }
                }
            }
        }
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun explicitContentBackTakesPrecedenceOverTheDispatcher() {
        val owner = BackOwner()
        var dispatched = 0
        var explicit = 0
        owner.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dispatched++
            }
        })
        val dependencies = dependencies(enabled = false)
        compose.setContent {
            CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides owner) {
                LoteriasCaixaDSTheme {
                    FeatureModulesProvider(dependencies) {
                        FeatureModuleContent(AppModule.CART, SlotEntry::class.java, onBack = { explicit++ }) { it.Content() }
                    }
                }
            }
        }
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, explicit)
            assertEquals(0, dispatched)
        }
    }

    private interface SlotEntry : FeatureUiEntry {
        @Composable
        fun Content()
    }

    private class InstalledEntry : SlotEntry {
        @Composable
        override fun Content() {
            Text("Conteúdo instalado", Modifier.width(INSTALLED_SLOT_WIDTH).testTag("installed-contract"))
        }
    }

    private class BackOwner : OnBackPressedDispatcherOwner {
        override val lifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }
        override val onBackPressedDispatcher = OnBackPressedDispatcher()
    }

    private fun dependencies(enabled: Boolean): FeatureModuleUiDependencies {
        val configuration = object : AppModulesConfigurationRepository {
            override val configuration = MutableStateFlow(AppModulesConfiguration(if (enabled) setOf(AppModule.CART) else emptySet()))
            override suspend fun refresh() = Unit
        }
        return FeatureModuleUiDependencies(
            AppModulesConfigurationUseCase(configuration),
            InstallAppModuleUseCase(object : ModuleInstallationRepository {
                override fun install(module: AppModule) = flowOf(ModuleDeliveryState.Ready)
            }),
            object : FeatureUiEntryLoader {
                override fun <T : FeatureUiEntry> load(module: AppModule, type: Class<T>, context: Context): T = type.cast(InstalledEntry())
            }
        )
    }
}

private const val EMBEDDED_TEST_SDK = 35
private val REQUESTED_SLOT_WIDTH = 211.dp
private val INSTALLED_SLOT_WIDTH = 240.dp
