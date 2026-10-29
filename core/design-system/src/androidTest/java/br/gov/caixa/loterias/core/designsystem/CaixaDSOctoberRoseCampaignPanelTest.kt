package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseCampaignPanel
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CaixaDSOctoberRoseCampaignPanelTest {
    @get:Rule val compose = createComposeRule()

    @Test fun campaignUsesReleaseTextAndRightAlignedDescriptionBounds() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.width(360.dp)) { CaixaDSOctoberRoseCampaignPanel() }
            }
        }
        val title = compose.onNodeWithText("Outubro Rosa").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val first = compose.onNodeWithText("Campanha anual que conscientiza sobre a importância da prevenção, do diagnóstico precoce e do tratamento adequado do câncer de mama e do câncer do colo do útero.")
            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val second = compose.onNodeWithText("O Ministério da Saúde orienta 3 dicas para melhorar a qualidade de vida e aumentar as chances de cura: manter hábitos saudáveis, ter atenção aos sinais do corpo e fazer exames regulares.")
            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        // O XML alinha à direita o título wrap-content e ambos os parágrafos em end34dp.
        assertEquals(title.right, first.right, 1f)
        assertEquals(first.right, second.right, 1f)
    }
}
