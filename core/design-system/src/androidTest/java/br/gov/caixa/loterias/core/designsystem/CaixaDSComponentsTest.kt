package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CaixaDSComponentsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun primaryButton_exposesLabelAndAction() {
        var clicks = 0
        composeRule.setContent {
            CaixaDSTheme {
                CaixaDSButton("Continuar", { clicks++ }, Modifier.testTag("button"))
            }
        }

        composeRule.onNodeWithTag("button").assertIsEnabled().performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun numberBall_formatsNumberWithTwoDigits() {
        composeRule.setContent {
            CaixaDSTheme { CaixaDSNumberBall(7, Modifier.testTag("number")) }
        }

        composeRule.onNodeWithTag("number").assertIsDisplayed()
        composeRule.onNodeWithTag("number").onChildren().onFirst().assertTextEquals("07")
    }

    @Test
    fun loading_fadesBackgroundAndContentTogetherAfterDelay() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.size(160.dp).background(Color.White).testTag("loading-host")) {
                    CaixaDSAuthenticationLoading()
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(400)
        assertLoadingCorner(Color.White)
        composeRule.mainClock.advanceTimeBy(500)
        assertLoadingCorner(Color(0xE6005EAB).compositeOver(Color.White))
    }

    @Test
    fun loading_removedBeforeDelayDoesNotFlashOrReusePreviousAnimation() {
        val loading = mutableStateOf(true)
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.size(160.dp).background(Color.White).testTag("loading-host")) {
                    if (loading.value) CaixaDSAuthenticationLoading()
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.runOnIdle { loading.value = false }
        composeRule.mainClock.advanceTimeBy(800)
        assertLoadingCorner(Color.White)
        composeRule.runOnIdle { loading.value = true }
        composeRule.mainClock.advanceTimeBy(300)
        assertLoadingCorner(Color.White)
        composeRule.mainClock.advanceTimeBy(500)
        assertLoadingCorner(Color(0xE6005EAB).compositeOver(Color.White))
    }

    private fun assertLoadingCorner(expected: Color) {
        // Away from the centered icon/text: detects a background drawn outside the alpha layer.
        val actual = composeRule.onNodeWithTag("loading-host").captureToImage().toPixelMap()[1, 1]
        assertEquals(expected.red, actual.red, 1f / 255)
        assertEquals(expected.green, actual.green, 1f / 255)
        assertEquals(expected.blue, actual.blue, 1f / 255)
        assertEquals(expected.alpha, actual.alpha, 1f / 255)
    }
}
