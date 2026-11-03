package br.gov.caixa.loterias.feature.onboarding.data.config

import android.content.Context
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Mantém a configuração tipada e descarta publicações inválidas, inclusive após atualização remota. */
class FirebaseAppModulesConfigurationRepositoryImpl internal constructor(
    private val source: AppModulesConfigurationSource,
    scope: CoroutineScope,
    private val cache: AppModulesConfigurationCache? = null,
) : AppModulesConfigurationRepository,
    AutoCloseable {
    constructor(context: Context, options: FirebaseOptions, scope: CoroutineScope) : this(
        FirebaseAppModulesConfigurationSource { featureConfigRemote(context.applicationContext, options) },
        scope,
        PreferencesAppModulesConfigurationCache(context)
    )

    private val mutableConfiguration = MutableStateFlow(readCachedConfiguration(cache))
    override val configuration = mutableConfiguration.asStateFlow()
    private val deliveryLock = Any()
    private val refreshMutex = Mutex()
    private var closed = false
    private val observation = source.observe(::publishActivatedValue)
    private val initialization = scope.launch {
        preserveConfigurationOnFailure {
            refreshMutex.withLock {
                source.initialize()
                publishActivatedValue()
            }
        }
    }

    override suspend fun refresh() = preserveConfigurationOnFailure {
        refreshMutex.withLock {
            if (synchronized(deliveryLock) { closed }) return@withLock
            source.initialize()
            source.refresh()
            publishActivatedValue()
        }
    }

    private fun publishActivatedValue() {
        synchronized(deliveryLock) {
            if (closed) return
            val value = runCatching { source.read() }.getOrNull() ?: return
            val parsed = parseAppModulesConfiguration(value) ?: return
            val persisted = cache?.let { runCatching { it.write(value) }.getOrDefault(false) } ?: true
            if (persisted) mutableConfiguration.value = parsed
        }
    }

    override fun close() {
        val shouldClose = synchronized(deliveryLock) {
            if (closed) {
                false
            } else {
                closed = true
                true
            }
        }
        if (shouldClose) {
            initialization.cancel()
            observation.close()
        }
    }
}

private suspend fun preserveConfigurationOnFailure(action: suspend () -> Unit) {
    runCatching { action() }.onFailure { failure ->
        if (failure is CancellationException) throw failure
        // Falha de rede ou cache não substitui a configuração válida já publicada.
    }
}

private fun readCachedConfiguration(cache: AppModulesConfigurationCache?): AppModulesConfiguration {
    val encoded = runCatching { cache?.read() }.getOrNull()
    return encoded?.let(::parseAppModulesConfiguration) ?: AppModulesConfiguration()
}
