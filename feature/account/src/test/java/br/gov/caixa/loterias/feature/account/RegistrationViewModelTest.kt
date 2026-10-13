package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.auth.AuthenticatedUser
import br.gov.caixa.loterias.core.auth.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.AccountRegistration
import br.gov.caixa.loterias.core.domain.account.Gender
import br.gov.caixa.loterias.core.domain.account.MunicipalityId
import br.gov.caixa.loterias.core.domain.account.RegisterAccountUseCase
import br.gov.caixa.loterias.core.domain.account.ResolveZipCodeUseCase
import br.gov.caixa.loterias.core.domain.account.ZipCodeLocation
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
            AppError.Unexpected to "Ocorreu um erro inesperado",
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
            cpf = "1", name = "Pessoa", email = "a@b.com", zipCode = "70000000",
            stateCode = "DF", city = "Brasília", municipalityId = MunicipalityId(7, 1, 0), acceptsTerms = true,
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

    private fun viewModel(
        user: AuthenticatedUser? = AuthenticatedUser("Pessoa", "12345678901", "pessoa@caixa.gov.br", "19900101"),
        resolve: suspend (String) -> AppResult<ZipCodeLocation> = { AppResult.Success(location()) },
        register: suspend (AccountRegistration) -> AppResult<Unit> = { AppResult.Success(Unit) },
    ) = RegistrationViewModel(
        CurrentUserIdentityProvider { user },
        ResolveZipCodeUseCase { resolve(it) },
        RegisterAccountUseCase { register(it) },
        FixedCoroutineDispatchers(dispatcherRule.dispatcher),
    )

    companion object {
        private fun location() = ZipCodeLocation(MunicipalityId(7, 1, 0), "DF", "Brasília")
    }
}
