package br.gov.caixa.loterias.apostas.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoteriasMessagingService : FirebaseMessagingService() {
    @Inject lateinit var runtime: PushRuntime

    override fun onNewToken(token: String) { runtime.onNewToken(token) }

    override fun onMessageReceived(message: RemoteMessage) {
        // CX notifications are system banners. Popup/read state belongs to account verification/inbox.
        val notification = message.notification
        if (notification != null) {
            if (notification.body != null) PushNotifications.show(this, notification.title, notification.body)
        } else PushNotifications.show(this, message.data["title"], message.data["message"])
    }
}
