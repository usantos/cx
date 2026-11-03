package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRuleV2
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeClassicSpecialContentTest {
    @get:Rule val compose = createFeatureComposeRuleV2()

    @Test fun comboKeepsLegacyArtworkDimensions() {
        compose.setFeatureContent { LoteriasCaixaDSTheme(darkTheme = false) { SpecialLotteryContent(HomeLotteryKind.COMBO) } }
        compose.onNodeWithContentDescription("Combos de apostas")
            .assertWidthIsEqualTo(233.dp).assertHeightIsEqualTo(327.dp)
        compose.onNodeWithText("Mais que apostas: combos criados\npara aumentar suas chances\nde realizar o que você deseja.")
            .assertIsDisplayed()
    }

    @Test fun instantKeepsOriginalArtworkAndPortalCopy() {
        compose.setFeatureContent { LoteriasCaixaDSTheme(darkTheme = false) { SpecialLotteryContent(HomeLotteryKind.INSTANT) } }
        compose.onNodeWithContentDescription("Bilhetes da Instantânea")
            .assertWidthIsEqualTo(246.dp).assertHeightIsEqualTo(186.dp)
        compose.onNodeWithText("Acesse e jogue\nno Portal da INSTANTÂNEA").assertIsDisplayed()
    }

    @Test fun quickAccessKeepsAllFourActionsAndFullWomenSupportCopy() {
        val clicks = IntArray(4)
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                HomeQuickAccessPanel(
                    null,
                    false,
                    { clicks[0]++ },
                    { clicks[1]++ },
                    { clicks[2]++ },
                    { clicks[3]++ }
                )
            }
        }
        listOf("Conferir bilhetes", "Jogo responsável", "Dados indisponíveis", "Central de Atendimento à Mulher")
            .forEach { compose.onNodeWithText(it).assertIsDisplayed().performClick() }
        compose.onNodeWithText("Denuncie e busque ajuda a vítimas de violência pelo WhatsApp do canal 180.")
            .assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(1, 1, 1, 1), clicks.toList()) }
    }
}
