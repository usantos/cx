package br.gov.caixa.loterias.feature.onboarding.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.onboarding.data.config.featureConfigRemote
import br.gov.caixa.loterias.feature.onboarding.data.config.observeFeatureConfigActivations
import br.gov.caixa.loterias.feature.onboarding.data.config.publishFeatureConfigActivations
import br.gov.caixa.loterias.feature.onboarding.domain.repository.ForceUpdateRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseForceUpdateRepositoryImpl(context: Context, private val options: FirebaseOptions, private val defaultsResource: Int) :
    ForceUpdateRepository {
    private val context = context.applicationContext
    private val remote by lazy { featureConfigRemote(this.context, options) }

    override val active: Boolean get() = runCatching { remote.getBoolean(KEY) }.getOrDefault(false)

    override suspend fun refresh(): Boolean {
        remote.setDefaultsAsync(defaultsResource).awaitCompletion()
        remote.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(FETCH_TIMEOUT_SECONDS)
                .setMinimumFetchIntervalInSeconds(MINIMUM_FETCH_INTERVAL_SECONDS)
                .build()
        ).awaitCompletion()
        // Verifica na inicialização e no retorno ao primeiro plano; limites de frequência ou erros sem conexão preservam os valores ativados.
        if (remote.fetch(0).awaitCompletion()) remote.activate().awaitCompletion()
        publishFeatureConfigActivations(remote)
        return active
    }

    override fun observe(onChanged: (Boolean) -> Unit): Closeable {
        val deliveryLock = Any()
        val closed = AtomicBoolean(false)
        val previous = AtomicReference(active)
        val publishChangedValue = {
            synchronized(deliveryLock) {
                if (!closed.get()) {
                    val value = runCatching { remote.getBoolean(KEY) }.getOrNull()
                    if (value != null && previous.getAndSet(value) != value && !closed.get()) onChanged(value)
                }
            }
        }
        val activations = observeFeatureConfigActivations(remote, publishChangedValue)
        val registration = runCatching {
            remote.addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    if (KEY in configUpdate.updatedKeys && !closed.get()) {
                        remote.activate().addOnCompleteListener { task ->
                            if (task.isSuccessful && !closed.get()) publishFeatureConfigActivations(remote)
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) = Unit
            })
        }.getOrElse { failure ->
            activations.close()
            throw failure
        }
        return Closeable {
            if (synchronized(deliveryLock) { closed.compareAndSet(false, true) }) {
                try {
                    registration.remove()
                } finally {
                    activations.close()
                }
            }
        }
    }

    companion object {
        const val KEY = "force_update"
        private const val FETCH_TIMEOUT_SECONDS = 10L
        private const val MINIMUM_FETCH_INTERVAL_SECONDS = 3_600L
    }
}

private suspend fun Task<*>.awaitCompletion(): Boolean = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (continuation.isActive) continuation.resume(task.isSuccessful)
    }
}
