package br.gov.caixa.loterias.apostas.authentication

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.core.security.topaz.topazMockComponents
import org.junit.Assert.assertEquals
import org.junit.Test

class TopazComponentIsolationTest {
    @Test fun mockBootstrapClearsPreviouslyEnabledPMOverridesForEverySdkAndWorkEntryPoint() {
        if (BuildConfig.ENABLE_MOCK_BACKEND) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            for (name in topazMockComponents) {
                assertEquals(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    context.packageManager.getComponentEnabledSetting(ComponentName(context.packageName, name)))
            }
        }
    }

    @Suppress("DEPRECATION")
    @Test fun mockSkipsOnlyWorkInitializerAndDisablesPersistedWorkDelivery() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = context.packageManager
        val flags = PackageManager.GET_DISABLED_COMPONENTS or PackageManager.GET_META_DATA
        val startup = manager.getProviderInfo(ComponentName(context.packageName, "androidx.startup.InitializationProvider"), flags)
        assertEquals(true, startup.enabled)
        assertEquals(if (BuildConfig.ENABLE_MOCK_BACKEND) "mock-disabled" else "androidx.startup",
            startup.metaData.getString("androidx.work.WorkManagerInitializer"))
        val components = listOf(
            Triple("service", "androidx.work.impl.background.systemalarm.SystemAlarmService", "@bool/enable_system_alarm_service_default"),
            Triple("service", "androidx.work.impl.background.systemjob.SystemJobService", "@bool/enable_system_job_service_default"),
            Triple("service", "androidx.work.impl.foreground.SystemForegroundService", "@bool/enable_system_foreground_service_default"),
            Triple("receiver", "androidx.work.impl.utils.ForceStopRunnable\$BroadcastReceiver", "true"),
            Triple("receiver", "androidx.work.impl.background.systemalarm.ConstraintProxy\$BatteryChargingProxy", "false"),
            Triple("receiver", "androidx.work.impl.background.systemalarm.ConstraintProxy\$BatteryNotLowProxy", "false"),
            Triple("receiver", "androidx.work.impl.background.systemalarm.ConstraintProxy\$StorageNotLowProxy", "false"),
            Triple("receiver", "androidx.work.impl.background.systemalarm.ConstraintProxy\$NetworkStateProxy", "false"),
            Triple("receiver", "androidx.work.impl.background.systemalarm.RescheduleReceiver", "false"),
            Triple("receiver", "androidx.work.impl.background.systemalarm.ConstraintProxyUpdateReceiver", "@bool/enable_system_alarm_service_default"),
            Triple("receiver", "androidx.work.impl.diagnostics.DiagnosticsReceiver", "true"),
        )
        for ((type, name, originalDefault) in components) {
            val originalEnabled = if (originalDefault.startsWith("@bool/")) {
                context.resources.getBoolean(context.resources.getIdentifier(originalDefault.removePrefix("@bool/"), "bool", context.packageName))
            } else originalDefault.toBoolean()
            val expected = !BuildConfig.ENABLE_MOCK_BACKEND && originalEnabled
            val component = ComponentName(context.packageName, name)
            val actual = if (type == "service") manager.getServiceInfo(component, flags).enabled
                else manager.getReceiverInfo(component, flags).enabled
            assertEquals(name, expected, actual)
        }
    }

    @Suppress("DEPRECATION")
    @Test fun mergedManifestDisablesEveryTopazEntryPointOnlyInMock() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = context.packageManager
        val enabled = !BuildConfig.ENABLE_MOCK_BACKEND
        val flags = PackageManager.GET_DISABLED_COMPONENTS
        for (name in listOf("br.com.topaz.heartbeat.broadcast.OnBootScheduler",
            "br.com.topaz.heartbeat.broadcast.CronListener", "br.com.topaz.heartbeat.broadcast.CallListener")) {
            assertEquals(name, enabled, manager.getReceiverInfo(ComponentName(context.packageName, name), flags).enabled)
        }
        for (name in listOf("br.com.topaz.heartbeat.notification.NotificationListener",
            "br.com.topaz.heartbeat.sosus.ScreenSupervisorIntentService")) {
            assertEquals(name, enabled, manager.getServiceInfo(ComponentName(context.packageName, name), flags).enabled)
        }
        for (name in listOf("br.com.topaz.heartbeat.calleridwatchlist.CallActionActivity",
            "br.com.topaz.heartbeat.securecall.SecureCallActivity")) {
            assertEquals(name, enabled, manager.getActivityInfo(ComponentName(context.packageName, name), flags).enabled)
        }
    }
}
