package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconConfigurationRepository
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetLauncherIconPresetUseCase
import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class LauncherIconScheduleAndroidTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Test fun nextDailyAlarmIsInexactIdleAllowedAndReplacesItsImmutableExplicitIntent() {
        val scheduler = AndroidLauncherIconScheduler(context)
        val first = Instant.parse("2026-11-01T03:00:00Z")
        val second = first.plusSeconds(SECONDS_PER_DAY)
        scheduler.schedule(first)
        scheduler.schedule(second)

        val alarms = shadowOf(context.getSystemService(AlarmManager::class.java)).scheduledAlarms
        assertEquals(1, alarms.size)
        val alarm = alarms.single()
        assertEquals(AlarmManager.RTC_WAKEUP, alarm.type)
        assertEquals(second.toEpochMilli(), alarm.triggerAtMs)
        assertTrue(alarm.isAllowWhileIdle)
        assertTrue("O alarme diário deve permitir uma janela inexata", alarm.windowLengthMs != 0L)
        val pending = shadowOf(alarm.operation)
        assertTrue(pending.isBroadcast)
        assertTrue(pending.isImmutable)
        assertTrue(pending.flags and PendingIntent.FLAG_UPDATE_CURRENT != 0)
        assertEquals(ComponentName(context, LauncherIconScheduleReceiver::class.java), pending.savedIntent.component)
        assertEquals(LauncherIconScheduleReceiver.ACTION_EVALUATE, pending.savedIntent.action)
    }

    @Test fun manifestRegistersOnlyPrivateBootAndClockEventsWithoutExactAlarmPermission() {
        val receiver = context.packageManager.getReceiverInfo(ComponentName(context, LauncherIconScheduleReceiver::class.java), 0)
        assertFalse(receiver.exported)
        assertTrue(receiver.enabled)
        val requested = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions.orEmpty().toSet()
        assertTrue("android.permission.RECEIVE_BOOT_COMPLETED" in requested)
        assertFalse("android.permission.SCHEDULE_EXACT_ALARM" in requested)
        assertFalse("android.permission.USE_EXACT_ALARM" in requested)
        for (action in listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED
        )) {
            val registered = context.packageManager.queryBroadcastReceivers(Intent(action).setPackage(context.packageName), 0)
            assertTrue(
                "O calendário deve retomar após $action",
                registered.any { it.activityInfo.name == LauncherIconScheduleReceiver::class.java.name }
            )
        }
        val implicitDaily = context.packageManager.queryBroadcastReceivers(
            Intent(LauncherIconScheduleReceiver.ACTION_EVALUATE).setPackage(context.packageName),
            0
        )
        assertFalse(implicitDaily.any { it.activityInfo.name == LauncherIconScheduleReceiver::class.java.name })
    }

    @Test fun receiverRefreshesAsynchronouslyAndFinishesOnSuccessAndFailure() = runTest {
        for (failure in listOf(
            null,
            IOException("Cache indisponível"),
            IllegalArgumentException("Parâmetro inválido"),
            IllegalStateException("Serviço indisponível"),
            SecurityException("Acesso negado")
        )) {
            var calls = 0
            val receiver = LauncherIconScheduleReceiver(
                refresh = { receivedContext ->
                    assertEquals(context, receivedContext)
                    calls++
                    if (failure != null) throw failure
                },
                dispatcher = StandardTestDispatcher(testScheduler)
            )
            context.registerReceiver(receiver, IntentFilter(LauncherIconScheduleReceiver.ACTION_EVALUATE), Context.RECEIVER_NOT_EXPORTED)
            try {
                context.sendBroadcast(Intent(LauncherIconScheduleReceiver.ACTION_EVALUATE).setPackage(context.packageName))
                shadowOf(Looper.getMainLooper()).idle()
                val shadow = shadowOf(receiver)
                assertTrue(shadow.wentAsync())
                val pending = shadowOf(requireNotNull(shadow.originalPendingResult))
                assertFalse(pending.future.isDone)
                testScheduler.runCurrent()
                assertEquals(1, calls)
                assertTrue("O PendingResult deve ser finalizado mesmo em falha", pending.future.isDone)
            } finally {
                context.unregisterReceiver(receiver)
            }
        }
    }

    @Test fun receiverIgnoresUnrelatedActions() = runTest {
        var calls = 0
        val receiver = LauncherIconScheduleReceiver({ calls++ }, StandardTestDispatcher(testScheduler))
        receiver.onReceive(context, Intent("outra.acao"))
        testScheduler.runCurrent()
        assertEquals(0, calls)
        assertFalse(shadowOf(receiver).wentAsync())
    }

    @Test fun routineSchedulingFailureDoesNotCrashStartupOrHideTheAppliedResult() = runTest {
        val repository = object : LauncherIconConfigurationRepository {
            override val activePreset = LauncherIconPreset.DEFAULT
        }
        for (failure in listOf(
            IOException("Cache indisponível"),
            IllegalArgumentException("Parâmetro inválido"),
            IllegalStateException("Serviço indisponível"),
            SecurityException("Acesso negado")
        )) {
            var attempts = 0
            val runtime = LauncherIconScheduleRuntime(
                {},
                GetLauncherIconPresetUseCase(repository),
                { Instant.parse("2026-11-01T03:00:00Z") },
                { LauncherIconApplyResult.APPLIED },
                {
                    attempts++
                    throw failure
                }
            )
            runtime.scheduleNextUpdate()
            assertEquals(LauncherIconApplyResult.APPLIED, runtime.refresh())
            assertEquals(2, attempts)
        }
    }

    @Test fun resumeFailureDoesNotCancelTheCompositionScopeOrBlockFutureResumes() = runTest {
        val owner = object : LifecycleOwner {
            override val lifecycle: Lifecycle get() = error("Não deve consultar o proprietário")
        }
        for (failure in listOf(
            IOException("Cache indisponível"),
            IllegalArgumentException("Parâmetro inválido"),
            IllegalStateException("Serviço indisponível"),
            SecurityException("Acesso negado")
        )) {
            var calls = 0
            val observer = LauncherIconCalendarObserver(this) {
                calls++
                throw failure
            }
            observer.onResume(owner)
            repeat(2) {
                observer.onPause(owner)
                observer.onResume(owner)
                testScheduler.runCurrent()
            }
            assertEquals(2, calls)
        }
    }
}

private const val SECONDS_PER_DAY = 86_400L
