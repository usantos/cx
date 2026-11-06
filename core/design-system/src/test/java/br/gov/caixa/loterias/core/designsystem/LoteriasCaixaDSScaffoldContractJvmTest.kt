package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.testing.capturePreviewEvidence
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LoteriasCaixaDSScaffoldContractJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun blueHeaderKeepsCanonicalTypographyNavigationAndCallbacks() {
        var backs = 0
        var actions = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSScaffold(
                    title = "Resumo",
                    onBack = { backs++ },
                    topBarNavigationWidth = OVERRIDE_NAVIGATION.dp,
                    topBarNavigationContent = { Text("Navegação ignorada") },
                    topBarTitleSize = OVERRIDE_TITLE.sp,
                    topBarTitleWeight = FontWeight.Light,
                    topBarAnnotatedTitle = AnnotatedString("Anotação ignorada"),
                    topBarAction = {
                        IconButton({ actions++ }) { Icon(LoteriasCaixaDSIcons.Back, "Ação") }
                    },
                    bottomBar = { Text("Rodapé") }
                ) { padding -> Text("Conteúdo", Modifier.padding(padding)) }
            }
        }
        compose.onNodeWithText("Navegação ignorada").assertDoesNotExist()
        compose.onNodeWithText("Anotação ignorada").assertDoesNotExist()
        val title = compose.onNodeWithText("Resumo").assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        title.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(CANONICAL_TITLE.sp, layouts.single().layoutInput.style.fontSize)
        assertEquals(FontWeight.Bold, layouts.single().layoutInput.style.fontWeight)
        assertEquals(
            with(compose.density) {
                CANONICAL_NAVIGATION.dp.toPx()
            },
            title.fetchSemanticsNode().boundsInRoot.left,
            PIXEL_TOLERANCE
        )
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithContentDescription("Ação").performClick()
        compose.runOnIdle {
            assertEquals(1, backs)
            assertEquals(1, actions)
        }
        capturePreviewEvidence(compose, "scaffold-blue-header")
    }

    @Test
    fun coloredHeaderForwardsNavigationAnnotatedTypographySubtitleAndActionWidth() {
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSScaffold(
                    title = "Fallback",
                    onBack = {},
                    gradientTopBar = true,
                    topBarStartColor = Color.Red,
                    topBarEndColor = Color.Green,
                    topBarNavigationWidth = OVERRIDE_NAVIGATION.dp,
                    topBarNavigationContent = { Text("Menu") },
                    topBarTitleSize = OVERRIDE_TITLE.sp,
                    topBarTitleWeight = FontWeight.Light,
                    topBarAnnotatedTitle = AnnotatedString("Anotado"),
                    topBarSubtitle = "Detalhes",
                    topBarActionWidth = ACTION_WIDTH.dp,
                    topBarAction = { Box(Modifier.fillMaxSize().testTag("action-slot")) }
                ) { padding -> Text("Conteúdo", Modifier.padding(padding)) }
            }
        }
        compose.onNodeWithText("Menu").assertIsDisplayed()
        compose.onNodeWithText("Fallback").assertDoesNotExist()
        compose.onNodeWithContentDescription("Voltar").assertDoesNotExist()
        val title = compose.onNodeWithText("Anotado")
        val layouts = mutableListOf<TextLayoutResult>()
        title.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(OVERRIDE_TITLE.sp, layouts.single().layoutInput.style.fontSize)
        assertEquals(FontWeight.Light, layouts.single().layoutInput.style.fontWeight)
        assertEquals(with(compose.density) { OVERRIDE_NAVIGATION.dp.toPx() }, title.fetchSemanticsNode().boundsInRoot.left, PIXEL_TOLERANCE)
        val action = compose.onNodeWithTag("action-slot").fetchSemanticsNode().boundsInRoot
        assertEquals(with(compose.density) { ACTION_WIDTH.dp.toPx() }, action.width, PIXEL_TOLERANCE)
        assertTrue(
            compose.onNodeWithText("Detalhes").fetchSemanticsNode().boundsInRoot.top > title.fetchSemanticsNode().boundsInRoot.bottom
        )
        val pixels = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(Color.Green, pixels[pixels.width - 1, action.center.y.toInt()])
        capturePreviewEvidence(compose, "scaffold-colored-header")
    }

    @Test
    fun customTopBarReplacesStandardHeaderAndBottomBarUsesSurfaceAndNavigationInsets() {
        var received: PaddingValues? = null
        var surface = Color.Unspecified
        var navigationInset = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                surface = LoteriasCaixaDS.colors.surface
                navigationInset = WindowInsets.navigationBars.getBottom(LocalDensity.current)
                LoteriasCaixaDSScaffold(
                    title = "Ausente",
                    onBack = {},
                    containerColor = Color.Magenta,
                    topBarAction = { Text("Ação ausente") },
                    customTopBar = { Box(Modifier.fillMaxWidth().height(CUSTOM_HEIGHT.dp).background(Color.Cyan).testTag("custom")) },
                    bottomBar = { Box(Modifier.fillMaxWidth().height(FOOTER_HEIGHT.dp).testTag("footer")) }
                ) { padding ->
                    received = padding
                    Box(Modifier.fillMaxSize().padding(padding)) { Text("Conteúdo") }
                }
            }
        }
        compose.onNodeWithText("Ausente").assertDoesNotExist()
        compose.onNodeWithText("Ação ausente").assertDoesNotExist()
        compose.onNodeWithContentDescription("Voltar").assertDoesNotExist()
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val header = compose.onNodeWithTag("custom").fetchSemanticsNode().boundsInRoot
        val footer = compose.onNodeWithTag("footer").fetchSemanticsNode().boundsInRoot
        assertEquals(with(compose.density) { CUSTOM_HEIGHT.dp.toPx() }, header.height, PIXEL_TOLERANCE)
        assertEquals(navigationInset.toFloat(), root.bottom - footer.bottom, PIXEL_TOLERANCE)
        compose.runOnIdle {
            assertEquals(header.height, with(compose.density) { checkNotNull(received).calculateTopPadding().toPx() }, PIXEL_TOLERANCE)
            assertEquals(
                root.bottom - footer.top,
                with(compose.density) {
                    checkNotNull(received).calculateBottomPadding().toPx()
                },
                PIXEL_TOLERANCE
            )
        }
        val pixels = compose.onRoot().captureToImage().toPixelMap()
        assertEquals(Color.Cyan, pixels[0, header.center.y.toInt()])
        assertEquals(Color.Magenta, pixels[0, (header.bottom + PIXEL_SAMPLE_OFFSET).toInt()])
        assertEquals(surface, pixels[0, footer.center.y.toInt()])
        capturePreviewEvidence(compose, "scaffold-custom-bars")
    }

    @Test
    fun fractionalRtlPaddingRetainsDelegatedLiveReadsAndToolbarTruncation() {
        val headerHeight = mutableStateOf(CUSTOM_HEIGHT.dp)
        var original: PaddingValues? = null
        var current: PaddingValues? = null
        val density = Density(FRACTIONAL_DENSITY)
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides density, LocalLayoutDirection provides LayoutDirection.Rtl) {
                LoteriasCaixaDSTheme {
                    LoteriasCaixaDSScaffold(
                        title = "Custom",
                        onBack = null,
                        customTopBar = { Box(Modifier.fillMaxWidth().height(headerHeight.value).testTag("custom")) },
                        bottomBar = { Box(Modifier.fillMaxWidth().height(FOOTER_HEIGHT.dp)) },
                        contentUsesTruncatedToolbarGuideline = true
                    ) { padding ->
                        if (original == null) original = padding
                        current = padding
                        Box(Modifier.fillMaxSize().padding(padding).testTag("content"))
                    }
                }
            }
        }
        val overlapPixels = with(density) { TOOLBAR_HEIGHT.dp.roundToPx() - TOOLBAR_HEIGHT.dp.toPx().toInt() }
        assertEquals(1, overlapPixels)
        fun assertPadding() {
            val header = compose.onNodeWithTag("custom").fetchSemanticsNode().boundsInRoot
            val body = compose.onNodeWithTag("content").fetchSemanticsNode().boundsInRoot
            compose.runOnIdle {
                val retained = checkNotNull(original)
                val latest = checkNotNull(current)
                assertEquals(header.height - overlapPixels, with(density) { retained.calculateTopPadding().toPx() }, PIXEL_TOLERANCE)
                assertEquals(latest.calculateBottomPadding(), retained.calculateBottomPadding())
                listOf(LayoutDirection.Ltr, LayoutDirection.Rtl).forEach { direction ->
                    assertEquals(latest.calculateLeftPadding(direction), retained.calculateLeftPadding(direction))
                    assertEquals(latest.calculateRightPadding(direction), retained.calculateRightPadding(direction))
                }
                assertEquals(body.left, with(density) { retained.calculateLeftPadding(LayoutDirection.Rtl).toPx() }, PIXEL_TOLERANCE)
            }
        }
        assertPadding()
        compose.runOnIdle { headerHeight.value = CHANGED_HEIGHT.dp }
        assertPadding()
        capturePreviewEvidence(compose, "scaffold-fractional-rtl")
    }

    private companion object {
        const val CANONICAL_NAVIGATION = 72
        const val CANONICAL_TITLE = 16
        const val OVERRIDE_NAVIGATION = 96
        const val OVERRIDE_TITLE = 23
        const val ACTION_WIDTH = 64
        const val CUSTOM_HEIGHT = 43
        const val CHANGED_HEIGHT = 61
        const val FOOTER_HEIGHT = 32
        const val TOOLBAR_HEIGHT = 56
        const val FRACTIONAL_DENSITY = 1.3f
        const val PIXEL_TOLERANCE = 1f
        const val PIXEL_SAMPLE_OFFSET = 2
    }
}
