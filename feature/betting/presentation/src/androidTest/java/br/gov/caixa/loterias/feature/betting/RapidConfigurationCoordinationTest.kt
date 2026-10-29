package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.presentation.route.RapidConfigurationRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidConfigurationViewModel

class RapidConfigurationCoordinationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun amountPresetsNearDailyLimitRetainOnlyTheSelectedMinimumAndDailyMaximum() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle { fixture.model.setMinimumAmount(BigDecimal("60")) }
        page(2)
        compose.onAllNodesWithText("120").assertCountEquals(0)
        compose.onAllNodesWithText("180").assertCountEquals(0)
        compose.onAllNodesWithText("60").assertCountEquals(1)
        compose.onAllNodesWithText("200").assertCountEquals(1)
    }

    @Test fun correctedMinimumBelowCartFloorRemainsVisibleAsNativePreset() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle { fixture.model.setMaximumAmount(BigDecimal("30")) }
        page(2)
        compose.runOnIdle { assertEquals(BigDecimal("25"), fixture.model.state.value.minimumAmount) }
        compose.onNodeWithText("25").assertExists()
    }

    @Test fun equalLimitsAtOneQuarterOfDailyLimitRetainTheLegacyBoundary() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle {
            fixture.model.setMaximumAmount(BigDecimal("50"))
            fixture.model.setMinimumAmount(BigDecimal("50"))
            assertEquals(BigDecimal("50"), fixture.model.state.value.minimumAmount)
            assertEquals(BigDecimal("50"), fixture.model.state.value.maximumAmount)
        }
    }

    @Test fun summaryVisitKeepsSaveAvailableAfterEditingAndPublishesSavedConfiguration() {
        val fixture = Fixture()
        var saved = 0
        var back = 0
        show(fixture, onSaved = { saved++ }, onBack = { back++ })
        compose.onNodeWithText("Salvar").assertDoesNotExist()
        page(6)
        compose.onNodeWithText("Salvar").assertIsEnabled()
        compose.onNodeWithContentDescription("Editar Valor mínimo da aposta").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(2, fixture.model.state.value.page) }
        compose.onNodeWithText("Salvar").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(1, saved)
            assertEquals(BigDecimal("30"), fixture.saved.single().minimumAmount)
            assertEquals(setOf(1), fixture.saved.single().requiredNumbers)
        }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, back) }
    }

    @Test fun emptyModalitySelectionDisablesSaveAndSummaryEditTargetsModalities() {
        val fixture = Fixture()
        show(fixture)
        page(6)
        compose.onNodeWithContentDescription("Editar Modalidades").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("mega-sena").performClick()
        compose.onNodeWithText("quina").performClick()
        compose.onNodeWithText("Salvar").assertIsNotEnabled()
        compose.runOnIdle { assertTrue(fixture.model.state.value.selectedModalityIds.isEmpty()) }
        compose.onNodeWithText("quina").performClick()
        compose.onNodeWithText("Salvar").assertIsEnabled()
        compose.runOnIdle { assertEquals(setOf(3), fixture.model.state.value.selectedModalityIds) }
    }

    @Test fun requiredAndExcludedNumbersKeepConflictWarningAndIndependentClearActions() {
        val fixture = Fixture()
        show(fixture)
        page(4)
        visibleText("03").performClick()
        page(5)
        visibleText("03").performClick()
        compose.onNodeWithText("Um número não pode ser marcado como obrigatório e proibido.").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        visibleText("Limpar seleção").performClick()
        compose.runOnIdle {
            assertEquals(setOf(1, 3), fixture.model.state.value.requiredNumbers)
            assertTrue(fixture.model.state.value.excludedNumbers.isEmpty())
            assertEquals(null, fixture.model.state.value.warningMessage)
        }
        page(6)
        compose.onNodeWithContentDescription("Editar Números obrigatórios").performClick()
        compose.waitForIdle()
        visibleText("Limpar seleção").performClick()
        compose.runOnIdle { assertTrue(fixture.model.state.value.requiredNumbers.isEmpty()) }
    }

    @Test fun invalidCustomAmountOpensNativeEntendiAndRetainsTheEditorForCorrection() {
        val fixture = Fixture()
        show(fixture)
        page(2)
        compose.onAllNodesWithText("...")[0].performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("100")
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("O valor selecionado tem que estar entre R$ 30,00 e R$ 200,00").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNode(hasSetTextAction()).assertExists()
        compose.runOnIdle { assertTrue(fixture.saved.isEmpty()) }
        compose.onNode(hasSetTextAction()).performTextReplacement("15000")
        compose.onNodeWithText("Salvar").performClick()
        compose.runOnIdle { assertEquals(BigDecimal("150.00"), fixture.model.state.value.maximumAmount) }
    }

    @Test fun amountLimitsRetryAndCustomDialogPreserveValidationAndCancellation() {
        val fixture = Fixture().apply { failLimits = true }
        show(fixture)
        page(2)
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(2, fixture.limitReads); fixture.failLimits = false }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onAllNodesWithText("...")[0].performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("100")
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("O valor selecionado tem que estar entre R$ 30,00 e R$ 200,00").assertExists()
        compose.runOnIdle { assertEquals(BigDecimal("100"), fixture.model.state.value.maximumAmount) }
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onAllNodesWithText("...")[0].performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("15000")
        compose.onNodeWithText("Salvar").performClick()
        compose.runOnIdle { assertEquals(BigDecimal("150.00"), fixture.model.state.value.maximumAmount) }
        compose.onAllNodesWithText("...")[1].performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("4000")
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(BigDecimal("30"), fixture.model.state.value.minimumAmount) }
    }

    @Test fun prizeSuggestionsAndSummaryEditKeepSelectedPrize() {
        val fixture = Fixture()
        show(fixture)
        page(1)
        compose.onNodeWithText("40 Mi").performClick()
        compose.runOnIdle { assertEquals(BigDecimal("40000000"), fixture.model.state.value.minimumPrize) }
        page(6)
        compose.onNodeWithContentDescription("Editar Prêmio mínimo").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, fixture.model.state.value.page) }
        compose.onAllNodesWithText("Qualquer valor").onLast().performClick()
        compose.runOnIdle { assertEquals(BigDecimal.ZERO, fixture.model.state.value.minimumPrize) }
    }

    @Test fun replacingViewModelUpdatesPageSelectionAndSaveWithoutResettingSummaryVisit() {
        val first = Fixture()
        val second = Fixture()
        val replace = mutableStateOf(false)
        compose.setContent {
            val firstModel = remember { first.create() }
            val secondModel = remember { second.create() }
            CaixaDSTheme {
                RapidConfigurationRoute(if (replace.value) secondModel else firstModel, {}, {})
            }
        }
        page(6)
        compose.runOnIdle { replace.value = true }
        compose.waitForIdle()
        compose.onNodeWithText("Salvar").assertIsEnabled()
        page(3)
        compose.onNodeWithText("mega-sena").performClick()
        compose.onNodeWithText("Salvar").performClick()
        compose.runOnIdle {
            assertEquals(6, first.model.state.value.page)
            assertTrue(first.saved.isEmpty())
            assertEquals(setOf(3), second.saved.single().modalities.map { it.id }.toSet())
        }
    }

    private fun page(index: Int) {
        compose.onNodeWithContentDescription("Etapa ${index + 1} de 7").performClick()
        compose.waitForIdle()
    }

    private fun visibleText(text: String) = compose.onAllNodesWithText(text).let { nodes ->
        nodes.fetchSemanticsNodes().indices.map { nodes[it] }.single { it.isDisplayed() }
    }

    private fun show(fixture: Fixture, onSaved: () -> Unit = {}, onBack: () -> Unit = {}) {
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme { RapidConfigurationRoute(model, onBack, onSaved) }
        }
    }

    private class Fixture {
        private val modalities = listOf(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), LotteryModality(3, "Quina", "QUINA"))
        val saved = mutableListOf<RapidBetConfiguration>()
        var failLimits = false
        var limitReads = 0
        lateinit var model: RapidConfigurationViewModel

        fun create() = RapidConfigurationViewModel(
            GetRapidBetConfigurationUseCase {
                ServiceResult.Success(RapidBetConfiguration(BigDecimal("30"), BigDecimal("100"), BigDecimal.ZERO, setOf(1), setOf(2), modalities, emptySet()))
            },
            GetLotteryModalitiesUseCase { AppResult.Success(modalities) },
            SaveRapidBetConfigurationUseCase { saved.add(it); ServiceResult.Success(it) },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            getParameters = GetBettingParametersUseCase {
                limitReads++
                if (failLimits) AppResult.Failure(AppError.Network)
                else AppResult.Success(BettingParameters(BigDecimal("200"), BigDecimal("30"), 100, emptyList()))
            },
            verifyAccess = VerifyAccountAccessUseCase { AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal("200"))) },
        ).also { model = it }
    }
}
