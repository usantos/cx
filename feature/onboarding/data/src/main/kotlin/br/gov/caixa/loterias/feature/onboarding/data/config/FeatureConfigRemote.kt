package br.gov.caixa.loterias.feature.onboarding.data.config

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

private const val APP_NAME = FirebaseApp.DEFAULT_APP_NAME

internal fun featureConfigRemote(context: Context, options: FirebaseOptions): FirebaseRemoteConfig = synchronized(FirebaseApp::class.java) {
    val applicationContext = context.applicationContext
    val app = FirebaseApp.getApps(applicationContext).firstOrNull { it.name == APP_NAME }
        ?: FirebaseApp.initializeApp(applicationContext, options, APP_NAME)
    FirebaseRemoteConfig.getInstance(app)
}
