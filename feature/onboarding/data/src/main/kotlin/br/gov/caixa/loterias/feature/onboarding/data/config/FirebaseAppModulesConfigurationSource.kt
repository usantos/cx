package br.gov.caixa.loterias.feature.onboarding.data.config

import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

internal class FirebaseAppModulesConfigurationSource(remoteProvider: () -> FirebaseRemoteConfig) : AppModulesConfigurationSource {
    private val remote by lazy(remoteProvider)

    override fun read(): String = remote.getString(KEY)

    override suspend fun initialize() = remote.ensureInitialized().awaitModulesCompletion()

    override suspend fun refresh() {
        remote.fetch(0).awaitModulesCompletion()
        remote.activate().awaitModulesCompletion()
        publishFeatureConfigActivations(remote)
    }

    override fun observe(onChanged: () -> Unit): Closeable {
        val closed = AtomicBoolean(false)
        val activations = observeFeatureConfigActivations(remote) { if (!closed.get()) onChanged() }
        val registration = runCatching {
            remote.addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    if (closed.get() || KEY !in configUpdate.updatedKeys) return
                    remote.activate().addOnCompleteListener { task ->
                        if (task.isSuccessful && !closed.get()) publishFeatureConfigActivations(remote)
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) = Unit
            })
        }.onFailure { activations.close() }.getOrThrow()
        return Closeable {
            if (closed.compareAndSet(false, true)) {
                try {
                    registration.remove()
                } finally {
                    activations.close()
                }
            }
        }
    }

    private companion object {
        const val KEY = "app_modules"
    }
}

private suspend fun Task<*>.awaitModulesCompletion(): Unit = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (continuation.isActive) {
            if (task.isSuccessful) {
                continuation.resume(Unit)
            } else {
                continuation.resumeWithException(task.exception ?: IllegalStateException("Configuração não concluída"))
            }
        }
    }
}
