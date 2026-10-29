package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.presentation.route.ShakeDetector
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShakeDetectorTest {
    @Test fun `opposite peaks trigger once with cooldown and reset`() {
        val detector = ShakeDetector()
        assertFalse(detector.detect(30f, 0f, 0f, 1000))
        assertFalse(detector.detect(-30f, 0f, 0f, 1050))
        assertTrue(detector.detect(-30f, 0f, 0f, 1100))
        assertFalse(detector.detect(30f, 0f, 0f, 1200))
        detector.reset()
        assertFalse(detector.detect(30f, 0f, 0f, 1300))
        assertTrue(detector.detect(-30f, 0f, 0f, 1400))
        detector.block(1500)
        assertFalse(detector.detect(30f, 0f, 0f, 1600))
    }

    @Test fun `gravity same direction and stale peak never trigger`() {
        val detector = ShakeDetector()
        assertFalse(detector.detect(0f, 9.81f, 0f, 0))
        assertFalse(detector.detect(30f, 0f, 0f, 1000))
        assertFalse(detector.detect(30f, 0f, 0f, 1100))
        assertFalse(detector.detect(-30f, 0f, 0f, 1800))
    }
}
