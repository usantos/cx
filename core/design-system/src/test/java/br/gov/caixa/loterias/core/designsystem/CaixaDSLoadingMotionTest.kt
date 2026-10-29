package br.gov.caixa.loterias.core.designsystem

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoadingMotion
import org.junit.Assert.assertEquals
import org.junit.Test

class CaixaDSLoadingMotionTest {
    @Test
    fun `background and content stay transparent until 500ms then fade for 200ms`() {
        val animation = TargetBasedAnimation(CaixaDSLoadingMotion.fadeSpec, Float.VectorConverter, 0f, 1f)
        listOf(0L, 100L, 499L, 500L).forEach { time ->
            assertEquals(0f, animation.getValueFromNanos(time * 1_000_000), 0f)
        }
        assertEquals(.1464466f, animation.getValueFromNanos(550_000_000), .00001f)
        assertEquals(.5f, animation.getValueFromNanos(600_000_000), .00001f)
        assertEquals(.8535534f, animation.getValueFromNanos(650_000_000), .00001f)
        assertEquals(1f, animation.getValueFromNanos(700_000_000), 0f)
        assertEquals(1f, animation.getValueFromNanos(5_000_000_000), 0f)
    }

    @Test
    fun `rotation starts immediately and repeats every 1200ms with Android default easing`() {
        val animation = TargetBasedAnimation(CaixaDSLoadingMotion.rotationSpec, Float.VectorConverter, 0f, 360f)
        repeat(3) { cycle ->
            mapOf(0 to 0f, 300 to 52.72078f, 600 to 180f, 900 to 307.2792f).forEach { (time, expected) ->
                assertEquals(expected, animation.getValueFromNanos((cycle * 1_200L + time) * 1_000_000), .0001f)
            }
        }
    }

    @Test
    fun `easing matches accelerate decelerate at endpoints and stays monotonic`() {
        val samples = (0..100).map { CaixaDSLoadingMotion.easing.transform(it / 100f) }
        assertEquals(0f, samples.first(), 0f)
        assertEquals(1f, samples.last(), 0f)
        samples.zipWithNext().forEach { (before, after) -> check(after >= before) }
        assertEquals(.5f, samples[50], .00001f)
    }
}
