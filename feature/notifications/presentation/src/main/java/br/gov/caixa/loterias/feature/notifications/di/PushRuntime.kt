package br.gov.caixa.loterias.feature.notifications.di

import android.app.Activity
import android.app.Application
import android.content.Context
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushRuntime @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessions: SessionTokenStore,
    private val register: RegisterPushDevice,
    private val hostConfiguration: HostFeatureConfiguration,
) {
    private val delegate = AndroidPushRuntime(
        context,
        sessions,
        sessions.observeIdentities(),
        register,
        hostConfiguration.mockBackendEnabled,
        br.gov.caixa.loterias.feature.notifications.data.push.PushFirebaseOptions(
            hostConfiguration.firebaseAppId,
            hostConfiguration.firebaseApiKey,
            hostConfiguration.firebaseProjectId,
            hostConfiguration.firebaseSenderId
        )
    )
    fun start(application: Application) = delegate.start(application)
    fun onNewToken(token: String) = delegate.onNewToken(token)
    fun showRemoteMessage(message: com.google.firebase.messaging.RemoteMessage, activity: Class<out Activity>, icon: Int) =
        br.gov.caixa.loterias.feature.notifications.data.push.showRemoteMessage(context, message, activity, icon)
}
