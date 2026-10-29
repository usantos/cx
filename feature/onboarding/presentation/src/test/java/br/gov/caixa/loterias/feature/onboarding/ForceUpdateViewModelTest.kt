package br.gov.caixa.loterias.feature.onboarding

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule
import br.gov.caixa.loterias.feature.onboarding.domain.repository.ForceUpdateRepository
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetActiveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.RefreshForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.state.ForceUpdateState
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.ForceUpdateViewModel
import java.io.Closeable
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ForceUpdateViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun initialFetchCompletesBeforeLoginCanBeShown() {
        val source = Source().apply { pending = CompletableDeferred() }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        main.dispatcher.scheduler.runCurrent()
        assertEquals(ForceUpdateState.CHECKING, model.state.value)
        source.pending!!.complete(false)
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ForceUpdateState.READY, model.state.value)
    }

    @Test fun trueBlocksAndRealtimeFalseReleasesTheGate() {
        val source = Source().apply { cached = true }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        assertEquals(ForceUpdateState.REQUIRED, model.state.value)
        main.dispatcher.scheduler.advanceUntilIdle()
        source.changed(false)
        main.dispatcher.scheduler.runCurrent()
        assertEquals(ForceUpdateState.READY, model.state.value)
        source.changed(true)
        main.dispatcher.scheduler.runCurrent()
        assertEquals(ForceUpdateState.REQUIRED, model.state.value)
    }

    @Test fun offlineFailureDoesNotClearAnActivatedMandatoryUpdate() {
        val source = Source().apply {
            cached = true
            fail = true
        }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ForceUpdateState.REQUIRED, model.state.value)
    }

    @Test fun absentFlagAndOfflineFirstLaunchKeepTheFalseDefault() {
        val source = Source().apply { fail = true }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ForceUpdateState.READY, model.state.value)
    }

    @Test fun resumeRefreshDoesNotDuplicateAnActiveRequest() {
        val source = Source().apply { pending = CompletableDeferred() }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        main.dispatcher.scheduler.runCurrent()
        model.refresh()
        model.refresh()
        main.dispatcher.scheduler.runCurrent()
        assertEquals(1, source.requests)
        source.pending!!.complete(false)
        main.dispatcher.scheduler.advanceUntilIdle()
        model.refresh()
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, source.requests)
    }

    @Test fun clearingDisposesObservationCancelsRequestAndIgnoresLateCallback() {
        val source = Source().apply {
            cached = true
            pending = CompletableDeferred()
        }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        val store = ViewModelStore().apply { put("force-update", model) }
        main.dispatcher.scheduler.runCurrent()
        val lateCallback = source.callback()
        store.clear()
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, source.closed)
        assertEquals(1, source.cancelled)
        lateCallback(false)
        assertEquals(ForceUpdateState.REQUIRED, model.state.value)
    }

    @Test fun cancellationNeverPublishesReadyOverAnActivatedUpdate() {
        val source = Source().apply {
            cached = true
            cancel = true
        }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ForceUpdateState.REQUIRED, model.state.value)
    }

    @Test fun observationRegistrationFailureStillRefreshesActiveValue() {
        val source = Source().apply { observationFails = true }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, source.requests)
        assertEquals(ForceUpdateState.READY, model.state.value)
    }

    @Test fun refreshCannotOverwriteANewerBackgroundActivation() {
        val source = Source().apply { pending = CompletableDeferred() }
        val model = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(source),
            RefreshForceUpdateUseCase(source),
            ObserveForceUpdateUseCase(source)
        )
        source.beforeRefreshReturn = {
            val worker = Thread { source.changed(true) }
            worker.start()
            worker.join(LIVE_TIMEOUT_MILLIS)
            check(!worker.isAlive)
        }
        main.dispatcher.scheduler.runCurrent()
        source.pending!!.complete(false)
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ForceUpdateState.REQUIRED, model.state.value)
    }

    @Test fun foregroundRecoveryReadsTwoChangesWithoutStreamEvents() {
        val source = Source()
        val model = model(source)
        model.startForegroundUpdates()
        main.dispatcher.scheduler.runCurrent()
        try {
            source.remote = true
            main.dispatcher.scheduler.advanceTimeBy(RECOVERY_INTERVAL_MILLIS - 1)
            main.dispatcher.scheduler.runCurrent()
            assertEquals(1, source.requests)
            assertEquals(ForceUpdateState.READY, model.state.value)
            main.dispatcher.scheduler.advanceTimeBy(1)
            main.dispatcher.scheduler.runCurrent()
            assertEquals(2, source.requests)
            assertEquals(ForceUpdateState.REQUIRED, model.state.value)
            source.remote = false
            main.dispatcher.scheduler.advanceTimeBy(RECOVERY_INTERVAL_MILLIS)
            main.dispatcher.scheduler.runCurrent()
            assertEquals(REQUESTS_AFTER_TWO_RECOVERIES, source.requests)
            assertEquals(ForceUpdateState.READY, model.state.value)
        } finally {
            model.stopForegroundUpdates()
        }
    }

    @Test fun pausingStopsRecoveryAndResumingRefreshesImmediately() {
        val source = Source()
        val model = model(source)
        model.startForegroundUpdates()
        main.dispatcher.scheduler.runCurrent()
        model.stopForegroundUpdates()
        main.dispatcher.scheduler.advanceTimeBy(RECOVERY_INTERVAL_MILLIS * MULTIPLE_RECOVERY_INTERVALS)
        main.dispatcher.scheduler.runCurrent()
        assertEquals(1, source.requests)
        source.remote = true
        model.startForegroundUpdates()
        main.dispatcher.scheduler.runCurrent()
        try {
            assertEquals(2, source.requests)
            assertEquals(ForceUpdateState.REQUIRED, model.state.value)
        } finally {
            model.stopForegroundUpdates()
        }
    }

    @Test fun repeatedForegroundStartsAndSlowFetchDoNotOverlapRequests() {
        val source = Source().apply { pending = CompletableDeferred() }
        val model = model(source)
        model.startForegroundUpdates()
        model.startForegroundUpdates()
        main.dispatcher.scheduler.runCurrent()
        try {
            main.dispatcher.scheduler.advanceTimeBy(RECOVERY_INTERVAL_MILLIS * MULTIPLE_RECOVERY_INTERVALS)
            main.dispatcher.scheduler.runCurrent()
            assertEquals(1, source.requests)
            source.pending!!.complete(false)
            source.pending = null
            main.dispatcher.scheduler.runCurrent()
            main.dispatcher.scheduler.advanceTimeBy(RECOVERY_INTERVAL_MILLIS)
            main.dispatcher.scheduler.runCurrent()
            assertEquals(2, source.requests)
        } finally {
            model.stopForegroundUpdates()
        }
    }

    @Test fun recoveryContinuesAfterAnOfflineFetch() {
        val source = Source().apply { fail = true }
        val model = model(source)
        model.startForegroundUpdates()
        main.dispatcher.scheduler.runCurrent()
        try {
            assertEquals(1, source.requests)
            source.fail = false
            source.remote = true
            main.dispatcher.scheduler.advanceTimeBy(RECOVERY_INTERVAL_MILLIS)
            main.dispatcher.scheduler.runCurrent()
            assertEquals(2, source.requests)
            assertEquals(ForceUpdateState.REQUIRED, model.state.value)
        } finally {
            model.stopForegroundUpdates()
        }
    }

    @Test fun clearingTheViewModelAlsoStopsForegroundRecovery() {
        val source = Source()
        val model = model(source)
        val store = ViewModelStore().apply { put("force-update", model) }
        model.startForegroundUpdates()
        main.dispatcher.scheduler.runCurrent()
        store.clear()
        main.dispatcher.scheduler.advanceTimeBy(RECOVERY_INTERVAL_MILLIS * MULTIPLE_RECOVERY_INTERVALS)
        main.dispatcher.scheduler.runCurrent()
        assertEquals(1, source.requests)
        assertEquals(1, source.closed)
    }

    private fun model(source: Source) = ForceUpdateViewModel(
        GetActiveForceUpdateUseCase(source),
        RefreshForceUpdateUseCase(source),
        ObserveForceUpdateUseCase(source)
    )

    private class Source : ForceUpdateRepository {
        var cached = false
        var remote: Boolean? = null
        var fail = false
        var cancel = false
        var observationFails = false
        var requests = 0
        var closed = 0
        var cancelled = 0
        var pending: CompletableDeferred<Boolean>? = null
        var beforeRefreshReturn: (() -> Unit)? = null
        private var listener: ((Boolean) -> Unit)? = null
        override val active: Boolean get() = cached
        override suspend fun refresh(): Boolean {
            requests++
            if (fail) throw java.io.IOException("offline fixture")
            if (cancel) throw kotlinx.coroutines.CancellationException("cancelled fixture")
            return try {
                val result = pending?.await() ?: remote ?: cached
                cached = result
                beforeRefreshReturn?.invoke()
                result
            } catch (cancellation: kotlinx.coroutines.CancellationException) {
                cancelled++
                throw cancellation
            }
        }
        override fun observe(onChanged: (Boolean) -> Unit): Closeable {
            if (observationFails) throw java.io.IOException("observation fixture")
            listener = onChanged
            return Closeable {
                closed++
                listener = null
            }
        }
        fun callback(): (Boolean) -> Unit = checkNotNull(listener)
        fun changed(value: Boolean) {
            cached = value
            listener?.invoke(value)
        }
    }
}

private const val LIVE_TIMEOUT_MILLIS = 5_000L

private const val RECOVERY_INTERVAL_MILLIS = 30_000L

private const val MULTIPLE_RECOVERY_INTERVALS = 3L
private const val REQUESTS_AFTER_TWO_RECOVERIES = 3
