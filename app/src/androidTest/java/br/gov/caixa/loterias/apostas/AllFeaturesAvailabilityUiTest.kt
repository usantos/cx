package br.gov.caixa.loterias.apostas

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.apostas.di.FeatureModulesModule
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.presentation.R as PresentationR
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleUiDependencies
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModulesProvider
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntryLoader
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Requer os quinze splits já entregues pelo SDK; não chama renderers nem operações de negócio. */
@RunWith(AndroidJUnit4::class)
class AllFeaturesAvailabilityUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun cadaModuloRespeitaDisponibilidadeRetryEReentradaComClasseReal() {
        val configuration = ControlledAvailability()
        val production = productionDependencies(configuration)
        val loader = FailOnceEntryLoader(production.entries)
        val dependencies = FeatureModuleUiDependencies(production.configuration, production.installation, loader)
        val selected = mutableStateOf<ModuleScenario?>(null)
        assertAllModulesInstalled()
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                FeatureModulesProvider(dependencies) {
                    selected.value?.let { scenario -> ModuleUnderTest(scenario) }
                }
            }
        }
        AppModule.entries.forEach { module ->
            val scenario = ModuleScenario(module)
            compose.runOnIdle { selected.value = scenario }
            assertDisabledAndBack(scenario, loader)
            recoverRealEntry(scenario, configuration, loader)
            toggleAvailableEntry(scenario, configuration, loader)
            compose.runOnIdle { selected.value = null }
            compose.runOnIdle { assertTrue("O owner anterior deve encerrar: ${module.key}", scenario.owner.closed) }
        }
    }

    private fun productionDependencies(configuration: ControlledAvailability): FeatureModuleUiDependencies {
        val repository = FeatureModulesModule.installationRepository(compose.activity.applicationContext)
        val installation = FeatureModulesModule.installation(repository)
        return FeatureModulesModule.featureModules(installation, AppModulesConfigurationUseCase(configuration))
    }

    private fun assertAllModulesInstalled() {
        val installed = SplitInstallManagerFactory.create(compose.activity.applicationContext).installedModules
        assertTrue(
            "Execute após a entrega dos quinze splits pelo SDK",
            installed.containsAll(AppModule.entries.map { it.splitName })
        )
    }

    private fun assertDisabledAndBack(scenario: ModuleScenario, loader: FailOnceEntryLoader) {
        waitForMessage(PresentationR.string.module_delivery_disabled)
        compose.onNodeWithTag(ENTRY_MARKER).assertDoesNotExist()
        compose.onNodeWithText(text(PresentationR.string.module_delivery_back)).performClick()
        compose.runOnIdle {
            assertEquals(scenario.module.key, 1, scenario.backs)
            assertEquals(scenario.module.key, 0, loader.attempts(scenario.module))
        }
    }

    private fun recoverRealEntry(scenario: ModuleScenario, configuration: ControlledAvailability, loader: FailOnceEntryLoader) {
        compose.runOnIdle { configuration.enable(scenario.module) }
        waitForMessage(PresentationR.string.module_delivery_failed)
        compose.onNodeWithTag(ENTRY_MARKER).assertDoesNotExist()
        compose.runOnIdle { assertEquals(scenario.module.key, 1, loader.attempts(scenario.module)) }
        compose.onNodeWithText(text(PresentationR.string.module_delivery_retry)).performClick()
        assertRealEntry(scenario.module)
        compose.runOnIdle { assertEquals(listOf(scenario.module.entryClassName), loader.resolved(scenario.module)) }
    }

    private fun toggleAvailableEntry(scenario: ModuleScenario, configuration: ControlledAvailability, loader: FailOnceEntryLoader) {
        compose.runOnIdle { configuration.disable() }
        waitForMessage(PresentationR.string.module_delivery_disabled)
        compose.onNodeWithTag(ENTRY_MARKER).assertDoesNotExist()
        compose.runOnIdle { configuration.enable(scenario.module) }
        assertRealEntry(scenario.module)
        compose.runOnIdle {
            assertEquals(scenario.module.key, EXPECTED_ATTEMPTS, loader.attempts(scenario.module))
            assertEquals(List(EXPECTED_RESOLUTIONS) { scenario.module.entryClassName }, loader.resolved(scenario.module))
            assertEquals(scenario.module.key, 1, scenario.backs)
            configuration.disable()
        }
        waitForMessage(PresentationR.string.module_delivery_disabled)
    }

    private fun assertRealEntry(module: AppModule) {
        compose.waitUntil(timeoutMillis = DELIVERY_TIMEOUT_MILLIS) {
            compose.onAllNodesWithTag(ENTRY_MARKER).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag(ENTRY_MARKER).assertIsDisplayed()
        compose.onNodeWithText(module.entryClassName).assertIsDisplayed()
    }

    private fun waitForMessage(resource: Int) {
        val message = text(resource)
        compose.waitUntil(timeoutMillis = DELIVERY_TIMEOUT_MILLIS) {
            compose.onAllNodesWithTag(EMPTY_STATE_TAG).fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodesWithTag(ENTRY_MARKER).fetchSemanticsNodes().isEmpty() &&
                compose.onAllNodesWithText(message).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag(EMPTY_STATE_TAG).assertIsDisplayed()
        compose.onNodeWithText(message).assertIsDisplayed()
    }

    private fun text(resource: Int): String = compose.activity.getString(resource)
}

@Composable
private fun ModuleUnderTest(scenario: ModuleScenario) {
    key(scenario.module) {
        DisposableEffect(scenario.owner) { onDispose { scenario.owner.close() } }
        CompositionLocalProvider(LocalViewModelStoreOwner provides scenario.owner) {
            FeatureModuleContent(
                module = scenario.module,
                entryType = FeatureUiEntry::class.java,
                onBack = { scenario.backs++ }
            ) { entry -> Text(entry.javaClass.name, Modifier.testTag(ENTRY_MARKER)) }
        }
    }
}

private class ModuleScenario(val module: AppModule) {
    val owner = ModuleOwner()
    var backs = 0
}

private class ModuleOwner : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
    var closed = false
        private set
    fun close() {
        viewModelStore.clear()
        closed = true
    }
}

private class ControlledAvailability : AppModulesConfigurationRepository {
    override val configuration = MutableStateFlow(AppModulesConfiguration(emptySet()))
    override suspend fun refresh() = Unit
    fun enable(module: AppModule) {
        configuration.value = AppModulesConfiguration(setOf(module))
    }
    fun disable() {
        configuration.value = AppModulesConfiguration(emptySet())
    }
}

private class FailOnceEntryLoader(private val real: FeatureUiEntryLoader) : FeatureUiEntryLoader {
    private val pendingFailures = AppModule.entries.toMutableSet()
    private val attemptCounts = mutableMapOf<AppModule, Int>()
    private val resolvedClasses = mutableMapOf<AppModule, MutableList<String>>()
    override fun <T : FeatureUiEntry> load(module: AppModule, type: Class<T>, context: Context): T? {
        attemptCounts[module] = attempts(module) + 1
        if (pendingFailures.remove(module)) return null
        return real.load(module, type, context)?.also { entry ->
            resolvedClasses.getOrPut(module) { mutableListOf() }.add(entry.javaClass.name)
        }
    }
    fun attempts(module: AppModule): Int = attemptCounts[module] ?: 0
    fun resolved(module: AppModule): List<String> = resolvedClasses[module].orEmpty()
}

private const val ENTRY_MARKER = "all-features-real-entry"
private const val EMPTY_STATE_TAG = "simlo.modules.0001"
private const val EXPECTED_ATTEMPTS = 3
private const val EXPECTED_RESOLUTIONS = 2
private const val DELIVERY_TIMEOUT_MILLIS = 15_000L
