package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertHasClickAction
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShakeTutorialLegacyGeometryTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun firstTutorialLayoutMatchesLegacyImageInsetsAndHeaderMetrics() {
        compose.setContent { ShakeTutorialRoute(onComplete = {}) }
        compose.waitForIdle()

        val rootBounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val screenWidth = rootBounds.width
        val illustrationWidth = compose.onNodeWithContentDescription(
            "Nova funcionalidade: Aposta Shake. Complete apostas automaticamente chacoalhando seu celular!",
        ).fetchSemanticsNode().boundsInRoot.width

        val legacySideInsets = with(compose.density) { 96.dp.toPx() }
        assertEquals(screenWidth - legacySideInsets, illustrationWidth, 1f)

        val titleBounds = compose.onNodeWithText("Conheça a Aposta Shake!")
            .fetchSemanticsNode().boundsInRoot
        assertTrue("title stays in the top tutorial header", titleBounds.top < rootBounds.height * 0.12f)
        val titleHeightDp = with(compose.density) { titleBounds.height.toDp().value }
        assertTrue("title retains the legacy 20sp line metric", titleHeightDp in 20f..34f)

    }

    @Test
    fun arrowDescriptionBelongsToItsClickableButton() {
        compose.setContent { ShakeTutorialRoute(onComplete = {}) }
        compose.onNodeWithContentDescription("Próxima etapa").assertHasClickAction()
    }

    @Test
    fun frameTransformMatchesLegacyCropAndLetterboxing() {
        assertEquals(0xFF0066B3.toInt(), CaixaDSColorTokens.LegacyCaixaBlue.toArgb())

        val standard = ShakeTutorialLegacyFrameTransform.fromSourceSize(Size(300f, 580f))
        assertEquals(306f, standard.outputWidth, 0f)
        assertEquals(580f, standard.outputHeight, 0f)
        assertEquals(3f, standard.destinationLeft, 0f)
        assertEquals(0f, standard.destinationTop, 0f)

        val margined = ShakeTutorialLegacyFrameTransform.fromSourceSize(Size(412f, 804f))
        assertEquals(53f, margined.sourceLeft, 0f)
        assertEquals(119f, margined.sourceTop, 0f)
        assertEquals(306f, margined.sourceWidth, 0f)
        assertEquals(580f, margined.sourceHeight, 0f)
        assertEquals(306f, margined.destinationWidth, 0f)
        assertEquals(580f, margined.destinationHeight, 0f)

        val agitation = ShakeTutorialLegacyFrameTransform.fromSourceSize(Size(404f, 604f))
        assertEquals(0f, agitation.sourceLeft, 0f)
        assertEquals(0f, agitation.sourceTop, 0f)
        assertEquals(306f, agitation.destinationWidth, 0f)
        assertEquals(458f, agitation.destinationHeight, 1f)
        assertEquals(61f, agitation.destinationTop, 1f)
    }

}
