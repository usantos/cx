package br.gov.caixa.loterias.core.presentation

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.presentation.viewmodel.DestinationSnapshotViewModel
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DestinationSnapshotViewModelTest {
    @Test fun olderNonCooperativeResponseCannotReplaceLatestSnapshot() {
        val requests = mutableListOf<Continuation<String?>>()
        val vm = DestinationSnapshotViewModel({ suspendCoroutine<String?> { requests += it } }, Dispatchers.Unconfined)
        val owner = ViewModelStore().apply { put("snapshot", vm) }
        try {
            vm.refresh()
            vm.refresh()
            assertEquals(2, requests.size)
            requests.last().resume("latest")
            assertEquals("latest", vm.state.value)
            requests.first().resume("old")
            assertEquals("latest", vm.state.value)
        } finally {
            owner.clear()
        }
    }

    @Test fun failedRefreshClearsThePreviousSnapshot() {
        var result: String? = "first"
        val vm = DestinationSnapshotViewModel({ result }, Dispatchers.Unconfined)
        val owner = ViewModelStore().apply { put("snapshot", vm) }
        try {
            vm.refresh()
            assertEquals("first", vm.state.value)
            result = null
            vm.refresh()
            assertNull(vm.state.value)
        } finally {
            owner.clear()
        }
    }

    @Test fun clearingOwnerPreventsLatePublicationAndNewLoads() {
        val requests = mutableListOf<Continuation<String?>>()
        val vm = DestinationSnapshotViewModel({ suspendCoroutine<String?> { requests += it } }, Dispatchers.Unconfined)
        val owner = ViewModelStore().apply { put("snapshot", vm) }
        vm.refresh()
        assertEquals(1, requests.size)
        owner.clear()
        requests.single().resume("late")
        vm.refresh()
        assertNull(vm.state.value)
        assertEquals(1, requests.size)
    }
}
