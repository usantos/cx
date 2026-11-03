package br.gov.caixa.loterias.feature.onboarding.data.config

import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import java.io.Closeable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppModulesConfigurationRepositoryTest {
    @Test fun startsDisabledThenPublishesCachedConfigurationWithoutFetching() = runTest {
        val source = ModuleSource()
        source.value = encoded(true)
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope)
        assertFalse(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        runCurrent()
        assertTrue(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        assertEquals(0, source.refreshes)
        repository.close()
    }

    @Test fun activatedChangesKeepLastValidValueAndCanDisableAgain() = runTest {
        val source = ModuleSource()
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope)
        runCurrent()
        source.publish(encoded(true))
        assertTrue(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        source.publish("invalid")
        assertTrue(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        source.publish(encoded(false))
        assertFalse(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        repository.close()
    }

    @Test fun refreshRecoversWithoutRealtimeAndKeepsCacheOnFailures() = runTest {
        val source = ModuleSource()
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope)
        runCurrent()
        source.value = encoded(true)
        repository.refresh()
        assertTrue(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        source.failure = IllegalStateException("offline")
        source.value = encoded(false)
        repository.refresh()
        assertTrue(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        assertEquals(2, source.refreshes)
        repository.close()
    }

    @Test fun closeIsIdempotentAndSuppressesLateEventsAndRefresh() = runTest {
        val source = ModuleSource()
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope)
        runCurrent()
        repository.close()
        repository.close()
        source.publish(encoded(true))
        repository.refresh()
        assertFalse(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        assertEquals(1, source.closes)
        assertEquals(0, source.refreshes)
    }

    @Test fun cancellationOfRefreshPropagatesToItsCaller() = runTest {
        val source = ModuleSource()
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope)
        runCurrent()
        source.failure = CancellationException("cancelled")
        val pending = async { repository.refresh() }
        runCurrent()
        assertTrue(pending.isCancelled)
        repository.close()
    }

    @Test fun closingBeforeCacheIsReadyCancelsInitialization() = runTest {
        val source = ModuleSource()
        source.initialization = CompletableDeferred()
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope)
        runCurrent()
        repository.close()
        source.value = encoded(true)
        source.initialization!!.complete(Unit)
        runCurrent()
        assertFalse(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
    }

    @Test fun restartAfterInvalidPublicationPreservesExplicitFalseBeforeRemoteInitialization() = runTest {
        val stored = CachedDocument()
        val source = ModuleSource()
        val disabled = """{"schema_version":1,"modules":{"results":{"enabled":false},"support_faq":{"enabled":true}}}"""
        val first = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope, TestCache(stored))
        runCurrent()
        source.publish(disabled)
        source.publish("""{"schema_version":2,"modules":{}}""")
        first.close()

        val restartedSource = ModuleSource().apply {
            value = "invalid"
            initialization = CompletableDeferred()
        }
        val restarted = FirebaseAppModulesConfigurationRepositoryImpl(restartedSource, backgroundScope, TestCache(stored))
        assertFalse(restarted.configuration.value.isEnabled(AppModule.RESULTS))
        assertTrue(restarted.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        restartedSource.initialization!!.complete(Unit)
        runCurrent()
        assertFalse(restarted.configuration.value.isEnabled(AppModule.RESULTS))
        assertEquals(listOf(disabled), stored.writes)
        assertEquals(0, restartedSource.refreshes)
        restarted.close()
    }

    @Test fun corruptedDurableCacheUsesDefaultsWithoutAcceptingInvalidActivatedJson() = runTest {
        val cache = TestCache(CachedDocument().apply { value = "invalid" })
        val source = ModuleSource().apply { value = """{"schema_version":2,"modules":{}}""" }
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope, cache)
        runCurrent()
        AppModule.entries.forEach { module ->
            assertEquals(module.key, module.defaultEnabled, repository.configuration.value.isEnabled(module))
        }
        repository.close()
    }

    @Test fun unreadableDurableCacheDoesNotPreventValidRemoteRecovery() = runTest {
        val stored = CachedDocument()
        val cache = TestCache(stored).apply { failRead = true }
        val source = ModuleSource().apply { value = encoded(true) }
        val repository = FirebaseAppModulesConfigurationRepositoryImpl(source, backgroundScope, cache)
        runCurrent()
        assertTrue(repository.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        assertEquals(listOf(encoded(true)), stored.writes)
        repository.close()
    }

    private class CachedDocument {
        var value: String? = null
        val writes = mutableListOf<String>()
    }

    private class TestCache(private val stored: CachedDocument) : AppModulesConfigurationCache {
        var failRead = false
        override fun read(): String? {
            check(!failRead) { "cache indisponível" }
            return stored.value
        }
        override fun write(value: String): Boolean {
            stored.value = value
            stored.writes += value
            return true
        }
    }

    private class ModuleSource : AppModulesConfigurationSource {
        var value = ""
        var refreshes = 0
        var closes = 0
        var failure: Exception? = null
        var initialization: CompletableDeferred<Unit>? = null
        private var observer: () -> Unit = {}
        override fun read(): String = value
        override suspend fun initialize() {
            initialization?.await()
        }
        override suspend fun refresh() {
            refreshes++
            failure?.let { throw it }
        }
        override fun observe(onChanged: () -> Unit): Closeable {
            observer = onChanged
            return Closeable { closes++ }
        }
        fun publish(encoded: String) {
            value = encoded
            observer()
        }
    }

    private fun encoded(enabled: Boolean) = """{"schema_version":1,"modules":{"support_faq":{"enabled":$enabled}}}"""
}
