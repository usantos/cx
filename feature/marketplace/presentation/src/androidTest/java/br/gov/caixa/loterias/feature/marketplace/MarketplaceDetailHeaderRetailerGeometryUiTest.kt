package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.components.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailHeaderRetailerGeometryUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun headerMetadataStartsAtNative4dpInset() {
        compose.setContent { CaixaDSTheme { DetailHeader(detail(), marketplaceColors("MEGA_SENA"), {}) } }
        assertEquals(px(4), compose.onNodeWithText("Concurso").fetchSemanticsNode().boundsInRoot.top, 1f)
    }

    @Test fun favoriteCaptionOverlapsButtonWithoutIncreasingRetailerRowHeight() {
        compose.setContent { CaixaDSTheme {
            Box(Modifier.width(426.dp)) { DetailRetailerData(detail(), false, false, {}) }
        } }
        val name = compose.onNodeWithText("Loterica Visual Offline").fetchSemanticsNode().boundsInRoot
        val caption = compose.onNodeWithText("Favoritar\nLotérica").fetchSemanticsNode().boundsInRoot
        // APK: topo da lotérica em 713, topo da legenda em 806, largura de 240px na densidade 3.
        assertEquals(px(31), caption.top - name.top, 1f)
        assertEquals(px(80), caption.width, 1f)
    }

    @Test fun lotecaFathersDayUsesNativeSingleLineHeaderLabel() {
        compose.setContent { CaixaDSTheme {
            DetailHeader(detail().copy(modality = "LOTECA", contestType = 2), marketplaceColors("LOTECA"), {},
                specialName = "loteca\ndos pais")
        } }
        compose.onNodeWithText("loteca dos pais").assertIsDisplayed()
        compose.onNodeWithText("loteca\ndos pais").assertDoesNotExist()
    }

    private fun px(value: Int) = with(compose.density) { value.dp.toPx() }
    private fun detail() = MarketplacePoolDetail("B1", "MEGA_SENA", 3070, 10, "LOTERICA VISUAL OFFLINE",
        "01.234567-8", "São Paulo - SP", "21/10/2026", "150000000", 20, 50, 1, 6,
        "10", "0.50", "10.50", emptyList())
}
