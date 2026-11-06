package br.gov.caixa.loterias.core.moduledelivery.data

import android.content.Context
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import com.google.android.gms.tasks.Task
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Observa a entrega de cada módulo e preserva downloads em andamento quando a entrada deixa de ser observada. */
class PlayModuleInstallationRepositoryImpl(private val manager: SplitInstallManager) : ModuleInstallationRepository {
    constructor(context: Context) : this(SplitInstallManagerFactory.create(context.applicationContext))
    private val sessions = ConcurrentHashMap<AppModule, PlayModuleInstallationSession>()
    override fun install(module: AppModule): Flow<ModuleDeliveryState> =
        sessions.computeIfAbsent(module) { PlayModuleInstallationSession(manager, it.splitName) }.install()
}

private class PlayModuleInstallationSession(private val manager: SplitInstallManager, private val moduleName: String) {

    private val requestMutex = Mutex()
    private var requestedSession: Int? = null
    private var pendingRequest: Task<Int>? = null

    fun install(): Flow<ModuleDeliveryState> = callbackFlow {
        trySend(ModuleDeliveryState.Checking)
        if (moduleName in manager.installedModules) {
            trySend(ModuleDeliveryState.Ready)
            close()
            return@callbackFlow
        }
        val observedSession = AtomicInteger(NO_SESSION)
        val latestEvents = ConcurrentHashMap<Int, SplitInstallSessionState>()
        fun publish(session: SplitInstallSessionState) {
            if (session.sessionId() != observedSession.get() || moduleName !in session.moduleNames()) return
            val state = session.toDeliveryState()
            trySend(state)
            if (state == ModuleDeliveryState.Ready || state == ModuleDeliveryState.Failed) close()
        }
        val listener = SplitInstallStateUpdatedListener { session ->
            if (moduleName in session.moduleNames()) {
                latestEvents[session.sessionId()] = session
                publish(session)
            }
        }
        manager.registerListener(listener)
        try {
            runCatching {
                requestMutex.withLock {
                    val session = if (moduleName in manager.installedModules) null else selectSession()
                    if (session == null) {
                        trySend(ModuleDeliveryState.Ready)
                        close()
                    } else {
                        observedSession.set(session.sessionId())
                        publish(latestEvents[session.sessionId()] ?: session)
                    }
                }
            }.onFailure { failure ->
                if (failure is CancellationException) throw failure
                trySend(ModuleDeliveryState.Failed)
                close()
            }
            awaitClose {}
        } finally {
            manager.unregisterListener(listener)
        }
    }.catch { failure ->
        if (failure is CancellationException) throw failure
        emit(ModuleDeliveryState.Failed)
    }

    private suspend fun selectSession(): SplitInstallSessionState? {
        val cached = requestedSession?.let { id ->
            runCatching { manager.getSessionState(id).awaitDeliveryResult() }.getOrElse { failure ->
                if (failure is CancellationException) throw failure
                null
            }
        }
        if (cached != null && cached.isActiveFaqSession()) return cached
        val active = manager.sessionStates.awaitDeliveryResult().firstOrNull { it.isActiveFaqSession() }
        if (active != null) {
            requestedSession = active.sessionId()
            pendingRequest = null
            return active
        }
        // O Task sobrevive ao cancelamento da coleta para não solicitar o mesmo download duas vezes.
        val request = pendingRequest ?: manager.startInstall(
            SplitInstallRequest.newBuilder().addModule(moduleName).build()
        ).also { pendingRequest = it }
        val id = try {
            request.awaitDeliveryResult()
        } finally {
            if (request.isComplete && !request.isSuccessful) pendingRequest = null
        }
        requestedSession = id
        pendingRequest = null
        if (moduleName in manager.installedModules) return null
        return manager.getSessionState(id).awaitDeliveryResult()
    }

    private fun SplitInstallSessionState.isActiveFaqSession(): Boolean = moduleName in moduleNames() && !hasTerminalStatus()

    private companion object {
        const val NO_SESSION = -1
    }
}

private fun SplitInstallSessionState.toDeliveryState(): ModuleDeliveryState = when (status()) {
    SplitInstallSessionStatus.INSTALLED -> ModuleDeliveryState.Ready

    SplitInstallSessionStatus.DOWNLOADING -> ModuleDeliveryState.Downloading(bytesDownloaded(), totalBytesToDownload())

    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> ModuleDeliveryState.Confirmation(sessionId())

    SplitInstallSessionStatus.PENDING,
    SplitInstallSessionStatus.DOWNLOADED,
    SplitInstallSessionStatus.INSTALLING,
    SplitInstallSessionStatus.CANCELING,
    -> ModuleDeliveryState.Pending

    else -> ModuleDeliveryState.Failed
}

private suspend fun <T> Task<T>.awaitDeliveryResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener(deliveryExecutor) { task ->
        if (continuation.isActive) {
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: IllegalStateException("Entrega não concluída"))
            }
        }
    }
}

private val deliveryExecutor = Executor { it.run() }
