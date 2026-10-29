package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.canRegister
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun `identity initializes immutable fields and actions update registration`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel()
        assertEquals("12345678901", viewModel.state.value.cpf)
        assertEquals("Pessoa", viewModel.state.value.name)
        assertEquals("pessoa@caixa.gov.br", viewModel.state.value.email)
        viewModel.changeGender(Gender.FEMALE)
        viewModel.toggleTerms()
        viewModel.toggleNotifications()
        assertEquals(Gender.FEMALE, viewModel.state.value.gender)
        assertTrue(viewModel.state.value.acceptsTerms)
        assertFalse(viewModel.state.value.acceptsNotifications)
    }

    @Test
    fun `zip code resolves location and shorter edit clears it`() = runTest(dispatcherRule.dispatcher) {
        var zip = ""
        val viewModel = viewModel(resolve = {
            zip = it
            AppResult.Success(location())
        })
        viewModel.changeZipCode("70.000-0009")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("70000000", zip)
        assertEquals("DF", viewModel.state.value.stateCode)
        assertEquals("Brasília", viewModel.state.value.city)
        viewModel.changeZipCode("1")
        assertEquals("", viewModel.state.value.city)
        assertEquals(MunicipalityId(0, 0, 0), viewModel.state.value.municipalityId)
    }

    @Test
    fun `submits backend registration and exposes completion`() = runTest(dispatcherRule.dispatcher) {
        var registration: AccountRegistration? = null
        val viewModel = viewModel(register = {
            registration = it
            AppResult.Success(Unit)
        })
        viewModel.changeZipCode("70000000")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleTerms()
        viewModel.submit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.registered)
        assertTrue(registration!!.acceptsTerms)
        assertTrue(registration!!.acceptsNotifications)
        assertEquals(MunicipalityId(7, 1, 0), registration!!.municipalityId)
    }

    @Test
    fun `maps every service error and missing identity`() = runTest(dispatcherRule.dispatcher) {
        val missing = viewModel(user = null)
        assertTrue(missing.state.value.errorMessage!!.startsWith("Não foi possível recuperar"))

        listOf(
            AppError.Network to "Não foi possível conectar",
            AppError.Validation("") to "Confira os dados informados.",
            AppError.Unexpected to "Ocorreu um erro inesperado"
        ).forEach { (error, message) ->
            val resolveFailure = viewModel(resolve = { AppResult.Failure(error) })
            resolveFailure.changeZipCode("70000000")
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            if (error is AppError.Validation) {
                assertEquals(null, resolveFailure.state.value.errorMessage)
                assertTrue(resolveFailure.state.value.zipCodeErrorMessage!!.startsWith(message))
            } else {
                assertEquals(null, resolveFailure.state.value.zipCodeErrorMessage)
                assertTrue(resolveFailure.state.value.errorMessage!!.startsWith(message))
            }

            val registerFailure = viewModel(register = { AppResult.Failure(error) })
            registerFailure.submit()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(registerFailure.state.value.errorMessage!!.startsWith(message))
            assertFalse(registerFailure.state.value.loading)
        }
    }

    @Test
    fun `readiness requires identity terms and resolved location`() {
        val ready = RegistrationUiState(
            cpf = "1",
            name = "Pessoa",
            email = "a@b.com",
            zipCode = "70000000",
            stateCode = "DF",
            city = "Brasília",
            municipalityId = MunicipalityId(7, 1, 0),
            acceptsTerms = true
        )
        assertTrue(canRegister(ready))
        assertFalse(canRegister(ready.copy(loading = true)))
        assertFalse(canRegister(ready.copy(cpf = "")))
        assertFalse(canRegister(ready.copy(name = "")))
        assertFalse(canRegister(ready.copy(email = "")))
        assertFalse(canRegister(ready.copy(zipCode = "1")))
        assertFalse(canRegister(ready.copy(stateCode = "")))
        assertFalse(canRegister(ready.copy(city = "")))
        assertFalse(canRegister(ready.copy(municipalityId = MunicipalityId(0, 0, 0))))
        assertFalse(canRegister(ready.copy(acceptsTerms = false)))
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

    private suspend fun <T> CompletableDeferred<T>.awaitIgnoringCancellation(): T = suspendCoroutine { continuation ->
        invokeOnCompletion { continuation.resume(getCompleted()) }
    }

    @Test fun `two submissions before IO starts reserve one operation and preserve success`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        var calls = 0
        val model = viewModel(ioDispatcher = io, register = {
            calls++
            if (calls == 1) AppResult.Success(Unit) else AppResult.Failure(AppError.Network)
        })
        model.submit()
        model.submit()
        io.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertTrue(model.state.value.registered)
        assertEquals(null, model.state.value.errorMessage)
        val terminal = model.state.value
        io.scheduler.advanceUntilIdle()
        assertEquals(terminal, model.state.value)
    }

    @Test fun `a zip response cannot release an unfinished registration submission`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        val pending = CompletableDeferred<AppResult<Unit>>()
        var calls = 0
        val model = viewModel(ioDispatcher = io, register = {
            calls++
            pending.await()
        })
        model.changeZipCode("70000000")
        model.toggleTerms()
        io.scheduler.advanceUntilIdle()
        model.submit()
        io.scheduler.runCurrent()
        model.changeZipCode("01001000")
        io.scheduler.runCurrent()
        assertFalse(model.state.value.loading)
        model.submit()
        io.scheduler.runCurrent()
        pending.complete(AppResult.Success(Unit))
        io.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertTrue(model.state.value.registered)
        assertEquals(null, model.state.value.errorMessage)
    }

    @Test fun `a finished failed registration permits an explicit retry`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        var calls = 0
        val model = viewModel(ioDispatcher = io, register = {
            calls++
            if (calls == 1) AppResult.Failure(AppError.Network) else AppResult.Success(Unit)
        })
        model.submit()
        io.scheduler.advanceUntilIdle()
        assertFalse(model.state.value.loading)
        assertFalse(model.state.value.registered)
        model.submit()
        io.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
        assertTrue(model.state.value.registered)
        assertEquals(null, model.state.value.errorMessage)
    }

    private fun viewModel(
        ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = dispatcherRule.dispatcher,
        user: AuthenticatedUser? = AuthenticatedUser("Pessoa", "12345678901", "pessoa@caixa.gov.br", "19900101"),
        resolve: suspend (String) -> AppResult<ZipCodeLocation> = { AppResult.Success(location()) },
        register: suspend (AccountRegistration) -> AppResult<Unit> = { AppResult.Success(Unit) },
    ) = RegistrationViewModel(
        CurrentUserIdentityProvider { user },
        ResolveZipCodeUseCase { resolve(it) },
        RegisterAccountUseCase { register(it) },
        FixedCoroutineDispatchers(ioDispatcher)
    )

    companion object {
        private fun location() = ZipCodeLocation(MunicipalityId(7, 1, 0), "DF", "Brasília")
    }
}
