package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.presentation.state.LoginAction
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataAction
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationAction
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceLoginState
import br.gov.caixa.loterias.feature.account.presentation.state.reducePersonalDataState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceRegistrationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountReducersTest {
    private val latestVersion = 3L
    private val location = ZipCodeLocation(MunicipalityId(1, 2, 3), "DF", "Brasília")

    @Test
    fun `registration rejects old zip result after same zip is reentered`() {
        val first = reduceRegistrationState(RegistrationUiState(), RegistrationAction.ZipCodeChanged("70000000", 1))
        val edited = reduceRegistrationState(first, RegistrationAction.ZipCodeChanged("1", 2))
        val latest = reduceRegistrationState(edited, RegistrationAction.ZipCodeChanged("70000000", latestVersion))
        assertSame(latest, reduceRegistrationState(latest, RegistrationAction.ZipCodeResolved("70000000", 1, location)))
        val resolved = reduceRegistrationState(latest, RegistrationAction.ZipCodeResolved("70000000", latestVersion, location))
        assertEquals("Brasília", resolved.city)
        assertFalse(resolved.loading)
        assertTrue(latest.loading)
    }

    @Test
    fun `personal data rejects stale success and failure without changing draft`() {
        val latest = reducePersonalDataState(PersonalDataUiState(), PersonalDataAction.ZipCodeChanged("70000000", latestVersion))
        assertSame(latest, reducePersonalDataState(latest, PersonalDataAction.ZipCodeResolved("70000000", 1, location)))
        assertSame(latest, reducePersonalDataState(latest, PersonalDataAction.ZipCodeFailed("70000000", 1, "old", null)))
        assertSame(latest, reducePersonalDataState(latest, PersonalDataAction.ZipCodeResolved("71000000", latestVersion, location)))
    }

    @Test
    fun `browser fallback claim cannot consume a newer generation`() {
        val first = reduceLoginState(LoginUiState(), LoginAction.BiometricAuthorizationFailed)
        val next = reduceLoginState(first, LoginAction.BiometricAuthorizationFailed)
        assertSame(next, reduceLoginState(next, LoginAction.BrowserFallbackConsumed(first.browserFallbackGeneration)))
        val consumed = reduceLoginState(next, LoginAction.BrowserFallbackConsumed(next.browserFallbackGeneration))
        assertFalse(consumed.browserFallbackPending)
        assertTrue(next.browserFallbackPending)
        assertSame(consumed, reduceLoginState(consumed, LoginAction.BrowserFallbackConsumed(next.browserFallbackGeneration)))
    }
}
