package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.account.presentation.route.PersonalDataRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import br.gov.caixa.loterias.feature.account.presentation.route.RegistrationRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel

/** Apenas validação de consulta sintética. Mutações de conta falham imediatamente se chamadas por acidente. */
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
        // A UF vazia tem seu rótulo e seu texto de exemplo desabilitado na árvore sem mesclagem.
        val ufTop = compose.onAllNodesWithText("UF", useUnmergedTree = true)
            .fetchSemanticsNodes().minOf { it.boundsInRoot.top }
        assertTrue("CEP validation must precede UF, not the bottom of the form", warning.bottom <= ufTop)
    }

    private companion object { const val MESSAGE = "CEP inválido para teste." }
}
