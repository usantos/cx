package br.gov.caixa.loterias.feature.notifications.di

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.notifications.data.datasource.push.PushTokenDataSource
import br.gov.caixa.loterias.feature.notifications.data.datasource.push.PushTokenDataSourceImpl
import br.gov.caixa.loterias.feature.notifications.data.push.PushFirebaseOptions
import br.gov.caixa.loterias.feature.notifications.data.push.PushNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** O ambiente de composição controla as repetições no escopo do aplicativo: login, retomada de Activity e renovação do token. */
class AndroidPushRuntime private constructor(
    private val context: Context,
    private val sessions: AuthenticatedSessionIdentityProvider,
    private val identityChanges: Flow<AuthenticatedSessionIdentity?>,
    private val register: RegisterPushDevice,
    private val simulated: Boolean,
    private val options: PushFirebaseOptions,
    private val tokenSource: PushTokenDataSource,
) : Application.ActivityLifecycleCallbacks {
    constructor(
        context: Context,
        sessions: AuthenticatedSessionIdentityProvider,
        identityChanges: Flow<AuthenticatedSessionIdentity?>,
        register: RegisterPushDevice,
        simulated: Boolean,
        options: PushFirebaseOptions,
    ) : this(context, sessions, identityChanges, register, simulated, options, PushTokenDataSourceImpl)

    internal companion object {
        fun withTokenSource(
            context: Context,
            sessions: AuthenticatedSessionIdentityProvider,
            identityChanges: Flow<AuthenticatedSessionIdentity?>,
            register: RegisterPushDevice,
            simulated: Boolean,
            options: PushFirebaseOptions,
            tokenSource: PushTokenDataSource,
        ) = AndroidPushRuntime(context, sessions, identityChanges, register, simulated, options, tokenSource)
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile private var tokenProvider: (suspend () -> String?)? = null
    private var started = false
    private val registration = br.gov.caixa.loterias.feature.notifications.domain.push.PushRegistrationCoordinator(
        sessions,
        register::invoke
    )

    fun start(application: Application) {
        if (started) return
        started = true
        PushNotifications.createChannel(context)
        if (simulated) return
        tokenProvider = runCatching { tokenSource.initialize(context, options) }.getOrNull()
        application.registerActivityLifecycleCallbacks(this)
        scope.launch {
            identityChanges.collectLatest { identity ->
                if (identity != null) refresh()
            }
        }
    }

    fun onNewToken(token: String) {
        if (simulated) return
        scope.launch { registration.onNewToken(token) }
    }

    private suspend fun refresh() {
        registration.refresh token@{
            tokenProvider?.invoke()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        scope.launch { refresh() }
    }
    override fun onActivityCreated(activity: Activity, state: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, state: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
