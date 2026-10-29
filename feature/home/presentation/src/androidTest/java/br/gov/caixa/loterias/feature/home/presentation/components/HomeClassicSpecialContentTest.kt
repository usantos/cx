package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeClassicSpecialContentTest {
    @get:Rule val compose = createComposeRule()

    @Test fun comboKeepsLegacyArtworkDimensions() {
        compose.setContent { CaixaDSTheme(darkTheme = false) { SpecialLotteryContent(HomeLotteryKind.COMBO) } }
        compose.onNodeWithContentDescription("Combos de apostas")
            .assertWidthIsEqualTo(233.dp).assertHeightIsEqualTo(327.dp)
        compose.onNodeWithText("Mais que apostas: combos criados\npara aumentar suas chances\nde realizar o que você deseja.")
            .assertIsDisplayed()
    }

    @Test fun instantKeepsOriginalArtworkAndPortalCopy() {
        compose.setContent { CaixaDSTheme(darkTheme = false) { SpecialLotteryContent(HomeLotteryKind.INSTANT) } }
        compose.onNodeWithContentDescription("Bilhetes da Instantânea")
            .assertWidthIsEqualTo(246.dp).assertHeightIsEqualTo(186.dp)
        compose.onNodeWithText("Acesse e jogue\nno Portal da INSTANTÂNEA").assertIsDisplayed()
    }

    @Test fun quickAccessKeepsAllFourActionsAndFullWomenSupportCopy() {
        val clicks = IntArray(4)
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                HomeQuickAccessPanel(null, false,
                    { clicks[0]++ }, { clicks[1]++ }, { clicks[2]++ }, { clicks[3]++ })
            }
        }
        listOf("Conferir bilhetes", "Jogo responsável", "Dados indisponíveis", "Central de Atendimento à Mulher")
            .forEach { compose.onNodeWithText(it).assertIsDisplayed().performClick() }
        compose.onNodeWithText("Denuncie e busque ajuda a vítimas de violência pelo WhatsApp do canal 180.")
            .assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(1, 1, 1, 1), clicks.toList()) }
    }
}
