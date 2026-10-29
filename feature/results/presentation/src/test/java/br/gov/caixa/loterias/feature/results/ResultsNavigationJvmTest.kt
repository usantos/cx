package br.gov.caixa.loterias.feature.results

import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase
import br.gov.caixa.loterias.feature.results.navigation.ResultsNavigationDependencies
import br.gov.caixa.loterias.feature.results.navigation.resultsGraph
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
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
class ResultsNavigationJvmTest {
    @get:Rule
    val compose = createComposeRule()
    private lateinit var controller: NavHostController
    private val latestRequests = mutableListOf<String>()
    private var confirmedRequests = 0
    private var visualRequests = 0
    private val bet = ConfirmedBet(11, "Mega-Sena", 10, "", "Não premiada", listOf(1, 2))
    private val result =
        DrawResult("Mega-Sena", 10, "22/10/2026", "", listOf(1, 3), emptyList(), emptyList(), false, "", "0", "", emptyList())

    @Test
    fun modalityAndDeepLinkDestinationsCreateTheirOwnQueriesAndReturnToSelection() {
        show("results/modalities")
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
        compose.onNodeWithText("quina").performScrollTo().performClick()
        compose.onNodeWithText("Concurso nº 10").assertIsDisplayed()
        compose.onNodeWithText("Voltar").performClick()
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
        compose.runOnIdle { controller.navigate("results/home/lotofacil") }
        compose.onNodeWithText("Concurso nº 10").assertIsDisplayed()
        compose.runOnIdle { controller.navigate("results/detail") }
        compose.onNodeWithText("Concurso nº 10").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf("quina", "lotofacil", "megasena"), latestRequests) }
    }

    @Test
    fun confirmedBetUsesSelectionAndCompletionNavigatesToTickets() {
        show("results/confirmed-bet", bet)
        compose.onNodeWithText("Resultado da aposta").assertIsDisplayed()
        compose.onNodeWithText("Sua aposta").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Voltar às apostas").performClick()
        compose.onNodeWithText("Minhas apostas").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, confirmedRequests)
            assertEquals(0, visualRequests)
        }
    }

    @Test
    fun missingSelectionRedirectsToTicketsWithoutInventingAConfirmedBet() {
        show("results/confirmed-bet")
        compose.onNodeWithText("Minhas apostas").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, confirmedRequests)
            assertEquals(0, visualRequests)
        }
    }

    @Test
    fun mockBackendExplicitlyUsesVisualBetFixture() {
        show("results/confirmed-bet", mockBackend = true)
        compose.onNodeWithText("Resultado da aposta").assertIsDisplayed()
        compose.onNodeWithText("Sua aposta").performScrollTo().assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, confirmedRequests)
            assertEquals(1, visualRequests)
        }
    }

    private fun show(start: String, selectedBet: ConfirmedBet? = null, mockBackend: Boolean = false) {
        val dependencies = object : ResultsNavigationDependencies {
            override val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
            override val mockBackendEnabled = mockBackend
            override val latestResult =
                GetLatestResultUseCase { modality ->
                    latestRequests.add(modality)
                    // Mantém o repositório assíncrono enquanto os efeitos do grafo/rota são inicializados.
                    kotlinx.coroutines.yield()
                    AppResult.Success(result.copy(modality = modality))
                }
            override val resultByContest =
                GetResultByContestUseCase { modality, contest -> AppResult.Success(result.copy(modality = modality, contest = contest)) }
            override val confirmedBetResults = GetConfirmedBetResultsUseCase { selected ->
                confirmedRequests++
                assertEquals(if (mockBackend) 100 else 11, selected.id.toInt())
                ServiceResult.Success(listOf(result))
            }
            override fun visualConfirmedBet(variant: String, originalRecurringBet: Boolean, redeemedPrize: Boolean): ConfirmedBet {
                visualRequests++
                return bet
            }
        }
        compose.setContent {
            controller = rememberNavController()
            val navigation = rememberAppNavigationState("fixture")
            remember {
                navigation.details.selectedBet = selectedBet
                true
            }
            val actions = remember { AppNavigationActions({ _, _ -> }, {}, {}, {}, {}, {}) }
            CaixaDSTheme {
                NavHost(controller, startDestination = start) {
                    resultsGraph(dependencies, controller, navigation, actions)
                    composable("tickets/my-bets") { Text("Minhas apostas") }
                    composable("retail/prize/{id}") { Text("Resgate de prêmio") }
                }
            }
        }
    }
}
