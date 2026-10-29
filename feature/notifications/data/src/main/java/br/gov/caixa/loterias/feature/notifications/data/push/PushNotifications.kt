package br.gov.caixa.loterias.feature.notifications.data.push

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import br.gov.caixa.loterias.feature.notifications.data.formatting.localizedMessage as messageText

object PushNotifications {
    private const val CHANNEL_ID = "CanalLoteriasId"
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL_ID, messageText("loterias_2a60a4"), NotificationManager.IMPORTANCE_HIGH).apply {
                description = messageText("canal_loterias_8d3249")
                setSound(null, null)
            }
        )
    }

    // A verificação de permissão em execução abaixo protege esta chamada; o Lint não a rastreia pelo ContextCompat.
    @SuppressLint("MissingPermission")
    fun show(context: Context, title: String?, body: String?, launchActivity: Class<out android.app.Activity>, smallIcon: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        createChannel(context)
        val intent = Intent(context, launchActivity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(smallIcon)
            .setContentTitle(title).setContentText(body).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true).setContentIntent(pending).build()
        NotificationManagerCompat.from(context).notify(0, notification)
    }
}
