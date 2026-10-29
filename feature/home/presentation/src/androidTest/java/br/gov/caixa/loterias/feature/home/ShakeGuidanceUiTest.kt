package br.gov.caixa.loterias.feature.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.presentation.components.ShakeMenuRow
import br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ShakeGuidanceUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun pinkSwitchKeepsPinkThumbAndWhiteMarkInBothStates() {
        val settings = mutableStateOf(HomePreferencesUiState(loaded = true, shakeEnabled = true))
        compose.setContent {
            CaixaDSTheme {
                ShakeMenuRow(settings.value, {}, {}, active = true, accent = CaixaDSColorTokens.OctoberRose,
                    onAccent = Color.White, trackBackground = Color.White, invertSwitchColors = true)
            }
        }
        fun assertSwitchColors(enabled: Boolean) {
            val pixels = compose.onNodeWithContentDescription("Aposta Shake").captureToImage().toPixelMap()
            val thumbX = (pixels.width * if (enabled) .75f else .25f).toInt()
            val trackX = (pixels.width * if (enabled) .25f else .75f).toInt()
            val centerY = pixels.height / 2
            // Amostra o seletor acima da marca branca e inspeciona a área interna da marca.
            assertEquals(CaixaDSColorTokens.OctoberRose, pixels[thumbX, pixels.height * 17 / 48])
            assertEquals(Color.White, pixels[trackX, centerY])
            val radius = pixels.width / 8
            assertTrue((thumbX - radius until thumbX + radius).any { x ->
                (centerY - radius until centerY + radius).any { y -> pixels[x, y] == Color.White }
            })
        }
        assertSwitchColors(true)
        compose.runOnIdle { settings.value = settings.value.copy(shakeEnabled = false) }
        assertSwitchColors(false)
    }

    @Test fun newPendingGuidanceIsShownEvenWhenDrawerWasAlreadyOpen() {
        val settings = mutableStateOf(HomePreferencesUiState(loaded = true))
        var consumed = 0
        compose.setContent {
            CaixaDSTheme {
                ShakeMenuRow(settings.value, {}, { consumed++; settings.value = settings.value.copy(shakeTooltipPending = false) }, active = true)
            }
        }
        compose.runOnIdle { settings.value = settings.value.copy(shakeTooltipPending = true) }
        compose.onNodeWithText("Agite o celular para preencher\nsua aposta com números aleatórios").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, consumed) }
    }
}
