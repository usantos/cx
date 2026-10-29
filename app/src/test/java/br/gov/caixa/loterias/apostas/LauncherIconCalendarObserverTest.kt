package br.gov.caixa.loterias.apostas

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.apostas.runtime.launcher.LauncherIconCalendarObserver
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LauncherIconCalendarObserverTest {
    private val owner = object : LifecycleOwner {
        override val lifecycle: Lifecycle get() = error("O observador não precisa consultar o proprietário")
    }

    @Test fun initialResumeKeepsStartupSelectionAndLaterResumesReadTheCurrentCalendar() = runTest {
        var selected = LauncherIconPreset.OUTUBRO_ROSA
        val applied = mutableListOf<LauncherIconPreset>()
        val observer = LauncherIconCalendarObserver(this) { applied += selected }
        observer.onResume(owner)
        runCurrent()
        assertEquals(emptyList<LauncherIconPreset>(), applied)
        selected = LauncherIconPreset.NOVEMBRO_AZUL
        observer.onPause(owner)
        observer.onResume(owner)
        runCurrent()
        selected = LauncherIconPreset.DEFAULT
        observer.onPause(owner)
        observer.onResume(owner)
        runCurrent()
        assertEquals(listOf(LauncherIconPreset.NOVEMBRO_AZUL, LauncherIconPreset.DEFAULT), applied)
    }

    @Test fun destroyedOwnerCannotStartAnotherCalendarRefresh() = runTest {
        val scope = CoroutineScope(Job() + StandardTestDispatcher(testScheduler))
        var calls = 0
        val observer = LauncherIconCalendarObserver(scope) { calls++ }
        observer.onResume(owner)
        observer.onPause(owner)
        scope.cancel()
        observer.onResume(owner)
        runCurrent()
        assertEquals(0, calls)
    }
}
