package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.PersonalDataRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import org.junit.Rule
import org.junit.Test

class PersonalDataVisitorUnderlayParityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun visitorInitialFormPreservesUnresolvedLimitPlaceholdersUntilProfileLoads() {
        // XML antes de atualizaLayout(): ambos os recursos ficam sem resolução enquanto o login do visitante está pendente.
        val model = PersonalDataViewModel(
            getProfile = GetAccountProfileUseCase { error("Visitor must not read private profile") },
            resolveZipCode = ResolveZipCodeUseCase { error("Unexpected lookup") },
            updatePersonalData = UpdatePersonalDataUseCase { _, _ -> error("Unexpected mutation") },
            deferInitialLoad = true,
            isSessionCurrent = { false },
        )
        compose.setContent {
            CaixaDSTheme { PersonalDataRoute(model, {}, { error("Visitor cannot save") }) }
        }
        compose.waitForIdle()
        compose.onNodeWithText("R$ %1\$s até %2\$s").assertExists()
        compose.onNodeWithText("Solicitar limite acima de %1\$s").assertExists()
        compose.onNodeWithText("Atualizar").assertIsNotEnabled()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
    }
}
