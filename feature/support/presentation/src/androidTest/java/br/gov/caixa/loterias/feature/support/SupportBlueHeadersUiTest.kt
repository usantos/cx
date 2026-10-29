package br.gov.caixa.loterias.feature.support

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.runtime.mutableStateOf
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSSupportTopBar
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SupportBlueHeadersUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun supportSectionHeaderMatchesStandardArrowTypographyAndSpacing() {
        val support = mutableStateOf(false)
        compose.setContent { CaixaDSTheme {
            if (support.value) CaixaDSSupportTopBar("Jogos", {}) else CaixaDSTopBar("Jogos", {})
        } }
        val standard = compose.onRoot().captureToImage().asAndroidBitmap()
        compose.runOnIdle { support.value = true }
        val section = compose.onRoot().captureToImage().asAndroidBitmap()
        assertTrue("Support header must use the same arrow, text size and spacing as the standard header", standard.sameAs(section))
    }

    @Test fun defaultApplicationHeaderIsSolidBlueAtBothEnds() {
        compose.setContent { CaixaDSTheme { CaixaDSScaffold("Cabeçalho", {}) {} } }
        val title = compose.onNodeWithText("Cabeçalho").fetchSemanticsNode().boundsInRoot
        val image = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(CaixaDSColorTokens.Blue700, image[2, title.center.y.toInt()])
        assertEquals(CaixaDSColorTokens.Blue700, image[image.width - 2, title.center.y.toInt()])
    }

    @Test fun termsAndFaqUseTheSameSelectedAndInactiveBluePaletteAsMarketplace() {
        compose.setContent { CaixaDSTheme {
            SupportScreen(SupportUiState(showTermsTabs = true, selectedTab = SupportTab.FAQ), {})
        } }
        val selected = compose.onNodeWithText("Dúvidas").fetchSemanticsNode().boundsInRoot
        val inactive = compose.onNodeWithText("Termo de uso").fetchSemanticsNode().boundsInRoot
        val image = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(CaixaDSMarketplaceStyle.tab, image[image.width - 2, selected.center.y.toInt()])
        assertEquals(CaixaDSMarketplaceStyle.inactiveTab, image[2, inactive.center.y.toInt()])
    }
}
