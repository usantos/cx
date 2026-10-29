package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.onNodeWithText
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
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoading
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLightColors
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButtonVariant
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSNumberBall
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSContentState

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
    fun allButtonVariantsPreserveHeightAndSuppressDisabledOrLoadingClicks() {
        val enabled = mutableStateOf(true)
        val loading = mutableStateOf(false)
        val clicks = mutableListOf<CaixaDSButtonVariant>()
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                Column {
                    CaixaDSButtonVariant.entries.forEach { variant ->
                        CaixaDSButton(variant.name, { clicks.add(variant) },
                            Modifier.testTag(variant.name), enabled = enabled.value,
                            loading = loading.value, variant = variant)
                    }
                }
            }
        }
        listOf(CaixaDSButtonVariant.Primary to CaixaDSLightColors.brand,
            CaixaDSButtonVariant.Danger to CaixaDSLightColors.error).forEach { (variant, expected) ->
            val pixels = composeRule.onNodeWithTag(variant.name).captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 4, pixels.height / 4]
            assertEquals(expected.red, actual.red, 1f / 255)
            assertEquals(expected.green, actual.green, 1f / 255)
            assertEquals(expected.blue, actual.blue, 1f / 255)
        }
        CaixaDSButtonVariant.entries.forEach { variant ->
            val button = composeRule.onNodeWithTag(variant.name)
            button.assertIsEnabled().performClick()
            assertEquals(with(composeRule.density) { 48.dp.toPx() },
                button.fetchSemanticsNode().boundsInRoot.height, 1f)
        }
        composeRule.runOnIdle { assertEquals(CaixaDSButtonVariant.entries.toList(), clicks) }
        composeRule.runOnIdle { enabled.value = false }
        CaixaDSButtonVariant.entries.forEach { variant ->
            composeRule.onNodeWithTag(variant.name).assertIsNotEnabled().performTouchInput { click() }
        }
        composeRule.runOnIdle { enabled.value = true; loading.value = true }
        CaixaDSButtonVariant.entries.forEach { variant ->
            composeRule.onNodeWithTag(variant.name).assertIsNotEnabled().performTouchInput { click() }
            composeRule.onNodeWithText(variant.name).assertDoesNotExist()
        }
        composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .assertCountEquals(4)
        composeRule.runOnIdle { assertEquals(CaixaDSButtonVariant.entries.toList(), clicks) }
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
        // Confirma a composição que retorna antes de medir seu novo atraso de esmaecimento.
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(300)
        assertLoadingCorner(Color.White)
        composeRule.mainClock.advanceTimeBy(500)
        assertLoadingCorner(Color(0xE6005EAB).compositeOver(Color.White))
    }

    @Test
    fun contentState_prioritizesLoadingThenErrorAndRetriesBeforeShowingContent() {
        val loading = mutableStateOf(true)
        val error = mutableStateOf<String?>("Falha ao carregar")
        var retries = 0
        composeRule.setContent {
            CaixaDSTheme {
                CaixaDSContentState(loading.value, error.value, PaddingValues(8.dp), { retries++ }) {
                    Text("Conteúdo carregado")
                }
            }
        }
        val progress = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        composeRule.onNode(progress).assertIsDisplayed()
        composeRule.onNodeWithText("Falha ao carregar").assertDoesNotExist()
        composeRule.onNodeWithText("Tentar novamente").assertDoesNotExist()
        composeRule.onNodeWithText("Conteúdo carregado").assertDoesNotExist()

        composeRule.runOnIdle { loading.value = false }
        composeRule.onNode(progress).assertDoesNotExist()
        composeRule.onNodeWithText("Falha ao carregar").assertIsDisplayed()
        composeRule.onNodeWithText("Conteúdo carregado").assertDoesNotExist()
        composeRule.onNodeWithText("Tentar novamente").performClick()
        composeRule.runOnIdle { assertEquals(1, retries) }

        composeRule.runOnIdle { error.value = null }
        composeRule.onNodeWithText("Conteúdo carregado").assertIsDisplayed()
        composeRule.onNodeWithText("Falha ao carregar").assertDoesNotExist()
        composeRule.onNodeWithText("Tentar novamente").assertDoesNotExist()
        composeRule.onNode(progress).assertDoesNotExist()
    }

    private fun assertLoadingCorner(expected: Color) {
        // Longe do ícone/texto centralizado: detecta um fundo desenhado fora da camada alfa.
        val actual = composeRule.onNodeWithTag("loading-host").captureToImage().toPixelMap()[1, 1]
        assertEquals(expected.red, actual.red, 1f / 255)
        assertEquals(expected.green, actual.green, 1f / 255)
        assertEquals(expected.blue, actual.blue, 1f / 255)
        assertEquals(expected.alpha, actual.alpha, 1f / 255)
    }
}
