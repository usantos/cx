package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalDataResumeTest : PersonalDataViewModelFixture() {
    @Test fun `first resume reuses initial profile fetch and subsequent clean resume refreshes server data`() =
        runTest(dispatcherRule.dispatcher) {
            var calls = 0
            val vm = viewModel(profileResult = {
                calls++
                AppResult.Success(profile().copy(name = "Pessoa $calls", authorizedDailyLimit = BigDecimal(DAILY_LIMIT_PER_FETCH * calls)))
            })
            vm.onResume()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, calls)
            vm.onResume()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(2, calls)
            assertEquals("Pessoa 2", vm.state.value.name)
            assertEquals(BigDecimal("400"), vm.state.value.authorizedDailyLimit)
        }

    @Test fun `resume preserves each unsaved field and resolved zip location without refetching`() = runTest(dispatcherRule.dispatcher) {
        val edits: List<(PersonalDataViewModel) -> Unit> = listOf(
            { it.changeGender(Gender.FEMALE) },
            { it.changeDailyLimit("R$ 120,50") },
            { it.toggleNotifications() },
            { it.changeZipCode("01001000") },
            { it.changeZipCode("700") }
        )
        edits.forEach { edit ->
            var calls = 0
            val vm = viewModel(profileResult = {
                calls++
                AppResult.Success(profile())
            }, resolve = {
                AppResult.Success(ZipCodeLocation(MunicipalityId(1, 2, RESOLVED_MUNICIPALITY_ID), "SP", "São Paulo"))
            })
            vm.onResume()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            edit(vm)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            val draft = vm.state.value
            vm.onResume()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, calls)
            assertEquals(draft, vm.state.value)
        }
    }

    @Test fun `reverting editable fields allows the next resume refresh`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val vm = viewModel(profileResult = {
            calls++
            AppResult.Success(profile())
        })
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.changeGender(Gender.FEMALE)
        vm.changeGender(Gender.MALE)
        vm.changeDailyLimit("R$ 150,00")
        vm.changeDailyLimit("100,00")
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
    }

    @Test fun `resumes cannot start a deferred profile load before its entry gate`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val vm = viewModel(deferInitialLoad = true, profileResult = {
            calls++
            AppResult.Success(profile())
        })
        vm.onResume()
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, calls)
        vm.load()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
    }

    @Test fun `edits made during resume refresh survive its late server response`() = runTest(dispatcherRule.dispatcher) {
        val refreshed = CompletableDeferred<AppResult<AccountProfile>>()
        var calls = 0
        val vm = viewModel(profileResult = {
            calls++
            if (calls == 1) AppResult.Success(profile()) else refreshed.await()
        })
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.changeDailyLimit("R$ 120,50")
        refreshed.complete(AppResult.Success(profile().copy(name = "Atualizado")))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("R$ 120,50", vm.state.value.dailyLimit)
        assertEquals("Pessoa", vm.state.value.name)
        assertFalse(vm.state.value.loading)
    }
}

private const val DAILY_LIMIT_PER_FETCH = 200

private const val RESOLVED_MUNICIPALITY_ID = 3L
