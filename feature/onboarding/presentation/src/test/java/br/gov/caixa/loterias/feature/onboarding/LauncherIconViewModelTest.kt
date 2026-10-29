package br.gov.caixa.loterias.feature.onboarding

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconCalendarConfiguration
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetLauncherIconPresetUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveLauncherIconCalendarUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.state.LauncherIconApplyOutcome
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.LauncherIconViewModel
import java.io.Closeable
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class LauncherIconViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun selectionWaitsForStartupActivationAndIsReadOnlyOnce() {
        val source = Source()
        val model = model(source)
        assertNull(model.state.value.preset)
        source.preset = LauncherIconPreset.NOVEMBRO_AZUL
        model.load()
        assertEquals(LauncherIconPreset.NOVEMBRO_AZUL, model.state.value.preset)
        source.preset = LauncherIconPreset.AGOSTO_LILAS
        model.load()
        assertEquals(LauncherIconPreset.NOVEMBRO_AZUL, model.state.value.preset)
    }

    @Test fun previewUsesTheSameParserWithoutReplacingTheActivatedValue() {
        val source = Source().apply { preset = LauncherIconPreset.OUTUBRO_ROSA }
        val model = model(source)
        model.load(" Setembro Amarelo ")
        assertEquals(LauncherIconPreset.SETEMBRO_AMARELO, model.state.value.preset)
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, source.preset)
    }

    @Test fun completionRequiresSelectionAndKeepsTheFirstNativeOutcome() {
        val model = model(Source())
        model.complete(LauncherIconApplyOutcome.APPLIED)
        assertNull(model.state.value.outcome)
        model.load()
        model.complete(LauncherIconApplyOutcome.DEFERRED)
        model.complete(LauncherIconApplyOutcome.APPLIED)
        assertEquals(LauncherIconApplyOutcome.DEFERRED, model.state.value.outcome)
        assertEquals(LauncherIconPreset.DEFAULT, model.state.value.preset)
    }

    @Test fun liveChangesPreserveTheReadyScreenAndIgnoreCompletionFromAnOlderCalendar() {
        val source = Source()
        val model = model(source)
        model.load()
        model.complete(LauncherIconApplyOutcome.APPLIED)
        val previousRevision = model.state.value.revision
        source.preset = LauncherIconPreset.OUTUBRO_ROSA
        source.observer!!.invoke()
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, model.state.value.preset)
        assertEquals(previousRevision + 1, model.state.value.revision)
        assertEquals(LauncherIconApplyOutcome.APPLIED, model.state.value.outcome)
        model.complete(LauncherIconApplyOutcome.FAILED, previousRevision)
        assertEquals(LauncherIconApplyOutcome.APPLIED, model.state.value.outcome)
        model.complete(LauncherIconApplyOutcome.UNCHANGED)
        assertEquals(LauncherIconApplyOutcome.UNCHANGED, model.state.value.outcome)
    }

    @Test fun aChangedScheduleWithTheSameIconStillRequestsNativeEvaluationAndRescheduling() {
        val source = Source()
        val model = model(source)
        model.load()
        source.observer!!.invoke()
        assertEquals(LauncherIconPreset.DEFAULT, model.state.value.preset)
        assertEquals(1L, model.state.value.revision)
        source.observer!!.invoke()
        assertEquals(2L, model.state.value.revision)
    }

    @Test fun previewsDoNotSubscribeAndClearingTheModelRemovesTheLiveObservation() {
        val previewSource = Source()
        model(previewSource).load("outubro_rosa")
        assertNull(previewSource.observer)
        val source = Source()
        val model = model(source)
        val store = ViewModelStore()
        store.put("launcher", model)
        model.load()
        model.load()
        store.clear()
        assertEquals(1, source.removals)
        assertNull(source.observer)
    }

    @Test fun initialSelectionIncludesAValueActivatedWhileTheListenerIsRegistered() {
        val source = Source()
        source.duringObserve = { source.preset = LauncherIconPreset.NOVEMBRO_AZUL }
        val model = model(source)
        model.load()
        assertEquals(LauncherIconPreset.NOVEMBRO_AZUL, model.state.value.preset)
    }

    private fun model(source: Source) = LauncherIconViewModel(
        GetLauncherIconPresetUseCase(source),
        ObserveLauncherIconCalendarUseCase(source)
    )

    private class Source : LauncherIconCalendarConfiguration {
        var preset = LauncherIconPreset.DEFAULT
        override val activePreset: LauncherIconPreset get() = preset
        var observer: (() -> Unit)? = null
        var duringObserve: (() -> Unit)? = null
        var removals = 0
        override suspend fun initialize(defaultsResource: Int) = Unit
        override fun nextEvaluationAt(): Instant = Instant.EPOCH
        override fun observe(onChanged: () -> Unit): Closeable {
            observer = onChanged
            duringObserve?.invoke()
            return Closeable {
                removals++
                observer = null
            }
        }
    }
}
