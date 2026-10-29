package br.gov.caixa.loterias.apostas.runtime.launcher

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconConfigurationRepository
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetLauncherIconPresetUseCase
import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class LauncherIconScheduleRuntimeTest {
    @Test fun refreshPreparesConfigurationBeforeResolvingApplyingAndScheduling() = runTest {
        val events = mutableListOf<String>()
        val next = Instant.parse("2026-11-01T03:00:00Z")
        var preset = LauncherIconPreset.DEFAULT
        val repository = object : LauncherIconConfigurationRepository {
            override val activePreset get() = preset
        }
        val runtime = LauncherIconScheduleRuntime(
            initialize = {
                events += "preparar"
                preset = LauncherIconPreset.OUTUBRO_ROSA
            },
            getPreset = GetLauncherIconPresetUseCase(repository),
            nextEvaluationAt = { next },
            applyPreset = {
                events += "aplicar:$it"
                LauncherIconApplyResult.APPLIED
            },
            schedule = { events += "agendar:$it" }
        )

        assertEquals(LauncherIconApplyResult.APPLIED, runtime.refresh())
        assertEquals(listOf("preparar", "aplicar:OUTUBRO_ROSA", "agendar:$next"), events)
    }

    @Test fun unchangedDeferredAndFailedApplicationsStillScheduleTheNextDay() = runTest {
        val next = Instant.parse("2026-12-01T03:00:00Z")
        val repository = object : LauncherIconConfigurationRepository {
            override val activePreset = LauncherIconPreset.DEFAULT
        }
        for (result in LauncherIconApplyResult.entries) {
            val scheduled = mutableListOf<Instant>()
            val runtime = LauncherIconScheduleRuntime(
                {},
                GetLauncherIconPresetUseCase(repository),
                { next },
                { result },
                scheduled::add
            )
            assertEquals(result, runtime.refresh())
            assertEquals(listOf(next), scheduled)
        }
    }

    @Test fun scheduleOnlyDoesNotInitializeOrApplyAndReadsTheCurrentDeadline() {
        var next = Instant.parse("2026-11-01T03:00:00Z")
        val scheduled = mutableListOf<Instant>()
        val repository = object : LauncherIconConfigurationRepository {
            override val activePreset get() = error("Não deve resolver o ícone ao apenas agendar")
        }
        val runtime = LauncherIconScheduleRuntime(
            { error("Não deve preparar o Firebase ao apenas agendar") },
            GetLauncherIconPresetUseCase(repository),
            { next },
            { error("Não deve reaplicar o ícone ao apenas agendar") },
            scheduled::add
        )
        runtime.scheduleNextUpdate()
        next = Instant.parse("2026-11-02T03:00:00Z")
        runtime.scheduleNextUpdate()
        assertEquals(listOf(Instant.parse("2026-11-01T03:00:00Z"), next), scheduled)
    }

    @Test fun failedInitializationStillSchedulesAndPreservesTheFailureIncludingCancellation() = runTest {
        val next = Instant.parse("2026-11-01T03:00:00Z")
        val repository = object : LauncherIconConfigurationRepository {
            override val activePreset = LauncherIconPreset.DEFAULT
        }
        for (failure in listOf(IOException("Cache indisponível"), CancellationException("Cancelado"))) {
            val scheduled = mutableListOf<Instant>()
            val runtime = LauncherIconScheduleRuntime(
                { throw failure },
                GetLauncherIconPresetUseCase(repository),
                { next },
                { error("Não deve aplicar antes da preparação") },
                scheduled::add
            )
            try {
                runtime.refresh()
                fail("A falha de preparação deve ser preservada")
            } catch (actual: IOException) {
                assertSame(failure, actual)
            } catch (actual: CancellationException) {
                assertSame(failure, actual)
            }
            assertEquals(listOf(next), scheduled)
        }
    }
}
