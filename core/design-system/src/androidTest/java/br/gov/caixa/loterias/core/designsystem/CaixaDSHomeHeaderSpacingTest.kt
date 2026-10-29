package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSHomeTopBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CaixaDSHomeHeaderSpacingTest {
    @get:Rule val compose = createComposeRule()

    @Test fun notificationsCampaignAndQuickAccessHaveEqualSpacing() {
        compose.setContent { CaixaDSTheme {
            CaixaDSHomeTopBar(0, true, false, 90f, {}, {}, {}, {})
        } }
        val bell = compose.onNodeWithContentDescription("Notificações").fetchSemanticsNode().boundsInRoot.center
        val campaign = compose.onNodeWithContentDescription("Outubro Rosa").fetchSemanticsNode().boundsInRoot.center
        val clover = compose.onNodeWithContentDescription("Apresentar atalhos").fetchSemanticsNode().boundsInRoot.center
        assertEquals(campaign.x - bell.x, clover.x - campaign.x, 1f)
        assertEquals(bell.y, campaign.y, 1f)
        assertEquals(campaign.y, clover.y, 1f)
    }
}
