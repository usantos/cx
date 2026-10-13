package br.gov.caixa.loterias.apostas

import android.content.Context
import br.gov.caixa.loterias.core.keymanagement.RemotePublicKeySetSource
import br.gov.caixa.loterias.core.keymanagement.SignedKeySetEnvelope
import br.gov.caixa.loterias.core.keymanagement.SignedKeySetStore
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseRemotePublicKeySetSource(
    private val remoteConfig: FirebaseRemoteConfig,
    private val gson: Gson = Gson(),
) : RemotePublicKeySetSource {
    override suspend fun fetch(): SignedKeySetEnvelope? {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(FETCH_TIMEOUT_SECONDS)
                .setMinimumFetchIntervalInSeconds(MINIMUM_FETCH_INTERVAL_SECONDS)
                .build(),
        ).awaitOrNull() ?: return null
        remoteConfig.fetchAndActivate().awaitOrNull() ?: return null
        val value = remoteConfig.getString(PARAMETER_NAME).trim()
        if (value.isEmpty()) return null
        return runCatching { gson.fromJson(value, SignedKeySetEnvelope::class.java) }.getOrNull()
    }

    companion object {
        const val PARAMETER_NAME = "tls_pinning_keyset_v1"
        private const val FETCH_TIMEOUT_SECONDS = 10L
        private const val MINIMUM_FETCH_INTERVAL_SECONDS = 3_600L

        fun create(context: Context): FirebaseRemotePublicKeySetSource {
            val app = FirebaseApp.getApps(context).firstOrNull { it.name == FIREBASE_APP_NAME }
                ?: FirebaseApp.initializeApp(
                    context,
                    FirebaseOptions.Builder()
                        .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                        .setApiKey(BuildConfig.FIREBASE_API_KEY)
                        .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                        .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
                        .build(),
                    FIREBASE_APP_NAME,
                )
            return FirebaseRemotePublicKeySetSource(FirebaseRemoteConfig.getInstance(app))
        }

        private const val FIREBASE_APP_NAME = "pinning-remote-config"
    }
}

class PreferencesSignedKeySetStore(
    context: Context,
    private val gson: Gson = Gson(),
) : SignedKeySetStore {
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

private suspend fun <T> Task<T>.awaitOrNull(): T? = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (!continuation.isActive) return@addOnCompleteListener
        continuation.resume(if (task.isSuccessful) task.result else null)
    }
}
