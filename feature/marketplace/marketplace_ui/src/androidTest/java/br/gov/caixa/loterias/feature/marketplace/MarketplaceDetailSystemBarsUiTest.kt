package br.gov.caixa.loterias.feature.marketplace

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.core.view.WindowCompat
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreenDelivered
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailSystemBarsUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun systemNavigationInsetIsWhiteWithoutContrastScrimOnDarkContent() {
        compose.runOnUiThread {
            compose.activity.enableEdgeToEdge()
            compose.activity.window.isNavigationBarContrastEnforced = true
        }
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSStatusBar {
                    Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.DarkGray))
                }
            }
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertFalse(compose.activity.window.isNavigationBarContrastEnforced)
            assertTrue(
                WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView).isAppearanceLightNavigationBars
            )
        }
        val image = compose.onRoot().captureToImage().asAndroidBitmap()
        assertEquals(android.graphics.Color.WHITE, image.getPixel(10, image.height - 10))
    }

    @Test fun detailPreservesNativeWhiteStatusAndRestoresListBlueOnExit() {
        val detail = mutableStateOf(true)
        compose.runOnUiThread { compose.activity.enableEdgeToEdge() }
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSStatusBar {
                    if (detail.value) {
                        MarketplaceDetailScreenDelivered(MarketplaceDetailUiState(), {}, {}, {}, {}, {})
                    } else {
                        Box(Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White))
                    }
                }
            }
        }
        compose.waitForIdle()
        // O detalhe da modalidade mantém seu recuo branco nativo e restaura o cabeçalho geral na saída.
        assertEquals(android.graphics.Color.WHITE, compose.onRoot().captureToImage().asAndroidBitmap().getPixel(10, 10))
        compose.runOnIdle {
            assertTrue(
                WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView).isAppearanceLightStatusBars
            )
            detail.value = false
        }
        compose.waitForIdle()
        assertEquals(0xFF005CA9.toInt(), compose.onRoot().captureToImage().asAndroidBitmap().getPixel(10, 10))
        compose.runOnIdle {
            assertFalse(
                WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView).isAppearanceLightStatusBars
            )
        }
    }
}
