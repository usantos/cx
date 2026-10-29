package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidConfigurationViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RapidConfigurationCoordinationTest {
    private val dispatcher = StandardTestDispatcher()
    private val modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val configuration = RapidBetConfiguration(
        BigDecimal.TEN,
        BigDecimal("100"),
        BigDecimal.ZERO,
        emptySet(),
        emptySet(),
        listOf(modality)
    )

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `conflicting number filters preserve the selection and explain the legacy restriction`() = runTest(dispatcher) {
        val vm = RapidConfigurationViewModel(
            GetRapidBetConfigurationUseCase { ServiceResult.Success(configuration) },
            GetLotteryModalitiesUseCase { AppResult.Success(listOf(modality)) },
            SaveRapidBetConfigurationUseCase { ServiceResult.Success(it) },
            FixedCoroutineDispatchers(dispatcher)
        )
        advanceUntilIdle()
        vm.toggleRequiredNumber(12)
        vm.toggleExcludedNumber(12)
        assertEquals(setOf(12), vm.state.value.requiredNumbers)
        assertEquals(emptySet<Int>(), vm.state.value.excludedNumbers)
        assertEquals("Um número não pode ser marcado como obrigatório e proibido.", vm.state.value.warningMessage)
        vm.dismissMessage()
        vm.toggleExcludedNumber(13)
        vm.toggleRequiredNumber(13)
        assertEquals(setOf(12), vm.state.value.requiredNumbers)
        assertEquals(setOf(13), vm.state.value.excludedNumbers)
        assertEquals("Um número não pode ser marcado como obrigatório e proibido.", vm.state.value.warningMessage)
    }

    @Test fun `configuration failure still loads modalities in the original order`() = runTest(dispatcher) {
        val calls = mutableListOf<String>()
        val vm = RapidConfigurationViewModel(
            GetRapidBetConfigurationUseCase {
                calls += "configuration"
                ServiceResult.Failed(AppError.Network)
            },
            GetLotteryModalitiesUseCase {
                calls += "modalities"
                AppResult.Success(listOf(modality))
            },
            SaveRapidBetConfigurationUseCase { error("Unexpected submission") },
            FixedCoroutineDispatchers(dispatcher)
        )
        advanceUntilIdle()
        assertEquals(listOf("configuration", "modalities"), calls)
        assertFalse(vm.state.value.loading)
        assertEquals("Não foi possível carregar o Rapidão. Verifique sua internet.", vm.state.value.errorMessage)
    }

    @Test fun `save captures form before dispatch and updates the original public state`() = runTest(dispatcher) {
        var submitted: RapidBetConfiguration? = null
        val vm = RapidConfigurationViewModel(
            GetRapidBetConfigurationUseCase { ServiceResult.Success(configuration) },
            GetLotteryModalitiesUseCase { AppResult.Success(listOf(modality)) },
            SaveRapidBetConfigurationUseCase {
                submitted = it
                ServiceResult.Success(it)
            },
            FixedCoroutineDispatchers(dispatcher)
        )
        val state = vm.state
        advanceUntilIdle()
        vm.setMinimumPrize(BigDecimal.TEN)
        vm.save()
        vm.setMinimumPrize(BigDecimal("20"))
        advanceUntilIdle()
        assertEquals(BigDecimal.TEN, submitted?.minimumPrize)
        assertEquals(BigDecimal("20"), state.value.minimumPrize)
        assertTrue(state.value.saved)
        assertSame(state, vm.state)
    }

    @Test fun `clearing viewmodel cancels a suspended load before modalities are queried`() = runTest(dispatcher) {
        var cancelled = false
        var modalityCalls = 0
        val vm = RapidConfigurationViewModel(
            GetRapidBetConfigurationUseCase {
                try {
                    awaitCancellation()
                } finally {
                    cancelled = true
                }
            },
            GetLotteryModalitiesUseCase {
                modalityCalls++
                AppResult.Success(listOf(modality))
            },
            SaveRapidBetConfigurationUseCase { error("Unexpected submission") },
            FixedCoroutineDispatchers(dispatcher)
        )
        val store = ViewModelStore().apply { put("rapid", vm) }
        runCurrent()
        store.clear()
        advanceUntilIdle()
        assertTrue(cancelled)
        assertEquals(0, modalityCalls)
        assertFalse(vm.state.value.saved)
    }
}
