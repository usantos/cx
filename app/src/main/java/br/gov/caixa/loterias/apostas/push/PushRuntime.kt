package br.gov.caixa.loterias.apostas.push

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.core.auth.SessionTokenStore
import br.gov.caixa.loterias.core.domain.push.RegisterPushDevice
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/** Application-scoped retries: login, each activity resume, and token refresh. */
@Singleton
class PushRuntime @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessions: SessionTokenStore,
    private val register: RegisterPushDevice,
) : Application.ActivityLifecycleCallbacks {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile private var messaging: FirebaseMessaging? = null
    private var started = false

    fun start(application: Application) {
        if (started) return
        started = true
        PushNotifications.createChannel(context)
        if (BuildConfig.ENABLE_MOCK_BACKEND) return
        messaging = runCatching {
            FirebaseApp.getApps(context).firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }
                ?: FirebaseApp.initializeApp(context, FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_API_KEY)
                    .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                    .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID).build())
            FirebaseMessaging.getInstance()
        }.getOrNull()
        application.registerActivityLifecycleCallbacks(this)
        scope.launch { sessions.observeIdentities().collectLatest { identity ->
            if (identity != null) refresh()
        } }
    }

    fun onNewToken(token: String) {
        if (BuildConfig.ENABLE_MOCK_BACKEND) return
        scope.launch { register(token) }
    }

    private suspend fun refresh() {
        // Capture before asynchronous Firebase retrieval so a replacement login cannot inherit this work.
        val identity = sessions.captureIdentity() ?: return
        val firebase = messaging ?: return
        val token = suspendCancellableCoroutine<String?> { continuation ->
            firebase.token.addOnCompleteListener { task ->
                if (continuation.isActive) continuation.resume(if (task.isSuccessful) task.result else null)
            }
        }
        if (token != null && sessions.isCurrent(identity)) register(token)
    }

    override fun onActivityResumed(activity: Activity) { scope.launch { refresh() } }
    override fun onActivityCreated(activity: Activity, state: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, state: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
