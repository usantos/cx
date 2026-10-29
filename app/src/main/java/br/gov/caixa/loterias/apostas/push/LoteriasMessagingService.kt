package br.gov.caixa.loterias.apostas.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import br.gov.caixa.loterias.feature.notifications.di.PushRuntime

@AndroidEntryPoint
class LoteriasMessagingService : FirebaseMessagingService() {
    @Inject lateinit var runtime: PushRuntime

    override fun onNewToken(token: String) { runtime.onNewToken(token) }

    override fun onMessageReceived(message: RemoteMessage) {
        runtime.showRemoteMessage(message, br.gov.caixa.loterias.apostas.MainActivity::class.java, br.gov.caixa.loterias.apostas.R.drawable.trevo)
    }
}
