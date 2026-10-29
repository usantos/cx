package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.Instant

internal class AndroidLauncherIconScheduler(context: Context) {
    private val context = context.applicationContext

    fun schedule(at: Instant) {
        val intent = Intent(context, LauncherIconScheduleReceiver::class.java)
            .setAction(LauncherIconScheduleReceiver.ACTION_EVALUATE)
        val pending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // A troca diária admite atraso do sistema e não exige permissão para alarmes exatos.
        context.getSystemService(AlarmManager::class.java)
            .setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at.toEpochMilli(), pending)
    }
}

private const val REQUEST_CODE = 4071
