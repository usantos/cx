package br.gov.caixa.loterias.feature.onboarding.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.onboarding.data.config.featureConfigRemote
import br.gov.caixa.loterias.feature.onboarding.data.config.observeFeatureConfigActivations
import br.gov.caixa.loterias.feature.onboarding.data.config.parseLauncherIconSchedule
import br.gov.caixa.loterias.feature.onboarding.data.config.publishFeatureConfigActivations
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconSchedule
import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconCalendarConfiguration
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import java.io.Closeable
import java.time.Clock
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseLauncherIconConfigurationRepositoryImpl private constructor(
    private val readActivatedValue: () -> String,
    private val clock: Clock,
    private val prepareActivatedValue: suspend (Int) -> Unit,
    private val observeActivatedValue: (() -> Unit) -> Closeable = { Closeable {} },
) : LauncherIconCalendarConfiguration {
    internal constructor(readActivatedValue: () -> String, clock: Clock = Clock.systemUTC()) :
        this(readActivatedValue, clock, {})

    constructor(context: Context, options: FirebaseOptions, clock: Clock = Clock.systemUTC()) :
        this(lazy { featureConfigRemote(context.applicationContext, options) }, clock)

    private constructor(remote: Lazy<FirebaseRemoteConfig>, clock: Clock) : this(
        { remote.value.getString(KEY) },
        clock,
        { defaultsResource ->
            remote.value.ensureInitialized().awaitCompletion()
            remote.value.setDefaultsAsync(defaultsResource).awaitCompletion()
        },
        { onChanged -> observeLauncherIconChanges(remote.value, onChanged) }
    )

    val activeSchedule: LauncherIconSchedule
        get() = parseLauncherIconSchedule(runCatching { readActivatedValue() }.getOrDefault(""))

    override val activePreset: LauncherIconPreset
        get() = activeSchedule.presetAt(clock.instant())

    override fun nextEvaluationAt(): Instant = activeSchedule.nextEvaluationAt(clock.instant())

    override fun observe(onChanged: () -> Unit): Closeable = observeActivatedValue(onChanged)

    /** Aguarda o cache local e os defaults, sem buscar ou ativar configurações de rede. */
    override suspend fun initialize(defaultsResource: Int) = prepareActivatedValue(defaultsResource)

    companion object {
        const val KEY = "app_launcher_icon"
    }
}

private suspend fun Task<*>.awaitCompletion() = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener {
        if (continuation.isActive) continuation.resume(Unit)
    }
}

private fun observeLauncherIconChanges(remote: FirebaseRemoteConfig, onChanged: () -> Unit): Closeable {
    val deliveryLock = Any()
    val closed = AtomicBoolean(false)
    val previous = AtomicReference(runCatching { remote.getString(FirebaseLauncherIconConfigurationRepositoryImpl.KEY) }.getOrNull())
    val publishChangedValue = {
        synchronized(deliveryLock) {
            if (!closed.get()) {
                val value = runCatching { remote.getString(FirebaseLauncherIconConfigurationRepositoryImpl.KEY) }.getOrNull()
                if (value != null && previous.getAndSet(value) != value && !closed.get()) onChanged()
            }
        }
    }
    val activations = observeFeatureConfigActivations(remote, publishChangedValue)
    val registration = runCatching {
        remote.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (closed.get() || FirebaseLauncherIconConfigurationRepositoryImpl.KEY !in configUpdate.updatedKeys) return
                remote.activate().addOnCompleteListener { task ->
                    // A ativação pode ter sido concluída pela busca ao retomar; basta ler o valor já aceito.
                    if (task.isSuccessful) publishFeatureConfigActivations(remote)
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
