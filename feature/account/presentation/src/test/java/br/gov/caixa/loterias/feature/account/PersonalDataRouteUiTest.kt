// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.account

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.PersonalDataRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PersonalDataRouteUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `zip help requires confirmation and notification preferences require explicit save`() {
        val opened = mutableListOf<String>()
        val updates = mutableListOf<PersonalDataUpdate>()
        var completed = 0
        val model = model { update, _ ->
            updates += update
            AppResult.Success(Unit)
        }
        compose.setContent {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        opened += uri
                    }
                }
            ) { CaixaDSTheme { PersonalDataRoute(model, {}, { completed++ }) } }
        }
        compose.onNodeWithText("Não sei o meu CEP").performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertTrue(opened.isEmpty()) }
        compose.onNodeWithText("Não sei o meu CEP").performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle { assertEquals(listOf("https://buscacepinter.correios.com.br/app/endereco/index.php"), opened) }
        compose.onNodeWithText("Aceito receber notificações de Loterias.").performScrollTo().performClick()
        compose.onNodeWithText(
            "Ao ativar esta opção, você receberá notificações importantes de loterias se estiverem habilitadas no seu aparelho. " +
                "A funcionalidade pode ser ativada ou desativada a qualquer momento. Para salvar, clique em atualizar."
        ).assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertTrue(updates.isEmpty())
            assertTrue(model.state.value.acceptsNotifications)
        }
        compose.onNodeWithText("Aceito receber notificações de Loterias.").performScrollTo().performClick()
        compose.onNodeWithText(
            "Ao desativar esta opção, você não receberá mais notificações de loterias. Para salvar, clique em atualizar."
        ).assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Atualizar").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(1, updates.size)
            assertEquals(1, completed)
            assertFalse(model.state.value.acceptsNotifications)
        }
    }

    @Test fun `save failure remains on form and dismisses without retrying request`() {
        var updates = 0
        var completed = 0
        val model = model { _, _ ->
            updates++
            AppResult.Failure(AppError.Validation("Synthetic update rejected"))
        }
        compose.setContent { CaixaDSTheme { PersonalDataRoute(model, {}, { completed++ }) } }
        compose.onNodeWithText("Atualizar").performClick()
        flush()
        compose.onNodeWithText("Synthetic update rejected").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        flush()
        compose.onNodeWithText("Synthetic update rejected").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, updates)
            assertEquals(0, completed)
            assertNull(model.state.value.errorMessage)
        }
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }
    private fun model(update: suspend (PersonalDataUpdate, AccountProfile) -> AppResult<Unit>) = PersonalDataViewModel(
        GetAccountProfileUseCase {
            AppResult.Success(
                AccountProfile(
                    "12345678901", "Synthetic Person", "fixture@example.test", "70000000", Gender.MALE,
                    MunicipalityId(7, 1, 0), "DF", "Brasília", BigDecimal("100"), BigDecimal("10"), BigDecimal("200"), false
                )
            )
        },
        ResolveZipCodeUseCase { AppResult.Success(ZipCodeLocation(MunicipalityId(7, 1, 0), "DF", "Brasília")) },
        UpdatePersonalDataUseCase { value, original -> update(value, original) },
        FixedCoroutineDispatchers(Dispatchers.Main.immediate)
    )
}
