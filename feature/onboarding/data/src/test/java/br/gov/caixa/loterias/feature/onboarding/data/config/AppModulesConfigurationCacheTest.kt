package br.gov.caixa.loterias.feature.onboarding.data.config

import android.content.Context
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import java.io.Closeable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@OptIn(ExperimentalCoroutinesApi::class)
class AppModulesConfigurationCacheTest {
    @Test
    fun recreatedCacheReadsTheCommittedPublicDocument() {
        val context = RuntimeEnvironment.getApplication()
        val name = "modules-cache-adapter-test"
        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        preferences.edit().clear().commit()
        val encoded = """{"schema_version":1,"modules":{"results":{"enabled":false}}}"""
        assertTrue(PreferencesAppModulesConfigurationCache(preferences).write(encoded))
        val recreated = PreferencesAppModulesConfigurationCache(context.getSharedPreferences(name, Context.MODE_PRIVATE))
        assertEquals(encoded, recreated.read())
    }

    @Test
    fun durableLastValidDocumentSurvivesRepositoryRecreationAndInvalidRemoteValue() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val name = "modules-cache-repository-test"
        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        preferences.edit().clear().commit()
        val encoded = """{"schema_version":1,"modules":{"retail":{"enabled":false}}}"""
        val first = FirebaseAppModulesConfigurationRepositoryImpl(
            FixedSource(encoded),
            backgroundScope,
            PreferencesAppModulesConfigurationCache(preferences)
        )
        runCurrent()
        assertFalse(first.configuration.value.isEnabled(AppModule.RETAIL))
        first.close()

        val restarted = FirebaseAppModulesConfigurationRepositoryImpl(
            FixedSource("invalid"),
            backgroundScope,
            PreferencesAppModulesConfigurationCache(context.getSharedPreferences(name, Context.MODE_PRIVATE))
        )
        assertFalse(restarted.configuration.value.isEnabled(AppModule.RETAIL))
        runCurrent()
        assertFalse(restarted.configuration.value.isEnabled(AppModule.RETAIL))
        assertFalse(restarted.configuration.value.isEnabled(AppModule.SUPPORT_FAQ))
        assertTrue(restarted.configuration.value.isEnabled(AppModule.HOME))
        restarted.close()
    }

    private class FixedSource(private val value: String) : AppModulesConfigurationSource {
        override fun read(): String = value
        override suspend fun initialize() = Unit
        override suspend fun refresh() = Unit
        override fun observe(onChanged: () -> Unit): Closeable = Closeable {}
    }
}
