// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.di.AccountModule
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountModuleContractTest {
    @Test fun `provided account use cases preserve validation and forward each explicit operation to same repository`() = runBlocking {
        val calls = mutableListOf<String>()
        val profile =
            AccountProfile(
                "synthetic-cpf", "Synthetic User", "fixture@example.test", "70000000", Gender.MALE,
                MunicipalityId(
                    7,
                    1,
                    0
                ),
                "DF", "Brasília",
                BigDecimal("100"), BigDecimal.TEN, BigDecimal("200"), false
            )
        val location = ZipCodeLocation(profile.municipalityId, "DF", "Brasília")
        val update = PersonalDataUpdate("70000000", Gender.FEMALE, profile.municipalityId, BigDecimal("120"), true)
        val registration = AccountRegistration("70000000", Gender.FEMALE, profile.municipalityId, true, false)
        val access = AccountAccess(PostLoginDestination.HOME, false, BigDecimal("100"))
        val repository = object : AccountRepository {
            override suspend fun profile(): AccountProfile {
                calls += "profile"
                return profile
            }
            override suspend fun locationForZipCode(zipCode: String): ZipCodeLocation {
                assertEquals("70000000", zipCode)
                calls += "zip"
                return location
            }
            override suspend fun updatePersonalData(value: PersonalDataUpdate) {
                assertEquals(update, value)
                calls += "update"
            }
            override suspend fun register(value: AccountRegistration) {
                assertEquals(registration, value)
                calls += "register"
            }
            override suspend fun verifyAccess(): AccountAccess {
                calls += "access"
                return access
            }
            override suspend fun recalculateAuthorizedLimit(): BigDecimal {
                calls += "limit"
                return BigDecimal("200")
            }
        }
        assertEquals(AppResult.Success(profile), AccountModule.accountProfile(repository)())
        assertEquals(AppResult.Success(location), AccountModule.resolveZipCode(repository)("70.000-000"))
        assertTrue(AccountModule.resolveZipCode(repository)("123") is AppResult.Failure)
        assertEquals(AppResult.Success(Unit), AccountModule.updatePersonalData(repository)(update, profile))
        assertTrue(AccountModule.updatePersonalData(repository)(update.copy(dailyLimit = BigDecimal.ZERO), profile) is AppResult.Failure)
        assertEquals(AppResult.Success(Unit), AccountModule.registerAccount(repository)(registration))
        assertTrue(AccountModule.registerAccount(repository)(registration.copy(acceptsTerms = false)) is AppResult.Failure)
        assertEquals(AppResult.Success(access), AccountModule.verifyAccountAccess(repository)())
        assertEquals(AppResult.Success(BigDecimal("200")), AccountModule.recalculateAuthorizedLimit(repository)())
        assertEquals(listOf("profile", "zip", "update", "register", "access", "limit"), calls)
    }
}
