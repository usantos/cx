package br.gov.caixa.loterias.core.designsystem

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLoadingMotion
import org.junit.Assert.assertEquals
import org.junit.Test

class LoteriasCaixaDSLoadingMotionTest {
    @Test
    fun `background and content stay transparent until 500ms then fade for 200ms`() {
        val animation = TargetBasedAnimation(LoteriasCaixaDSLoadingMotion.fadeSpec, Float.VectorConverter, 0f, 1f)
        listOf(
            0L,
            LoadingMotionTestExpectations.EARLY_FADE_SAMPLE_MILLIS,
            LoadingMotionTestExpectations.LAST_TRANSPARENT_SAMPLE_MILLIS,
            LoadingMotionTestExpectations.FADE_START_MILLIS
        ).forEach { time ->
            assertEquals(0f, animation.getValueFromNanos(time * LoadingMotionTestExpectations.NANOS_PER_MILLI), 0f)
        }
        assertEquals(
            LoadingMotionTestExpectations.EXPECTED_QUARTER_FADE,
            animation.getValueFromNanos(LoadingMotionTestExpectations.QUARTER_FADE_NANOS),
            LoadingMotionTestExpectations.FADE_TOLERANCE
        )
        assertEquals(
            LoadingMotionTestExpectations.EXPECTED_MIDPOINT,
            animation.getValueFromNanos(LoadingMotionTestExpectations.HALF_FADE_NANOS),
            LoadingMotionTestExpectations.FADE_TOLERANCE
        )
        assertEquals(
            LoadingMotionTestExpectations.EXPECTED_THREE_QUARTER_FADE,
            animation.getValueFromNanos(LoadingMotionTestExpectations.THREE_QUARTER_FADE_NANOS),
            LoadingMotionTestExpectations.FADE_TOLERANCE
        )
        assertEquals(1f, animation.getValueFromNanos(LoadingMotionTestExpectations.FADE_END_NANOS), 0f)
        assertEquals(1f, animation.getValueFromNanos(LoadingMotionTestExpectations.SETTLED_FADE_NANOS), 0f)
    }

    @Test
    fun `rotation starts immediately and repeats every 1200ms with Android default easing`() {
        val animation =
            TargetBasedAnimation(
                LoteriasCaixaDSLoadingMotion.rotationSpec,
                Float.VectorConverter,
                0f,
                LoadingMotionTestExpectations.FULL_ROTATION_DEGREES
            )
        repeat(LoadingMotionTestExpectations.OBSERVED_CYCLES) { cycle ->
            mapOf(
                0 to 0f,
                LoadingMotionTestExpectations.QUARTER_ROTATION_MILLIS to LoadingMotionTestExpectations.EXPECTED_QUARTER_ROTATION_DEGREES,
                LoadingMotionTestExpectations.HALF_ROTATION_MILLIS to LoadingMotionTestExpectations.EXPECTED_HALF_ROTATION_DEGREES,
                LoadingMotionTestExpectations.THREE_QUARTER_ROTATION_MILLIS to
                    LoadingMotionTestExpectations.EXPECTED_THREE_QUARTER_ROTATION_DEGREES
            ).forEach { (time, expected) ->
                assertEquals(
                    expected,
                    animation.getValueFromNanos(
                        (cycle * LoadingMotionTestExpectations.ROTATION_PERIOD_MILLIS + time) *
                            LoadingMotionTestExpectations.NANOS_PER_MILLI
                    ),
                    LoadingMotionTestExpectations.ROTATION_TOLERANCE
                )
            }
        }
    }

    @Test
    fun `easing matches accelerate decelerate at endpoints and stays monotonic`() {
        val samples = (0..LoadingMotionTestExpectations.EASING_SAMPLE_INTERVALS).map {
            LoteriasCaixaDSLoadingMotion.easing.transform(it / LoadingMotionTestExpectations.EASING_SAMPLE_DIVISOR)
        }
        assertEquals(0f, samples.first(), 0f)
        assertEquals(1f, samples.last(), 0f)
        samples.zipWithNext().forEach { (before, after) -> check(after >= before) }
        assertEquals(
            LoadingMotionTestExpectations.EXPECTED_MIDPOINT,
            samples[LoadingMotionTestExpectations.MIDPOINT_SAMPLE_INDEX],
            LoadingMotionTestExpectations.FADE_TOLERANCE
        )
    }
}

private object LoadingMotionTestExpectations {
    const val EARLY_FADE_SAMPLE_MILLIS = 100L
    const val LAST_TRANSPARENT_SAMPLE_MILLIS = 499L
    const val FADE_START_MILLIS = 500L
    const val NANOS_PER_MILLI = 1_000_000
    const val EXPECTED_QUARTER_FADE = .1464466f
    const val QUARTER_FADE_NANOS: Long = 550_000_000
    const val FADE_TOLERANCE = .00001f
    const val EXPECTED_MIDPOINT = .5f
    const val HALF_FADE_NANOS: Long = 600_000_000
    const val EXPECTED_THREE_QUARTER_FADE = .8535534f
    const val THREE_QUARTER_FADE_NANOS: Long = 650_000_000
    const val FADE_END_NANOS: Long = 700_000_000
    const val SETTLED_FADE_NANOS: Long = 5_000_000_000
    const val FULL_ROTATION_DEGREES = 360f
    const val OBSERVED_CYCLES = 3
    const val QUARTER_ROTATION_MILLIS = 300
    const val EXPECTED_QUARTER_ROTATION_DEGREES = 52.72078f
    const val HALF_ROTATION_MILLIS = 600
    const val EXPECTED_HALF_ROTATION_DEGREES = 180f
    const val THREE_QUARTER_ROTATION_MILLIS = 900
    const val EXPECTED_THREE_QUARTER_ROTATION_DEGREES = 307.2792f
    const val ROTATION_PERIOD_MILLIS = 1_200L
    const val ROTATION_TOLERANCE = .0001f
    const val EASING_SAMPLE_INTERVALS = 100
    const val EASING_SAMPLE_DIVISOR = 100f
    const val MIDPOINT_SAMPLE_INDEX = 50
}
