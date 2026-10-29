package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.account.presentation.route.PersonalDataRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel

class PersonalDataFailureStateTest {
    @get:Rule val compose = createComposeRule()

    @Test fun profileLoadingAndFailureCanBeAcknowledgedWithoutSavingOrCompleting() {
        val response = CompletableDeferred<AppResult<AccountProfile>>()
        val updates = AtomicInteger(0)
        val model = PersonalDataViewModel(
            getProfile = GetAccountProfileUseCase { response.await() },
            resolveZipCode = ResolveZipCodeUseCase { AppResult.Failure(AppError.Network) },
            updatePersonalData = UpdatePersonalDataUseCase { _, _ ->
                updates.incrementAndGet()
                AppResult.Failure(AppError.Network)
            },
        )
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                PersonalDataRoute(model, onBack = {}, onComplete = { error("Failed profile must not complete") })
            }
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { response.complete(AppResult.Failure(AppError.Network)) }
        compose.waitUntil { !model.state.value.loading }
        compose.onNodeWithText("Não foi possível conectar. Verifique sua internet e tente novamente.").assertIsDisplayed()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithText("Entendi").performTouchInput { click(center) }
        compose.runOnIdle {
            assertEquals(null, model.state.value.errorMessage)
            assertEquals(0, updates.get())
        }
    }
}
