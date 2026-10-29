package br.gov.caixa.loterias.apostas

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase
import br.gov.caixa.loterias.feature.support.presentation.route.SupportRoute
import br.gov.caixa.loterias.feature.support.presentation.viewmodel.SupportViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** O detalhe da pergunta no legado usa uma Activity separada: Voltar do sistema retorna primeiro às seções. */
class SupportPublicBackUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun systemBackFromQuestionsReturnsToSectionsBeforeExitingSupport() {
        var exits = 0
        var reads = 0
        compose.setContent {
            BackHandler { exits++ }
            val model = remember {
                SupportViewModel(
                    getSections = GetSupportSectionsUseCase { AppResult.Success(listOf(SupportSection(1, "APOSTAS", emptyList()))) },
                    getQuestions = GetSupportQuestionsUseCase {
                        reads++
                        AppResult.Success(listOf(SupportQuestion(7, "Pergunta sintética", "Resposta sintética")))
                    },
                    getTerms = GetTermsUseCase { error("FAQ must not read terms") },
                    acceptTermsUseCase = AcceptTermsUseCase { error("Public FAQ must not accept terms") },
                    privacyHtml = "",
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                )
            }
            CaixaDSTheme { SupportRoute(model, { exits++ }, { error("Unexpected acceptance") }) }
        }
        compose.onNodeWithText("APOSTAS").performClick()
        compose.onNodeWithText("Pergunta sintética").assertIsDisplayed()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("APOSTAS").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, exits); assertEquals(1, reads) }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle { assertEquals(1, exits) }
    }
}
