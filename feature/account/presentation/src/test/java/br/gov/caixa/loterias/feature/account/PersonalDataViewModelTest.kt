package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.presentation.screen.canUpdatePersonalData
import br.gov.caixa.loterias.feature.account.presentation.screen.dailyLimitValidationMessage
import br.gov.caixa.loterias.feature.account.presentation.screen.formatCpf
import br.gov.caixa.loterias.feature.account.presentation.screen.formatZipCode
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.parseBrazilianCurrency
import java.math.BigDecimal
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalDataViewModelTest : PersonalDataViewModelFixture() {

    @Test
    fun `loads profile and edits all mutable personal fields`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals("12345678901", viewModel.state.value.cpf)
        assertEquals("R$ 100,00", viewModel.state.value.dailyLimit)
        assertTrue(viewModel.state.value.acceptsNotifications)
        viewModel.changeGender(Gender.FEMALE)
        viewModel.changeDailyLimit("R$ 120,50")
        viewModel.toggleNotifications()
        assertEquals(Gender.FEMALE, viewModel.state.value.gender)
        assertEquals("R$ 120,50", viewModel.state.value.dailyLimit)
        assertFalse(viewModel.state.value.acceptsNotifications)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `valid zip code resolves municipality and invalid edit clears location`() = runTest(dispatcherRule.dispatcher) {
        var requestedZip = ""
        val viewModel = viewModel(resolve = {
            requestedZip = it
            AppResult.Success(location())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.changeZipCode("71.234-5679")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("71234567", requestedZip)
        assertEquals("DF", viewModel.state.value.stateCode)
        assertEquals("Brasília", viewModel.state.value.city)

        viewModel.changeZipCode("123")
        assertEquals("", viewModel.state.value.stateCode)
        assertEquals(MunicipalityId(0, 0, 0), viewModel.state.value.municipalityId)
    }

    @Test
    fun `zip lookup maps network validation and unexpected failures`() = runTest(dispatcherRule.dispatcher) {
        val errors = listOf(
            AppError.Network to "Não foi possível conectar",
            AppError.Validation("") to "Confira os dados informados.",
            AppError.Unexpected to "Ocorreu um erro inesperado"
        )
        errors.forEach { (error, expected) ->
            val viewModel = viewModel(resolve = { AppResult.Failure(error) })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            viewModel.changeZipCode("70000000")
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            if (error is AppError.Validation) {
                assertNull(viewModel.state.value.errorMessage)
                assertTrue(viewModel.state.value.zipCodeErrorMessage!!.startsWith(expected))
            } else {
                assertNull(viewModel.state.value.zipCodeErrorMessage)
                assertTrue(viewModel.state.value.errorMessage!!.startsWith(expected))
            }
            assertEquals("", viewModel.state.value.city)
        }
    }

    @Test
    fun `submits normalized values and reports backend failure`() = runTest(dispatcherRule.dispatcher) {
        var submitted: PersonalDataUpdate? = null
        var shouldFail = false
        val viewModel = viewModel(update = { value, _ ->
            if (shouldFail) {
                AppResult.Failure(AppError.Validation("Limite recusado"))
            } else {
                submitted = value
                AppResult.Success(Unit)
            }
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.changeDailyLimit("R$ 125,75")
        viewModel.submit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(BigDecimal("125.75"), submitted?.dailyLimit)
        assertTrue(viewModel.state.value.saved)

        shouldFail = true
        viewModel.load()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.submit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Limite recusado", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.saving)
    }

    @Test
    fun `invalid currency does not call update and profile failure can retry`() = runTest(dispatcherRule.dispatcher) {
        var profileCalls = 0
        var updateCalls = 0
        val viewModel = viewModel(
            profileResult = {
                profileCalls++
                if (profileCalls == 1) AppResult.Failure(AppError.Network) else AppResult.Success(profile())
            },
            update = { _, _ ->
                updateCalls++
                AppResult.Success(Unit)
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.startsWith("Não foi possível conectar"))
        viewModel.dismissError()
        assertNull(viewModel.state.value.errorMessage)
        viewModel.submit()
        assertEquals(0, updateCalls)
        viewModel.load()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.changeDailyLimit("inválido")
        viewModel.submit()
        assertEquals("Informe um limite diário válido.", viewModel.state.value.errorMessage)
        assertEquals(0, updateCalls)
    }

    @Test
    fun `deferred visitor preview does not request profile until a session is available`() = runTest(dispatcherRule.dispatcher) {
        var profileCalls = 0
        val viewModel = viewModel(
            profileResult = {
                profileCalls++
                AppResult.Success(profile())
            },
            deferInitialLoad = true
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.loading)
        assertNull(viewModel.state.value.errorMessage)
        assertEquals(0, profileCalls)

        viewModel.load()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, profileCalls)
        assertEquals("12345678901", viewModel.state.value.cpf)
    }

    @Test
    fun `recalculates authorized limit and maps failure`() = runTest(dispatcherRule.dispatcher) {
        var failure = false
        val viewModel = viewModel(recalculate = {
            if (failure) AppResult.Failure(AppError.Unexpected) else AppResult.Success(BigDecimal("350"))
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.recalculateLimit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(BigDecimal("350"), viewModel.state.value.authorizedDailyLimit)
        failure = true
        viewModel.recalculateLimit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.startsWith("Ocorreu um erro inesperado"))
    }

    @Test
    fun `currency masks and form readiness follow legacy rules`() {
        assertEquals(BigDecimal("1234.56"), parseBrazilianCurrency("R$ 1.234,56"))
        assertNull(parseBrazilianCurrency("  "))
        assertNull(parseBrazilianCurrency("x"))
        assertEquals("123.456.789-01", formatCpf("123456789012"))
        assertEquals("12345-678", formatZipCode("123456789"))
        assertEquals("123", formatCpf("123"))
        assertEquals("12345", formatZipCode("12345"))

        val ready = PersonalDataUiState(
            loading = false,
            zipCode = "70000000",
            stateCode = "DF",
            city = "Brasília",
            municipalityId = MunicipalityId(7, 1, 0),
            dailyLimit = "100"
        )
        assertTrue(canUpdatePersonalData(ready))
        assertFalse(canUpdatePersonalData(ready.copy(loading = true)))
        assertFalse(canUpdatePersonalData(ready.copy(saving = true)))
        assertFalse(canUpdatePersonalData(ready.copy(zipCode = "1")))
        assertFalse(canUpdatePersonalData(ready.copy(stateCode = "")))
        assertFalse(canUpdatePersonalData(ready.copy(city = "")))
        assertFalse(canUpdatePersonalData(ready.copy(municipalityId = MunicipalityId(0, 0, 0))))
        assertFalse(canUpdatePersonalData(ready.copy(dailyLimit = "x")))
        assertEquals(
            "O valor mínimo é R$ 10,00",
            dailyLimitValidationMessage(ready.copy(dailyLimit = "R$ 9,99", minimumDailyLimit = BigDecimal("10")))
        )
        assertEquals(
            "O valor máximo é R$ 200,00",
            dailyLimitValidationMessage(ready.copy(dailyLimit = "R$ 200,01", authorizedDailyLimit = BigDecimal("200")))
        )
        assertNull(
            dailyLimitValidationMessage(
                ready.copy(
                    dailyLimit = "R$ 100,00",
                    minimumDailyLimit = BigDecimal("10"),
                    authorizedDailyLimit = BigDecimal("200")
                )
            )
        )
        assertNull(dailyLimitValidationMessage(ready.copy(dailyLimit = "inválido")))
    }

    @Test fun `expired lease prevents queued profile read`() {
        var reads = 0
        val vm = viewModel(isSessionCurrent = { false }, profileResult = {
            reads++
            AppResult.Success(profile())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, reads)
        assertEquals("", vm.state.value.name)
    }

    @Test fun `expired lease ignores pending private profile response`() {
        var current = true
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<AccountProfile>>()
        val vm = viewModel(isSessionCurrent = { current }, profileResult = { pending.await() })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        current = false
        pending.complete(AppResult.Success(profile()))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("", vm.state.value.name)
    }

    @Test
    fun `obsolete zip responses cannot finish the current lookup`() = runTest(dispatcherRule.dispatcher) {
        listOf<AppResult<ZipCodeLocation>>(
            AppResult.Success(ZipCodeLocation(MunicipalityId(1, 2, 3), "SP", "São Paulo")),
            AppResult.Failure(AppError.Network),
            AppResult.Failure(AppError.Validation("CEP anterior inválido"))
        ).forEach { obsoleteResult ->
            val obsolete = CompletableDeferred<AppResult<ZipCodeLocation>>()
            val current = CompletableDeferred<AppResult<ZipCodeLocation>>()
            val vm = viewModel(resolve = { zip ->
                if (zip == "01001000") obsolete.awaitIgnoringCancellation() else current.await()
            })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.changeZipCode("01001000")
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.changeZipCode("70000000")
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            obsolete.complete(obsoleteResult)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

            assertEquals("70000000", vm.state.value.zipCode)
            assertEquals("", vm.state.value.city)
            assertEquals(MunicipalityId(0, 0, 0), vm.state.value.municipalityId)
            assertEquals(null, vm.state.value.errorMessage)
            assertEquals(null, vm.state.value.zipCodeErrorMessage)
            assertTrue(vm.state.value.loading)

            current.complete(AppResult.Success(location()))
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals("Brasília", vm.state.value.city)
            assertFalse(vm.state.value.loading)
        }
    }

    @Test
    fun `shorter zip edit cancels loading and ignores pending response`() = runTest(dispatcherRule.dispatcher) {
        listOf<AppResult<ZipCodeLocation>>(
            AppResult.Success(location()),
            AppResult.Failure(AppError.Network),
            AppResult.Failure(AppError.Validation("CEP inválido"))
        ).forEach { result ->
            val pending = CompletableDeferred<AppResult<ZipCodeLocation>>()
            val vm = viewModel(resolve = { pending.awaitIgnoringCancellation() })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.changeZipCode("70000000")
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(vm.state.value.loading)
            vm.changeZipCode("700")
            assertFalse(vm.state.value.loading)
            val edited = vm.state.value
            pending.complete(result)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(edited, vm.state.value)
        }
    }

    @Test
    fun `returning to same zip ignores its previous request after latest completion`() = runTest(dispatcherRule.dispatcher) {
        val old = CompletableDeferred<AppResult<ZipCodeLocation>>()
        var calls = 0
        val vm = viewModel(resolve = {
            calls++
            if (calls == 1) {
                old.awaitIgnoringCancellation()
            } else {
                AppResult.Success(location())
            }
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.changeZipCode("70000000")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.changeZipCode("700")
        vm.changeZipCode("70000000")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val latest = vm.state.value
        old.complete(AppResult.Failure(AppError.Network))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Brasília", latest.city)
        assertEquals(latest, vm.state.value)
    }

    @Test
    fun `expired session ignores queued and pending zip lookups`() = runTest(dispatcherRule.dispatcher) {
        listOf<AppResult<ZipCodeLocation>>(
            AppResult.Success(location()),
            AppResult.Failure(AppError.Network),
            AppResult.Failure(AppError.Validation("CEP inválido"))
        ).forEach { result ->
            var current = true
            var calls = 0
            val pending = CompletableDeferred<AppResult<ZipCodeLocation>>()
            val vm = viewModel(isSessionCurrent = { current }, resolve = {
                calls++
                pending.await()
            })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.changeZipCode("01001000")
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            current = false
            val expired = vm.state.value
            pending.complete(result)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(expired, vm.state.value)
            vm.changeZipCode("70000000")
            val edited = vm.state.value
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, calls)
            assertEquals(edited, vm.state.value)
        }
    }

    private suspend fun <T> CompletableDeferred<T>.awaitIgnoringCancellation(): T = suspendCoroutine { continuation ->
        invokeOnCompletion { continuation.resume(getCompleted()) }
    }
}
