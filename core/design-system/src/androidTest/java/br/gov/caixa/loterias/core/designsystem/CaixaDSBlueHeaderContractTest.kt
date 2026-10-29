package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CaixaDSBlueHeaderContractTest {
    @get:Rule val compose = createComposeRule()

    @Test fun blueHeaderCannotOverrideTypographyOrNavigation() {
        val override = mutableStateOf(false)
        compose.setContent { CaixaDSTheme {
            if (override.value) {
                CaixaDSScaffold("Resultados", {},
                    topBarTitleSize = 30.sp,
                    topBarTitleWeight = FontWeight.Normal,
                    topBarAnnotatedTitle = AnnotatedString("Título diferente"),
                    topBarNavigationWidth = 120.dp,
                    topBarNavigationContent = {},
                ) {}
            } else {
                CaixaDSScaffold("Resultados", {}) {}
            }
        } }
        val standard = compose.onRoot().captureToImage().asAndroidBitmap()
        compose.runOnIdle { override.value = true }
        val requestedOverrides = compose.onRoot().captureToImage().asAndroidBitmap()
        assertTrue("Blue header must always use the DS typography and back arrow", standard.sameAs(requestedOverrides))
    }
}
