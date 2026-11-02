package br.gov.caixa.loterias.feature.support

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.runtime.mutableStateOf
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSSupportTopBar
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
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
        compose.setContent { LoteriasCaixaDSTheme {
            if (support.value) LoteriasCaixaDSSupportTopBar("Jogos", {}) else LoteriasCaixaDSTopBar("Jogos", {})
        } }
        val standard = compose.onRoot().captureToImage().asAndroidBitmap()
        compose.runOnIdle { support.value = true }
        val section = compose.onRoot().captureToImage().asAndroidBitmap()
        assertTrue("Support header must use the same arrow, text size and spacing as the standard header", standard.sameAs(section))
    }

    @Test fun defaultApplicationHeaderIsSolidBlueAtBothEnds() {
        compose.setContent { LoteriasCaixaDSTheme { LoteriasCaixaDSScaffold("Cabeçalho", {}) {} } }
        val title = compose.onNodeWithText("Cabeçalho").fetchSemanticsNode().boundsInRoot
        val image = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(LoteriasCaixaDSColorTokens.Blue700, image[2, title.center.y.toInt()])
        assertEquals(LoteriasCaixaDSColorTokens.Blue700, image[image.width - 2, title.center.y.toInt()])
    }

    @Test fun termsAndFaqUseTheSameSelectedAndInactiveBluePaletteAsMarketplace() {
        compose.setContent { LoteriasCaixaDSTheme {
            SupportScreen(SupportUiState(showTermsTabs = true, selectedTab = SupportTab.FAQ), {})
        } }
        val selected = compose.onNodeWithText("Dúvidas").fetchSemanticsNode().boundsInRoot
        val inactive = compose.onNodeWithText("Termo de uso").fetchSemanticsNode().boundsInRoot
        val image = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(LoteriasCaixaDSMarketplaceStyle.tab, image[image.width - 2, selected.center.y.toInt()])
        assertEquals(LoteriasCaixaDSMarketplaceStyle.inactiveTab, image[2, inactive.center.y.toInt()])
    }
}
