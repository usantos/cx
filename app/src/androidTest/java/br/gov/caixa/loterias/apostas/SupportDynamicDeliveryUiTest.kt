package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.support.di.createSupportModuleDelivery
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.navigation.SupportModuleNavigationDependencies
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntry
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntryLoader
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportModuleGate
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import com.google.android.gms.tasks.Tasks
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Entrega pelo SDK real em bundletool local-testing; a configuração e o conteúdo são controlados somente neste teste. */
class SupportDynamicDeliveryUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun instalaFaqSobDemandaEDesabilitaConteudoSemRemoverOSplit() {
        val context = compose.activity.applicationContext
        val manager = SplitInstallManagerFactory.create(context)
        assertFalse("O cenário exige instalar inicialmente somente a base, sem o módulo FAQ", FAQ_MODULE in manager.installedModules)
        val configuration = ControlledModulesConfiguration()
        val state = mutableStateOf(SupportUiState(sections = listOf(SupportSection(SECTION_ID, SECTION_TITLE, emptyList()))))
        val actions = mutableListOf<SupportAction>()
        val resolvedClasses = mutableListOf<String>()
        var backs = 0
        val dependencies = object : SupportModuleNavigationDependencies {
            override val modules = AppModulesConfigurationUseCase(configuration)
            override val delivery = createSupportModuleDelivery(context)
            override val entryLoader = SupportFaqEntryLoader {
                SplitCompat.installActivity(compose.activity)
                Class.forName(FAQ_ENTRY_CLASS, true, compose.activity.classLoader)
                    .asSubclass(SupportFaqEntry::class.java).getDeclaredConstructor().newInstance()
                    .also { resolvedClasses += it.javaClass.name }
            }
        }
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                SupportModuleGate(dependencies, onBack = { backs++ }) { entry ->
                    SupportScreen(
                        state = state.value,
                        onAction = { action ->
                            actions += action
                            when (action) {
                                is SupportAction.OpenSection -> state.value = state.value.copy(
                                    selectedSectionId = action.id,
                                    selectedSectionTitle = action.title,
                                    questions = listOf(SupportQuestion(QUESTION_ID, QUESTION_TITLE, QUESTION_ANSWER))
                                )

                                is SupportAction.ToggleQuestion -> state.value = state.value.copy(expandedQuestionIds = setOf(action.id))

                                else -> error("Ação inesperada no conteúdo local: $action")
                            }
                        },
                        faqContent = { currentState, onAction -> entry.Content(currentState, onAction) }
                    )
                }
            }
        }
        compose.onNodeWithText(DISABLED_MESSAGE).assertIsDisplayed()
        compose.onNodeWithTag("simlo.help.0050").assertIsDisplayed()
        compose.runOnIdle { assertTrue("Desabilitado não resolve a classe opcional", resolvedClasses.isEmpty()) }
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, backs)
            configuration.enable(true)
        }
        compose.waitUntil(timeoutMillis = INSTALL_TIMEOUT_MILLIS) {
            compose.onAllNodesWithTag("simlo.help.0026").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("simlo.help.0026").assertIsDisplayed()
        compose.onNodeWithText(SECTION_TITLE).performClick()
        compose.onNodeWithTag("simlo.help.0029").assertIsDisplayed()
        compose.onNodeWithText(QUESTION_TITLE).assertIsDisplayed().performClick()
        compose.onNodeWithTag("simlo.help.0018").assertIsDisplayed()
        compose.onNodeWithText(QUESTION_ANSWER).assertIsDisplayed()
        assertTrue("O SDK deve confirmar o módulo FAQ instalado", FAQ_MODULE in manager.installedModules)
        val installedSessions = faqSessions(manager)
        compose.runOnIdle {
            assertEquals(listOf(SupportAction.OpenSection(SECTION_ID, SECTION_TITLE), SupportAction.ToggleQuestion(QUESTION_ID)), actions)
            assertEquals(listOf(FAQ_ENTRY_CLASS), resolvedClasses)
            configuration.enable(false)
        }
        compose.onNodeWithText(DISABLED_MESSAGE).assertIsDisplayed()
        compose.onNodeWithTag("simlo.help.0018").assertDoesNotExist()
        assertTrue("Desabilitar disponibilidade mantém o código instalado", FAQ_MODULE in manager.installedModules)
        compose.runOnIdle { configuration.enable(true) }
        compose.waitUntil(timeoutMillis = INSTALL_TIMEOUT_MILLIS) {
            compose.onAllNodesWithTag("simlo.help.0018").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(QUESTION_ANSWER).assertIsDisplayed()
        assertEquals("Reentrada consulta o split instalado sem criar outra sessão", installedSessions, faqSessions(manager))
        compose.runOnIdle {
            assertEquals(listOf(FAQ_ENTRY_CLASS, FAQ_ENTRY_CLASS), resolvedClasses)
            assertEquals(1, backs)
        }
    }

    private fun faqSessions(manager: SplitInstallManager): Set<Int> =
        Tasks.await(manager.sessionStates, SDK_TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .filter { FAQ_MODULE in it.moduleNames() }.map { it.sessionId() }.toSet()

    private class ControlledModulesConfiguration : AppModulesConfigurationRepository {
        override val configuration = MutableStateFlow(AppModulesConfiguration())
        override suspend fun refresh() = Unit
        fun enable(enabled: Boolean) {
            configuration.value = AppModulesConfiguration(if (enabled) setOf(AppModule.SUPPORT_FAQ) else emptySet())
        }
    }
}

private const val FAQ_MODULE = "faq"
private const val FAQ_ENTRY_CLASS = "br.gov.caixa.loterias.feature.support.faq.SupportFaqEntryImpl"
private const val SECTION_ID = 1L
private const val SECTION_TITLE = "Entrega local do FAQ"
private const val QUESTION_ID = 7L
private const val QUESTION_TITLE = "O módulo foi instalado?"
private const val QUESTION_ANSWER = "Conteúdo do split FAQ instalado pelo SDK em teste local."
private const val DISABLED_MESSAGE = "Este módulo não está disponível no momento."
private const val INSTALL_TIMEOUT_MILLIS = 60_000L
private const val SDK_TASK_TIMEOUT_SECONDS = 10L
