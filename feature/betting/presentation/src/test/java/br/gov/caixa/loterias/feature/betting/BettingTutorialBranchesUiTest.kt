// Literais numéricos descrevem seleções de loteria, identificadores e massas de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.components.AnimatedLotteryTutorial
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingFavoriteDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingTeamsContent
import br.gov.caixa.loterias.feature.betting.presentation.components.SuperSevenTutorial
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.route.ShakeTutorialRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class BettingTutorialBranchesUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun shakeArrowsVisitAllSixStepsThenComplete() {
        var completed = 0
        show { ShakeTutorialRoute { completed++ } }
        val headings =
            listOf(
                "Conheça a Aposta Shake!",
                "Abra o volante de apostas",
                "Agite o celular\npara completar",
                "Aposta completada\nautomaticamente!",
                "Ative ou desative\nno menu lateral",
                "Vamos testar?"
            )
        headings.forEachIndexed { index, title ->
            compose.onNodeWithText(title).assertExists()
            compose.onNodeWithContentDescription("Etapa ${index + 1} de 6").assertExists()
            if (index < 5) compose.onNodeWithContentDescription("Próxima etapa").performClick()
        }
        compose.onNodeWithContentDescription("Fechar tutorial").performClick()
        compose.runOnIdle { assertEquals(1, completed) }
    }

    @Test fun shakePreviousAndBackReturnToFirstPageBeforeClosing() {
        var completed = 0
        show { ShakeTutorialRoute { completed++ } }
        compose.onNodeWithContentDescription("Etapa anterior").assertDoesNotExist()
        compose.onNodeWithContentDescription("Próxima etapa").performClick()
        compose.onNodeWithContentDescription("Próxima etapa").performClick()
        compose.onNodeWithContentDescription("Etapa anterior").performClick()
        compose.onNodeWithText("Abra o volante de apostas").assertExists()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Conheça a Aposta Shake!").assertExists()
        compose.runOnIdle {
            assertEquals(0, completed)
            compose.activity.onBackPressedDispatcher.onBackPressed()
        }
        compose.runOnIdle { assertEquals(1, completed) }
    }

    @Test fun shakeSkipCompletesFromIntroduction() {
        var completed = 0
        show { ShakeTutorialRoute { completed++ } }
        compose.onNodeWithText("Pular tutorial").performClick()
        compose.runOnIdle { assertEquals(1, completed) }
    }

    @Test fun shakeIllustrationSwipesAdvanceAndReturnWithoutClosing() {
        var completed = 0
        show { ShakeTutorialRoute { completed++ } }
        compose.onNodeWithContentDescription(
            "Nova funcionalidade: Aposta Shake. Complete apostas automaticamente chacoalhando seu celular!"
        ).performTouchInput {
            swipeLeft()
        }
        compose.onNodeWithText("Abra o volante de apostas").assertExists()
        compose.onNodeWithContentDescription("Volante da Mega-Sena aberto, ainda sem números selecionados.").performTouchInput {
            swipeRight()
        }
        compose.onNodeWithText("Conheça a Aposta Shake!").assertExists()
        compose.runOnIdle { assertEquals(0, completed) }
    }

    @Test fun superSevenTutorialAdvancesPagesAndCompletesOnlyAtFinalAction() {
        val definition = tutorialDefinition("betting/tutorial-super-seven")
        val state = mutableStateOf(BettingFlowUiState(WorkflowSpec("betting/tutorial-super-seven", "", "", primaryAction = "")))
        var completed = 0
        show {
            SuperSevenTutorial(state.value, definition, { state.value = state.value.copy(page = it) }, {}, {
                if (state.value.page <
                    definition.pages.lastIndex
                ) {
                    state.value = state.value.copy(page = state.value.page + 1)
                } else {
                    completed++
                }
            }, {})
        }
        compose.onNodeWithText("Bem vindo ao Super Sete").assertExists()
        compose.onNodeWithText("Avançar").performClick()
        compose.onNodeWithText("Apostas combinadas").assertExists()
        compose.onNodeWithText("Avançar").performClick()
        compose.onNodeWithText("Pronto  :)").assertExists()
        compose.onNodeWithText("Aumente suas chances de ganhar apostando agora mesmo!").assertExists()
        compose.onNodeWithText("Iniciar aposta").performClick()
        compose.runOnIdle { assertEquals(1, completed) }
    }

    @Test fun superSevenSkipAndToolbarBackUseIndependentCallbacks() {
        var skips = 0
        var backs = 0
        show {
            SuperSevenTutorial(
                BettingFlowUiState(
                    WorkflowSpec("betting/tutorial-super-seven", "", "", primaryAction = "")
                ),
                tutorialDefinition("betting/tutorial-super-seven"),
                {
                },
                { backs++ },
                {},
                { skips++ }
            )
        }
        compose.onNodeWithText("Pular").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, skips)
            assertEquals(1, backs)
        }
    }

    @Test fun plusTutorialIntermediatePageOffersSkipAndCloseWithoutPrimary() {
        val definition = tutorialDefinition("betting/tutorial-plus")
        var close = 0
        var skip = 0
        show {
            AnimatedLotteryTutorial(
                BettingFlowUiState(
                    WorkflowSpec("betting/tutorial-plus", "", "", primaryAction = ""),
                    page = 2
                ),
                definition,
                {
                },
                { close++ },
                {},
                { skip++ },
                {}
            )
        }
        compose.onNodeWithText("Vamos começar").assertDoesNotExist()
        compose.onNodeWithText("Aposte agora").assertDoesNotExist()
        compose.onNodeWithText("Fechar").performClick()
        compose.onNodeWithText("Pular").performClick()
        compose.runOnIdle {
            assertEquals(1, close)
            assertEquals(1, skip)
        }
    }

    @Test fun plusTutorialFinalOffersCompletionPreferenceAndSecondaryClose() {
        val definition = tutorialDefinition("betting/tutorial-plus")
        val state =
            mutableStateOf(
                BettingFlowUiState(WorkflowSpec("betting/tutorial-plus", "", "", primaryAction = ""), page = definition.pages.lastIndex)
            )
        var primary = 0
        var secondary = 0
        show {
            AnimatedLotteryTutorial(state.value, definition, {}, {}, { primary++ }, { secondary++ }, {
                state.value =
                    state.value.copy(doNotShowAgain = !state.value.doNotShowAgain)
            })
        }
        compose.onNodeWithText("Não exibir novamente").assertExists()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("Aposte agora").performClick()
        compose.onNodeWithText("Fechar").performClick()
        compose.runOnIdle {
            assertEquals(true, state.value.doNotShowAgain)
            assertEquals(1, primary)
            assertEquals(1, secondary)
        }
    }

    @Test fun teamSearchCanMatchStateAndNoResultsAreNotMistakenForUnavailableTeams() {
        val state = mutableStateOf(BettingGameUiState(game = game(), teamQuery = "sp"))
        show { BettingTeamsContent(state.value, { state.value = state.value.copy(teamQuery = it) }, {}) }
        compose.onNodeWithContentDescription("Santos").assertExists()
        compose.onNodeWithContentDescription("Flamengo").assertDoesNotExist()
        compose.onNode(hasSetTextAction()).performTextReplacement("NENHUM")
        compose.onNodeWithContentDescription("Santos").assertDoesNotExist()
        compose.onNodeWithText("Times indisponíveis para este concurso.").assertDoesNotExist()
    }

    @Test fun selectedTeamQueryShowsFullCatalogAndSelectionCallbackKeepsTeamIdentity() {
        val selected = mutableListOf<SportsTeam>()
        val team = game().teams.first()
        show { BettingTeamsContent(BettingGameUiState(game = game(), selectedTeam = team, teamQuery = "Flamengo-RJ"), {}, selected::add) }
        compose.onNodeWithContentDescription("Flamengo").assertIsSelected()
        compose.onNodeWithContentDescription("Santos").performClick()
        compose.runOnIdle { assertEquals(listOf(game().teams[1]), selected) }
    }

    @Test fun absentGameShowsUnavailableTeamMessageWithoutSelectionRows() {
        show { BettingTeamsContent(BettingGameUiState(), {}, {}) }
        compose.onNodeWithText("Times indisponíveis para este concurso.").assertExists()
        compose.onNodeWithContentDescription("Flamengo").assertDoesNotExist()
    }

    @Test fun favoriteEditorUpdatesControlledValueAndSupportsImeConfirmAndCancel() {
        val name = mutableStateOf("")
        var confirms = 0
        var cancels = 0
        show { BettingFavoriteDialog(name.value, { name.value = it }, { confirms++ }, { cancels++ }) }
        compose.onNodeWithText("Ex: Aposta do João").assertExists()
        compose.onNode(hasSetTextAction()).performTextReplacement("Jogos da família")
        compose.onNode(hasSetTextAction()).assertTextEquals("Jogos da família")
        compose.onNodeWithText("Ex: Aposta do João").assertDoesNotExist()
        compose.onNode(hasSetTextAction()).performImeAction()
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(2, confirms)
            assertEquals(1, cancels)
        }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { content() } }
    }
    private fun game() = LotteryGameParameters(
        LotteryModality(
            20,
            "Timemania",
            "TIMEMANIA"
        ),
        123, "", 80, 10, 10, 10, emptyList(), emptyList(),
        teams = listOf(
            SportsTeam(1, "Flamengo", "RJ"),
            SportsTeam(2, "Santos", "SP"),
            SportsTeam(3, "Grêmio", "RS"),
            SportsTeam(4, "Palmeiras", "SP")
        )
    )
}
