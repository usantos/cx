// Literais numéricos descrevem geometria fixa dos testes, IDs dos dados de teste, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.support

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase
import br.gov.caixa.loterias.feature.support.navigation.SupportNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.support.navigation.supportGraph
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SupportNavigationJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<androidx.activity.ComponentActivity>()
    private lateinit var nav: NavHostController
    private var accepts = 0
    private var rechecks = 0
    private val sectionRequests = mutableListOf<Long>()
    private var termsRequests = 0
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
    fun readOnlyTermsTabsAndRequiredAcceptanceHaveDifferentCompletionBehavior() {
        show()
        compose.runOnIdle { nav.navigate("support/terms-view") }
        compose.onNodeWithTag("support_terms_document").assertIsDisplayed()
        compose.onNodeWithText("Termo de uso").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { nav.navigate("support/terms?tab=FAQ") }
        compose.onNodeWithText("Apostas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { nav.navigate("support/terms") }
        compose.onNodeWithText("Termo de uso").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { nav.navigate("support/terms-acceptance") }
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Aceitar Termos de Uso").performClick()
        compose.onNodeWithText("Continuar").performClick()
        compose.runOnIdle {
            assertEquals(1, accepts)
            assertEquals(1, rechecks)
            assertEquals(3, termsRequests)
        }
    }

    @Test
    fun gameHelpGraphPreservesRequestedModalityAndBackAcrossLegacyColorVariants() {
        show()
        listOf(14 to "Lotogol", 16 to "Lotomania", 19 to "Loteca", 20 to "Timemania", 8 to "Lotofacil").forEach { (id, name) ->
            compose.runOnIdle { nav.navigate("support/game-help?modalityId=$id&modalityName=$name") }
            compose.onNodeWithText(name.lowercase()).assertIsDisplayed()
            compose.onNodeWithTag("support_game_help").assertIsDisplayed()
            compose.onNodeWithContentDescription("Fechar").performClick()
            compose.onNodeWithText("Página inicial").assertIsDisplayed()
        }
    }

    private fun show() {
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
            }
        )
        compose.setContent {
            nav = rememberNavController()
            CaixaDSTheme {
                NavHost(nav, startDestination = "fixture") {
                    composable("fixture") { Text("Página inicial") }
                    supportGraph(dependencies, nav, session)
                }
            }
        }
    }
}
