// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.recovery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingGameError
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BettingRecoveryReviewUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `expired selection is visible for review without a submission control`() {
        compose.setContent {
            CaixaDSTheme {
                BettingGameError(
                    "Concurso encerrado",
                    PaddingValues(),
                    {},
                    BettingCheckpoint(2, "NORMAL", 99, numbers = setOf(4, 1), numberQuantity = 6)
                )
            }
        }
        compose.onNodeWithText("Rascunho preservado para conferência").assertIsDisplayed()
        compose.onNodeWithText("Números: 1 · 4").assertIsDisplayed()
        compose.onNodeWithText("Adicionar ao carrinho").assertDoesNotExist()
    }
}
