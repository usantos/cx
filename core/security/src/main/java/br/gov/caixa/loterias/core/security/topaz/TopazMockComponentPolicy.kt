package br.gov.caixa.loterias.core.security.topaz

interface ComponentOverrideController {
    fun state(name: String): Int
    fun setState(name: String, state: Int)
}

interface ComponentOverrideBackup {
    fun read(): Map<String, Int>?
    fun save(states: Map<String, Int>): Boolean
    fun clear(): Boolean
}

/** O retrato deve ser persistido antes de qualquer alteração no PM; somente as mudanças registradas são restauradas. */
class TopazMockComponentPolicy(
    private val components: List<String>,
    private val controller: ComponentOverrideController,
    private val backup: ComponentOverrideBackup,
) {
    fun apply(simulated: Boolean) {
        val previous = backup.read()
        check(previous == null || (previous.keys.all { it in components } && previous.values.all { it in 0..4 })) {
            "Registro de componentes de simulação inválido"
        }
        if (simulated) {
            val original = previous.orEmpty().toMutableMap()
            components.forEach { name ->
                val state = controller.state(name)
                if (state != DISABLED && name !in original) original[name] = state
            }
            if (original.isNotEmpty()) {
                check(backup.save(original)) { "Não foi possível preservar os estados dos componentes de simulação" }
            }
            components.forEach { name ->
                if (controller.state(name) != DISABLED) controller.setState(name, DISABLED)
            }
        } else if (previous != null) {
            previous.forEach { (name, originalState) ->
                if (controller.state(name) != originalState) controller.setState(name, originalState)
            }
            check(backup.clear()) { "Não foi possível finalizar a restauração dos componentes de simulação" }
        }
    }

    companion object { const val DISABLED = 2 }
}

val topazMockComponents = listOf(
    "br.com.topaz.heartbeat.calleridwatchlist.CallActionActivity",
    "br.com.topaz.heartbeat.securecall.SecureCallActivity",
    "br.com.topaz.heartbeat.notification.NotificationListener",
    "br.com.topaz.heartbeat.sosus.ScreenSupervisorIntentService",
    "br.com.topaz.heartbeat.broadcast.OnBootScheduler",
    "br.com.topaz.heartbeat.broadcast.CronListener",
    "br.com.topaz.heartbeat.broadcast.CallListener",
    "androidx.work.impl.background.systemalarm.SystemAlarmService",
    "androidx.work.impl.background.systemjob.SystemJobService",
    "androidx.work.impl.foreground.SystemForegroundService",
    "androidx.work.impl.utils.ForceStopRunnable\$BroadcastReceiver",
    "androidx.work.impl.background.systemalarm.ConstraintProxy\$BatteryChargingProxy",
    "androidx.work.impl.background.systemalarm.ConstraintProxy\$BatteryNotLowProxy",
    "androidx.work.impl.background.systemalarm.ConstraintProxy\$StorageNotLowProxy",
    "androidx.work.impl.background.systemalarm.ConstraintProxy\$NetworkStateProxy",
    "androidx.work.impl.background.systemalarm.RescheduleReceiver",
    "androidx.work.impl.background.systemalarm.ConstraintProxyUpdateReceiver",
    "androidx.work.impl.diagnostics.DiagnosticsReceiver",
)
