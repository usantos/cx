package br.gov.caixa.loterias.feature.notifications.data.datasource.push

import android.content.Context
import br.gov.caixa.loterias.feature.notifications.data.push.PushFirebaseOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/** Mantém a inicialização do SDK Firebase na fronteira substituível de tokens do ambiente de execução. */
fun interface PushTokenDataSource {
    fun initialize(context: Context, options: PushFirebaseOptions): (suspend () -> String?)?
}

object PushTokenDataSourceImpl : PushTokenDataSource {
    override fun initialize(context: Context, options: PushFirebaseOptions): (suspend () -> String?) {
        synchronized(FirebaseApp::class.java) {
            FirebaseApp.getApps(context).firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }
                ?: FirebaseApp.initializeApp(
                    context,
                    FirebaseOptions.Builder()
                        .setApplicationId(options.applicationId)
                        .setApiKey(options.apiKey)
                        .setProjectId(options.projectId)
                        .setGcmSenderId(options.senderId).build()
                )
        }
        val messaging = FirebaseMessaging.getInstance()
        return {
            suspendCancellableCoroutine { continuation ->
                messaging.token.addOnCompleteListener { task ->
                    if (continuation.isActive) continuation.resume(if (task.isSuccessful) task.result else null)
                }
            }
        }
    }
}
