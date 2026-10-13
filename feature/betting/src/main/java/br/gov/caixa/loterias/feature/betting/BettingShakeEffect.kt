package br.gov.caixa.loterias.feature.betting

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog

@Composable
internal fun BettingShakeEffect(enabled: Boolean, state: BettingGameUiState, viewModel: BettingGameViewModel, blocked: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current
    val owner = LocalLifecycleOwner.current
    val latest by rememberUpdatedState(state)
    val latestBlocked by rememberUpdatedState(blocked)
    val detector = remember { ShakeDetector() }
    var confirmation by remember { mutableStateOf(false) }
    DisposableEffect(enabled, owner) {
        val manager = context.getSystemService(SensorManager::class.java)
        val sensor = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            override fun onSensorChanged(event: SensorEvent) {
                if (!enabled || !view.hasWindowFocus() || latestBlocked || confirmation || latest.loading || latest.addedToCart || latest.mode != BetEntryMode.MANUAL || latest.game?.modality?.id == LOTOGOL_ID) {
                    detector.reset(); return
                }
                if (detector.detect(event.values[0], event.values[1], event.values[2], SystemClock.elapsedRealtime())) {
                    if (latest.shakeComplete()) confirmation = true else viewModel.fillShake(false)
                }
            }
        }
        fun update() {
            manager?.unregisterListener(listener)
            detector.reset()
            if (enabled && owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && sensor != null) {
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
            } else confirmation = false
        }
        val observer = LifecycleEventObserver { _, _ -> update() }
        owner.lifecycle.addObserver(observer)
        update()
        onDispose { owner.lifecycle.removeObserver(observer); manager?.unregisterListener(listener); detector.reset() }
    }
    if (confirmation) CaixaDSLegacyDialog(
        title = "Atenção", message = "Você perderá a aposta atual, deseja realmente realizar outra aposta?",
        positiveLabel = "Sim", negativeLabel = "Não",
        onPositive = { viewModel.fillShake(true); confirmation = false; detector.block(SystemClock.elapsedRealtime()) },
        onNegative = { confirmation = false; detector.block(SystemClock.elapsedRealtime()) },
    )
}
