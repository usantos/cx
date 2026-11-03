// Literais numéricos descrevem geometria fixa dos testes, IDs dos dados de teste, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.support.faq

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportDeliveryState
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase
import br.gov.caixa.loterias.feature.support.navigation.SupportModuleNavigationDependencies
import br.gov.caixa.loterias.feature.support.navigation.SupportNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.support.navigation.supportGraph
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntryLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@RunWith(DynamicFeatureRobolectricTestRunner::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@Config(sdk = [36], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SupportFaqNavigationJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController
    private var accepts = 0
    private var rechecks = 0
    private val sectionRequests = mutableListOf<Long>()
    private var termsRequests = 0
    private val moduleConfiguration = MutableStateFlow(AppModulesConfiguration(setOf(AppModule.SUPPORT_FAQ)))
    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity: AuthenticatedSessionIdentity? = null
        override val privateEntryKey = "public"
        override val isPrivateSessionCurrent = { false }
        override val onLogout = { error("Support must not log out") }
        override val requestVisitorLogin: (String) -> Unit = { error("Support must not authenticate") }
        override val recheckAccountCompletion = {
            rechecks++
            Unit
        }
        override val onAuthenticated: (String) -> Unit = { error("Support must not publish credentials") }
        override val onTryNow = {}
    }

    @Test
    fun faqGraphOpensAndClosesSectionsWithoutLeavingTheDestination() {
        show()
        compose.runOnIdle { nav.navigate("support") }
        compose.onNodeWithText("Apostas").assertIsDisplayed().performClick()
        compose.onNodeWithText("Como apostar?").assertIsDisplayed().performClick()
        compose.onNodeWithText("Escolha uma modalidade.").assertIsDisplayed()
        compose.onNodeWithText("Como pagar?").assertIsDisplayed().performClick()
        compose.onNodeWithText("Use os meios disponíveis.").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Apostas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Página inicial").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(1L), sectionRequests) }
    }

    @Test
    fun termsFaqTabUsesInstalledEntryAndReturnsToTerms() {
        show()
        compose.runOnIdle { nav.navigate("support/terms?tab=FAQ") }
        compose.onNodeWithText("Apostas").assertIsDisplayed().performClick()
        compose.onNodeWithText("Como apostar?").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Termo de uso").performClick()
        compose.onNodeWithTag("support_html").assertExists()
        compose.runOnIdle { assertEquals(1, termsRequests) }
    }

    @Test
    fun desabilitarFaqAbertoRestauraAbasECabecalhoDosTermos() {
        show()
        compose.runOnIdle { nav.navigate("support/terms") }
        compose.onNodeWithTag("support_html").assertExists()
        compose.onNodeWithText("Dúvidas").performClick()
        compose.onNodeWithText("Apostas").assertIsDisplayed().performClick()
        compose.onNodeWithText("Como apostar?").assertIsDisplayed()
        compose.onNodeWithText("Termo de uso").assertDoesNotExist()
        compose.runOnIdle { moduleConfiguration.value = AppModulesConfiguration() }
        compose.onNodeWithText("Este módulo não está disponível no momento.").assertIsDisplayed()
        compose.onNodeWithText("Como apostar?").assertDoesNotExist()
        compose.onNodeWithText("Loterias Caixa").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Voltar").assertCountEquals(1)
        compose.onNodeWithText("Termo de uso").assertIsDisplayed().performClick()
        compose.onNodeWithTag("support_html").assertExists()
        compose.onNodeWithText("Este módulo não está disponível no momento.").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(0, accepts)
            assertEquals(0, rechecks)
            assertEquals(listOf(1L), sectionRequests)
        }
    }

    private fun show() {
        val moduleDependencies = object : SupportModuleNavigationDependencies {
            override val modules = AppModulesConfigurationUseCase(object : AppModulesConfigurationRepository {
                override val configuration = moduleConfiguration
                override suspend fun refresh() = Unit
            })
            override val delivery = object : SupportModuleDelivery {
                override fun install() = flowOf(SupportDeliveryState.Ready)
            }
            override val entryLoader = SupportFaqEntryLoader { SupportFaqEntryImpl() }
        }
        val dependencies = SupportNavigationDependenciesAdapter(
            AcceptTermsUseCase {
                accepts++
                AppResult.Success(Unit)
            },
            GetBettingParametersUseCase { AppResult.Failure(AppError.Network) },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            GetSupportQuestionsUseCase { id ->
                sectionRequests.add(id)
                AppResult.Success(
                    listOf(
                        SupportQuestion(1, "Como apostar?", "Escolha uma modalidade."),
                        SupportQuestion(2, "Como pagar?", "Use os meios disponíveis.")
                    )
                )
            },
            GetSupportSectionsUseCase {
                AppResult.Success(listOf(SupportSection(1, "Apostas", emptyList()), SupportSection(2, "Pagamentos", emptyList())))
            },
            GetTermsUseCase {
                termsRequests++
                AppResult.Success("<p>Termos oficiais</p>")
            },
            optionalModule = moduleDependencies
        )
        compose.setContent {
            nav = rememberNavController()
            LoteriasCaixaDSTheme {
                NavHost(nav, startDestination = "fixture") {
                    composable("fixture") { Text("Página inicial") }
                    supportGraph(dependencies, nav, session)
                }
            }
        }
    }
}
