package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.components.FavoriteTrapezoid
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoriteTrapezoidClippingTest {
    @get:Rule val compose = createComposeRule()

    @Test fun nativeBackgroundCannotDrawOverAdjacentFavorites() {
        val modality = mutableIntStateOf(2)
        var density = 1f
        compose.setContent { CaixaDSTheme {
            density = LocalDensity.current.density
            Box(Modifier.size(300.dp, 200.dp).background(CaixaDSColorTokens.White).testTag("canvas")) {
                FavoriteTrapezoid(modality.intValue, Modifier.offset(20.dp, 40.dp).size(80.dp, 55.dp))
            }
        } }
        for (id in listOf(2, 8, 3, 9, 11, 16, 18, 19, 20, 7)) {
            compose.runOnIdle { modality.intValue = id }
            val pixels = compose.onNodeWithTag("canvas").captureToImage().toPixelMap()
            for (y in 0 until pixels.height step 3) {
                for (x in 0 until pixels.width step 3) {
                    if (x < 19 * density || x > 101 * density || y < 39 * density || y > 96 * density) {
                        assertEquals("Modality $id paints outside its header at $x,$y", CaixaDSColorTokens.White, pixels[x, y])
                    }
                }
            }
        }
    }
}
