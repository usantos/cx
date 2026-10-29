package br.gov.caixa.loterias.core.security.keyset

import android.content.Context
import br.gov.caixa.loterias.core.common.config.ConfigurationActivationEvents
import br.gov.caixa.loterias.core.keymanagement.model.RemotePublicKeySetSource
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetEnvelope
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetStore
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseRemotePublicKeySetSource(private val remoteConfig: FirebaseRemoteConfig, private val gson: Gson = Gson()) :
    RemotePublicKeySetSource {
    override suspend fun fetch(): SignedKeySetEnvelope? {
        if (!remoteConfig.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setFetchTimeoutInSeconds(FETCH_TIMEOUT_SECONDS)
                    .setMinimumFetchIntervalInSeconds(MINIMUM_FETCH_INTERVAL_SECONDS)
                    .build()
            ).awaitSuccessful()
        ) {
            return null
        }
        if (!remoteConfig.fetchAndActivate().awaitSuccessful()) return null
        ConfigurationActivationEvents.publish(remoteConfig)
        val value = remoteConfig.getString(PARAMETER_NAME).trim()
        if (value.isEmpty()) return null
        return runCatching { gson.fromJson(value, SignedKeySetEnvelope::class.java) }.getOrNull()
    }

    companion object {
        const val PARAMETER_NAME = "tls_pinning_keyset"
        private const val FETCH_TIMEOUT_SECONDS = 10L
        private const val MINIMUM_FETCH_INTERVAL_SECONDS = 3_600L

        fun create(context: Context, options: FirebaseKeySetOptions): FirebaseRemotePublicKeySetSource =
            synchronized(FirebaseApp::class.java) {
                val app = FirebaseApp.getApps(context).firstOrNull { it.name == FIREBASE_APP_NAME }
                    ?: FirebaseApp.initializeApp(
                        context,
                        FirebaseOptions.Builder()
                            .setApplicationId(options.applicationId)
                            .setApiKey(options.apiKey)
                            .setProjectId(options.projectId)
                            .setGcmSenderId(options.senderId)
                            .build(),
                        FIREBASE_APP_NAME
                    )
                FirebaseRemotePublicKeySetSource(FirebaseRemoteConfig.getInstance(app))
            }

        private const val FIREBASE_APP_NAME = FirebaseApp.DEFAULT_APP_NAME
    }
}

class PreferencesSignedKeySetStore(context: Context, private val gson: Gson = Gson()) : SignedKeySetStore {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun read(): SignedKeySetEnvelope? = preferences.getString(KEY_ENVELOPE, null)
        ?.let { encoded -> runCatching { gson.fromJson(encoded, SignedKeySetEnvelope::class.java) }.getOrNull() }

    override fun write(envelope: SignedKeySetEnvelope) {
        preferences.edit().putString(KEY_ENVELOPE, gson.toJson(envelope)).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "signed_tls_keyset"
        const val KEY_ENVELOPE = "last_accepted_envelope"
    }
}

internal suspend fun <T> Task<T>.awaitSuccessful(): Boolean = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (!continuation.isActive) return@addOnCompleteListener
        continuation.resume(task.isSuccessful)
    }
}
