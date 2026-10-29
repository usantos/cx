package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSHtmlDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme

class CaixaDSModalWidthTest {
    @get:Rule val compose = createComposeRule()

    @Test fun helpDialogRetainsLateralMarginsAndCapsItsReadableWidth() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.fillMaxSize().testTag("host-window"))
                CaixaDSHtmlDialog("Como jogar", "Ajuda", onPositive = {})
            }
        }

        val hostWidth = compose.onNodeWithTag("host-window").fetchSemanticsNode().boundsInRoot.width
        val titleWidth = compose.onNodeWithText("Como jogar").fetchSemanticsNode().boundsInRoot.width
        // Cada borda é medida separadamente: em densidades fracionárias, duas bordas
        // de 24dp arredondadas podem diferir em um pixel da conversão de um único intervalo de 48dp.
        val exteriorInsets = with(compose.density) { 2 * 24.dp.roundToPx() }.toFloat()
        val interiorInsets = with(compose.density) { 2 * 24.dp.roundToPx() }.toFloat()
        val maxDialogWidth = with(compose.density) { 560.dp.roundToPx() }.toFloat()
        assertEquals(
            "Help surface leaves 24dp on each side and is at most 560dp wide",
            minOf(hostWidth - exteriorInsets, maxDialogWidth) - interiorInsets,
            titleWidth,
            1f,
        )
    }

    @Test fun phoneDialogHas24DpOnBothSides() {
        compose.setContent {
            Box(Modifier.requiredWidth(360.dp), contentAlignment = Alignment.Center) {
                Surface(Modifier.caixaDSModalWidth().height(48.dp).testTag("modal")) {}
            }
        }
        compose.onNodeWithTag("modal").assertWidthIsEqualTo(312.dp)
    }

    @Test fun tabletDialogCapsItsWidthInsteadOfStretchingWithTheWindow() {
        compose.setContent {
            Box(Modifier.requiredWidth(1024.dp), contentAlignment = Alignment.Center) {
                Surface(Modifier.caixaDSModalWidth().height(48.dp).testTag("modal")) {}
            }
        }
        compose.onNodeWithTag("modal").assertWidthIsEqualTo(560.dp)
    }

    @Test fun narrowWindowDoesNotInheritMaterialMinimumWidth() {
        compose.setContent {
            Box(Modifier.requiredWidth(240.dp), contentAlignment = Alignment.Center) {
                Surface(Modifier.caixaDSModalWidth().widthIn(min = 280.dp).height(48.dp).testTag("modal")) {}
            }
        }
        compose.onNodeWithTag("modal").assertWidthIsEqualTo(192.dp)
    }
}
