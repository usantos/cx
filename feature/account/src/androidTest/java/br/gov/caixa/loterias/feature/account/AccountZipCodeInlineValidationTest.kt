package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.auth.AuthenticatedUser
import br.gov.caixa.loterias.core.auth.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.account.*
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Synthetic lookup validation only. Account mutations fail immediately if accidentally called. */
class AccountZipCodeInlineValidationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun personalLookupValidationIsBesideCepWithoutGenericDialogAndClearsOnEdit() {
        val model = PersonalDataViewModel(
            GetAccountProfileUseCase { AppResult.Success(AccountProfile("00000000000", "Pessoa de Teste",
                "teste@example.invalid", "00000000", Gender.MALE, MunicipalityId(1, 1, 1), "DF",
                "Município de Teste", BigDecimal("100"), BigDecimal("10"), BigDecimal("200"), false)) },
            ResolveZipCodeUseCase { AppResult.Failure(AppError.Validation(MESSAGE)) },
            UpdatePersonalDataUseCase { _, _ -> error("No account update allowed") },
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            PersonalDataRoute(model, {}, { error("No save completion allowed") })
        } }
        compose.waitUntil { !model.state.value.loading }
        compose.runOnIdle { model.changeZipCode("00000001") }
        compose.waitUntil { !model.state.value.loading }
        assertInlineWarningBeforeUf()
        compose.onNodeWithText("Entendi").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals("", model.state.value.stateCode)
            assertEquals("", model.state.value.city)
            assertEquals(MunicipalityId(0, 0, 0), model.state.value.municipalityId)
            model.changeZipCode("0000000")
        }
        compose.onNodeWithText(MESSAGE).assertDoesNotExist()
    }

    @Test fun registrationLookupValidationIsBesideCepAndClearsOnEdit() {
        val model = RegistrationViewModel(
            CurrentUserIdentityProvider { AuthenticatedUser("Pessoa de Teste", "00000000000", "teste@example.invalid", "19900101") },
            ResolveZipCodeUseCase { AppResult.Failure(AppError.Validation(MESSAGE)) },
            RegisterAccountUseCase { error("No registration allowed") },
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            RegistrationRoute(model, {}, { error("No registration completion allowed") }, {})
        } }
        compose.runOnIdle { model.changeZipCode("00000001") }
        compose.waitUntil { !model.state.value.loading }
        assertInlineWarningBeforeUf()
        compose.onNodeWithText("Finalizar cadastro").assertIsNotEnabled()
        compose.runOnIdle { model.changeZipCode("0000000") }
        compose.onNodeWithText(MESSAGE).assertDoesNotExist()
    }

    private fun assertInlineWarningBeforeUf() {
        compose.onNodeWithText(MESSAGE).performScrollTo().assertIsDisplayed()
        val warning = compose.onNodeWithText(MESSAGE).fetchSemanticsNode().boundsInRoot
        // Empty UF has both its label and its disabled placeholder in the unmerged tree.
        val ufTop = compose.onAllNodesWithText("UF", useUnmergedTree = true)
            .fetchSemanticsNodes().minOf { it.boundsInRoot.top }
        assertTrue("CEP validation must precede UF, not the bottom of the form", warning.bottom <= ufTop)
    }

    private companion object { const val MESSAGE = "CEP inválido para teste." }
}
