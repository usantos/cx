package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.components.FavoriteModalityGrid
import br.gov.caixa.loterias.feature.betting.presentation.components.LegacyComboCatalogCard
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidModalitiesPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidNumberCells
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPreset
import br.gov.caixa.loterias.feature.betting.presentation.screen.LotogolBetContent
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BettingAccessibilityRegressionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun comboActionIncludesItsNameAndPrice() {
        var calls = 0
        compose.setContent {
            CaixaDSTheme {
                LegacyComboCatalogCard(BetCombo(1, "Combo acessível", "", false, BigDecimal.TEN, emptyList())) { calls++ }
            }
        }
        compose.onNode(hasText("Combo acessível") and hasClickAction()).performClick()
        compose.runOnIdle { assertEquals(1, calls) }
    }

    @Test fun rapidPresetExposesSelectedRadioState() {
        compose.setContent { CaixaDSTheme { RapidPreset("Meu valor", true, true, false) {} } }
        compose.onNodeWithText("Meu valor").assertIsSelected()
        compose.onNodeWithText("Meu valor").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
    }

    @Test fun rapidNumberAnnouncesToggleStateAndInvokesOnce() {
        val chosen = mutableStateOf(emptySet<Int>())
        var calls = 0
        compose.setContent {
            CaixaDSTheme {
                RapidNumberCells(listOf(1), chosen.value, false, {
                    calls++
                    chosen.value = setOf(it)
                })
            }
        }
        compose.onNodeWithText("01").performClick().assertIsOn()
        compose.runOnIdle { assertEquals(1, calls) }
    }

    @Test fun favoriteModalityFilterExposesSelection() {
        compose.setContent { CaixaDSTheme { Column { FavoriteModalityGrid(listOf(2 to "Mega-Sena"), 2) {} } } }
        compose.onNodeWithText("mega-sena").assertIsSelected()
    }

    @Test fun rapidModalitiesExposeIndependentToggleState() {
        compose.setContent {
            CaixaDSTheme {
                RapidModalitiesPage(listOf(LotteryModality(2, "Mega-Sena", "MEGASENA")), setOf(2)) {}
            }
        }
        compose.onNodeWithText("mega-sena").assertIsOn()
    }

    @Test fun lotogolScoreIdentifiesItsTeamAndSelectedState() {
        val game = LotteryGameParameters(
            LotteryModality(LOTOGOL, "Lotogol", "LOTOGOL"), 1, "", MAXIMUM_LOTOGOL_SCORE, 1, MAXIMUM_LOTOGOL_SCORE, 1,
            emptyList(), emptyList(), matches = listOf(SportsMatch(1, SportsTeam(1, "Casa", "SP"), SportsTeam(2, "Fora", "RJ")))
        )
        val actions = BettingGameActions({}, {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, {}, { _, _, _ -> }, {}, {}, {}, {})
        compose.setContent {
            CaixaDSTheme {
                LotogolBetContent(BettingGameUiState(loading = false, game = game, lotogolScores = mapOf(1 to (2 to 1))), actions)
            }
        }
        compose.onNodeWithContentDescription("Casa, 2 gols").assertIsSelected()
        compose.onNodeWithContentDescription("Fora, 1 gol").assertIsSelected()
    }

    @Test fun disabledRapidPresetRetainsStateButOffersNoAction() {
        compose.setContent { CaixaDSTheme { RapidPreset("...", true, false, true) {} } }
        compose.onNodeWithText("...").assertIsSelected().assertIsNotEnabled()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        compose.onNodeWithText("...").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
    }

    @Test fun lotogolFivePlusDescribesThresholdAndKeepsScoreSentinel() {
        val game = LotteryGameParameters(
            LotteryModality(LOTOGOL, "Lotogol", "LOTOGOL"), 1, "",
            MAXIMUM_LOTOGOL_SCORE, 1, MAXIMUM_LOTOGOL_SCORE, 1, emptyList(), emptyList(),
            matches = listOf(SportsMatch(1, SportsTeam(1, "Casa", "SP"), SportsTeam(2, "Fora", "RJ")))
        )
        val selections = mutableListOf<Triple<Int, Boolean, Int>>()
        val actions = BettingGameActions(
            {}, {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, {},
            { match, home, score -> selections.add(Triple(match, home, score)) }, {}, {}, {}, {}
        )
        compose.setContent {
            CaixaDSTheme {
                LotogolBetContent(
                    BettingGameUiState(
                        loading = false,
                        game = game,
                        lotogolScores = mapOf(1 to (MAXIMUM_LOTOGOL_SCORE to null))
                    ),
                    actions
                )
            }
        }
        compose.onNodeWithContentDescription("Casa, 5 ou mais gols").assertIsSelected().assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(listOf(Triple(1, true, MAXIMUM_LOTOGOL_SCORE)), selections) }
    }
}

private const val LOTOGOL = 14
private const val MAXIMUM_LOTOGOL_SCORE = 5
