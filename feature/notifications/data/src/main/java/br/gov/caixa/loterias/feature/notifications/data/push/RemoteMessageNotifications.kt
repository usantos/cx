package br.gov.caixa.loterias.feature.notifications.data.push

import android.content.Context
import com.google.firebase.messaging.RemoteMessage

fun showRemoteMessage(context: Context, message: RemoteMessage, launchActivity: Class<out android.app.Activity>, smallIcon: Int) {
    // Notificações CX são faixas do sistema. O estado de leitura e dos pop-ups pertence à verificação da conta/caixa de entrada.
    val notification = message.notification
    if (notification != null) {
        if (notification.body != null) PushNotifications.show(context, notification.title, notification.body, launchActivity, smallIcon)
    } else {
        PushNotifications.show(context, message.data["title"], message.data["message"], launchActivity, smallIcon)
    }
}
