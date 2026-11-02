package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoteriasCaixaDSBlueHeaderContractTest {
    @get:Rule val compose = createComposeRule()

    @Test fun blueHeaderCannotOverrideTypographyOrNavigation() {
        val override = mutableStateOf(false)
        compose.setContent {
            LoteriasCaixaDSTheme {
                if (override.value) {
                    LoteriasCaixaDSScaffold(
                        "Resultados",
                        {},
                        topBarTitleSize = 30.sp,
                        topBarTitleWeight = FontWeight.Normal,
                        topBarAnnotatedTitle = AnnotatedString("Título diferente"),
                        topBarNavigationWidth = 120.dp,
                        topBarNavigationContent = {}
                    ) {}
                } else {
                    LoteriasCaixaDSScaffold("Resultados", {}) {}
                }
            }
        }
        val standard = compose.onRoot().captureToImage().asAndroidBitmap()
        compose.runOnIdle { override.value = true }
        val requestedOverrides = compose.onRoot().captureToImage().asAndroidBitmap()
        assertTrue("Blue header must always use the DS typography and back arrow", standard.sameAs(requestedOverrides))
    }
}
