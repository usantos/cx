// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimation
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimationImage
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import com.airbnb.lottie.LottieCompositionFactory
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
class CaixaDSAnimationJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun nullCompositionLeavesHostUntouchedUntilAuthoredAnimationArrives() {
        val composition = mutableStateOf<com.airbnb.lottie.LottieComposition?>(null)
        val fixture = requireNotNull(LottieCompositionFactory.fromJsonStringSync(imageAnimation, null).value)
        compose.setContent {
            Box(Modifier.size(80.dp).background(Color.White).testTag("animation")) {
                CaixaDSAnimation(
                    composition.value,
                    { .5f },
                    Modifier.size(80.dp),
                    images = listOf(
                        CaixaDSAnimationImage("image", "Artwork", R.drawable.caixads_home_clover),
                        CaixaDSAnimationImage("missing-asset", "Ignored", R.drawable.caixads_home_clover)
                    )
                )
            }
        }
        val host = compose.onNodeWithTag("animation").assertIsDisplayed()
        val blank = host.captureToImage().toPixelMap()
        assertEquals(Color.White, blank[blank.width / 2, blank.height / 2])
        compose.runOnIdle { composition.value = fixture }
        compose.waitUntil(5_000) {
            val pixels = host.captureToImage().toPixelMap()
            (0 until pixels.height).sumOf { y ->
                (0 until pixels.width).count { x -> pixels[x, y] != Color.White }
            } > 100
        }
        val rendered = host.captureToImage().toPixelMap()
        var ink = 0
        for (y in 0 until rendered.height) for (x in 0 until rendered.width) if (rendered[x, y] != Color.White) ink++
        assertTrue("Replacement artwork must be painted by its authored Lottie image layer", ink > 100)
        assertEquals(Color.White, rendered[0, 0])
    }

    private val imageAnimation = """
        {"v":"5.7.0","fr":30,"ip":0,"op":60,"w":64,"h":64,"nm":"Fixture",
         "assets":[{"id":"image","w":64,"h":64,"u":"","p":"fixture.png"}],
         "layers":[{"ddd":0,"ind":1,"ty":2,"nm":"Artwork","refId":"image",
           "ks":{"o":{"a":0,"k":100},"r":{"a":0,"k":0},
                 "p":{"a":0,"k":[32,32,0]},"a":{"a":0,"k":[32,32,0]},"s":{"a":0,"k":[100,100,100]}},
           "ao":0,"ip":0,"op":60,"st":0,"bm":0}]}
    """.trimIndent()
}
