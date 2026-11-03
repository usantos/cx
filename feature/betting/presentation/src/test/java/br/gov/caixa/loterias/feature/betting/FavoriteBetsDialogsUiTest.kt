// Literais numéricos descrevem seleções de loteria, identificadores e massas de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.components.AddFavoriteOptionsDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.ModalityFilterDialog
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class FavoriteBetsDialogsUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun modalityFilterDefersApplyingPendingChoiceUntilFilterPressed() {
        val selected = mutableListOf<Int?>()
        show { ModalityFilterDialog(null, listOf(2 to "Mega-Sena", 19 to "Loteca", 8 to "Lotofácil"), {}, selected::add) }
        compose.onNodeWithText("loteca").assertDoesNotExist()
        compose.onNodeWithText("mega-sena").performClick()
        compose.runOnIdle { assertEquals(emptyList<Int?>(), selected) }
        compose.onNodeWithText("Filtrar").performClick()
        compose.runOnIdle { assertEquals(listOf(2), selected) }
    }

    @Test fun modalityFilterCanResetToAllAndCancelOrCloseWithoutApplying() {
        val selected = mutableListOf<Int?>()
        var dismisses = 0
        show { ModalityFilterDialog(2, listOf(2 to "Mega-Sena"), { dismisses++ }, selected::add) }
        compose.onNodeWithText("Todas").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithContentDescription("Fechar").performClick()
        compose.runOnIdle {
            assertEquals(emptyList<Int?>(), selected)
            assertEquals(2, dismisses)
        }
        compose.onNodeWithText("Filtrar").performClick()
        compose.runOnIdle { assertEquals(listOf<Int?>(null), selected) }
    }

    @Test fun favoriteOptionsRequireOpeningContestChoiceBeforeConfirming() {
        val mega = modality(2, "Mega-Sena")
        var confirmations = 0
        show {
            AddFavoriteOptionsDialog(
                FavoriteBetsUiState(addCandidate = favorite(), games = listOf(game(mega), game(mega).copy(contestType = "ESPECIAL"))),
                {},
                { confirmations++ },
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("Adicionar ao carrinho").assertIsNotEnabled()
        compose.onNodeWithText("Tipo de Concurso").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled().performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, confirmations) }
    }

    @Test fun singleNormalFavoriteOptionsEnableConfirmAndCanExpandRecurringPicker() {
        var confirmations = 0
        var cancellations = 0
        show {
            AddFavoriteOptionsDialog(
                FavoriteBetsUiState(addCandidate = favorite(), games = listOf(game(modality(2, "Mega-Sena")))),
                { cancellations++ },
                { confirmations++ },
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("Tipo de Concurso").assertDoesNotExist()
        compose.onNodeWithText("Quantidade de teimosinhas").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled().performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, confirmations)
            assertEquals(1, cancellations)
        }
    }

    @Test fun lotomaniaFavoriteOptionsExposeMirrorToggle() {
        var toggles = 0
        show {
            AddFavoriteOptionsDialog(FavoriteBetsUiState(addCandidate = favorite().copy(modalityName = "Lotomania", modalityId = 16)), {
            }, {}, {}, {}, { toggles++ })
        }
        compose.onNodeWithText(
            "Ao selecionar esta opção, o sistema fará a aposta automaticamente, escolhendo os 50 " +
                "números que não foram registrados na sua aposta original."
        ).performClick()
        compose.runOnIdle { assertEquals(1, toggles) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { Column { content() } } }
    }
    private fun modality(id: Int, name: String) = LotteryModality(id, name, "M$id")
    private fun favorite() = FavoriteBet(1, 2, "Mega-Sena", "Números da família", listOf(listOf(1, 2, 100)))
    private fun game(modality: LotteryModality) =
        LotteryGameParameters(modality, 123, "22/10/2026", 60, 6, 20, 10, listOf(0, 2, 4), emptyList())
}
