package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountUseCasesTest {
    @Test
    fun `profile and zip code use cases delegate valid requests`() = runTest {
        val repository = FakeAccountRepository()
        assertEquals(profile(), (GetAccountProfile(repository)() as AppResult.Success).value)
        assertEquals(location(), (ResolveZipCode(repository)("70.000-000") as AppResult.Success).value)
        assertEquals("70000000", repository.resolvedZipCode)
    }

    @Test
    fun `zip code use case rejects malformed value`() = runTest {
        val result = ResolveZipCode(FakeAccountRepository())("123")
        assertTrue(result is AppResult.Failure && result.error is AppError.Validation)
    }

    @Test
    fun `personal data update validates and persists allowed limit`() = runTest {
        val repository = FakeAccountRepository()
        val update = update(limit = "100")
        assertTrue(UpdatePersonalData(repository)(update, profile()) is AppResult.Success)
        assertEquals(update, repository.updated)
    }

    @Test
    fun `personal data update rejects invalid cep municipality and limits`() = runTest {
        val useCase = UpdatePersonalData(FakeAccountRepository())
        listOf(
            update(zip = "1"),
            update(municipality = MunicipalityId(0, 0, 0)),
            update(limit = "9.99"),
            update(limit = "200.01"),
        ).forEach { update ->
            val result = useCase(update, profile())
            assertTrue(result is AppResult.Failure && result.error is AppError.Validation)
        }
    }

    @Test
    fun `registration requires terms zip code and municipality`() = runTest {
        val repository = FakeAccountRepository()
        val valid = AccountRegistration("70000000", Gender.MALE, MunicipalityId(7, 1, 0), true, false)
        assertTrue(RegisterAccount(repository)(valid) is AppResult.Success)
        assertEquals(valid, repository.registration)
        listOf(
            valid.copy(acceptsTerms = false),
            valid.copy(zipCode = "1"),
            valid.copy(municipalityId = MunicipalityId(0, 0, 0)),
        ).forEach {
            assertTrue(RegisterAccount(repository)(it) is AppResult.Failure)
        }
    }

    @Test
    fun `account access use case delegates session verification`() = runTest {
        val access = AccountAccess(PostLoginDestination.HOME, false, BigDecimal("100"))
        val repository = FakeAccountRepository(access)
        assertEquals(access, (VerifyAccountAccess(repository)() as AppResult.Success).value)
    }

    @Test
    fun `authorized limit recalculation accepts nonnegative values`() = runTest {
        val repository = FakeAccountRepository(authorizedLimit = BigDecimal("250"))
        assertEquals(BigDecimal("250"), (RecalculateAuthorizedLimit(repository)() as AppResult.Success).value)
        assertTrue(
            RecalculateAuthorizedLimit(FakeAccountRepository(authorizedLimit = BigDecimal("-1")))() is AppResult.Failure,
        )
    }

    private class FakeAccountRepository(
        private val access: AccountAccess = AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO),
        private val authorizedLimit: BigDecimal = BigDecimal.ZERO,
    ) : AccountRepository {
        var resolvedZipCode = ""
        var updated: PersonalDataUpdate? = null
        var registration: AccountRegistration? = null
        override suspend fun profile() = AccountUseCasesTest.profile()
        override suspend fun locationForZipCode(zipCode: String): ZipCodeLocation {
            resolvedZipCode = zipCode
            return location()
        }
        override suspend fun updatePersonalData(update: PersonalDataUpdate) { updated = update }
        override suspend fun register(registration: AccountRegistration) { this.registration = registration }
        override suspend fun verifyAccess() = access
        override suspend fun recalculateAuthorizedLimit() = authorizedLimit
    }

    companion object {
        private fun profile() = AccountProfile(
            "12345678901", "Pessoa", "pessoa@caixa.gov.br", "70000000", Gender.MALE,
            MunicipalityId(7, 1, 0), "DF", "Brasília",
            BigDecimal("100"), BigDecimal("10"), BigDecimal("200"), true,
        )
        private fun location() = ZipCodeLocation(MunicipalityId(7, 1, 0), "DF", "Brasília")
        private fun update(
            zip: String = "70000000",
            municipality: MunicipalityId = MunicipalityId(7, 1, 0),
            limit: String = "100",
        ) = PersonalDataUpdate(zip, Gender.FEMALE, municipality, BigDecimal(limit), false)
    }
}
