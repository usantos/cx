package br.gov.caixa.loterias.feature.betting

/** Two opposite acceleration peaks, matching the legacy thresholds and cooldown. */
internal class ShakeDetector {
    private var firstPeak = -1L
    private var lastShake = -1L
    private var peak = floatArrayOf(0f, 0f, 0f)
    fun detect(x: Float, y: Float, z: Float, time: Long): Boolean {
        if (lastShake >= 0 && time - lastShake < 1500) return false
        if (x * x + y * y + z * z < 2.7f * 2.7f * 9.81f * 9.81f) return false
        if (firstPeak >= 0 && time - firstPeak in 80..500 && x * peak[0] + y * peak[1] + z * peak[2] < 0) {
            lastShake = time; firstPeak = -1; return true
        }
        if (firstPeak < 0 || time - firstPeak > 500) { firstPeak = time; peak = floatArrayOf(x, y, z) }
        return false
    }
    fun block(time: Long) { firstPeak = -1; lastShake = time }
    fun reset() { firstPeak = -1; lastShake = -1 }
}
