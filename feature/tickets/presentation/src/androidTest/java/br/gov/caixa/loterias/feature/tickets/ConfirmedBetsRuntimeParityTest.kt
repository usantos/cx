package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.*
import androidx.compose.ui.test.*
import org.junit.Assert.*
import android.widget.TextView
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyEmptyBets
import br.gov.caixa.loterias.feature.tickets.presentation.route.ConfirmedBetsDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

/** Dados sintéticos de teste para carrinho/lista vazia; nenhum aplicativo autenticado ou serviço real é usado. */
class ConfirmedBetsRuntimeParityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun emptyListUsesCxEightDpRowInsetAndKeepsBetAction() {
        var betNow = false
        compose.setContent {
            CaixaDSTheme {
                Column {
                    Spacer(Modifier.fillMaxWidth().height(16.dp).testTag("list-margin"))
                    LegacyEmptyBets { betNow = true }
                }
            }
        }
        val margin = compose.onNodeWithTag("list-margin").fetchSemanticsNode().boundsInRoot
        val text = compose.onNodeWithText("Você ainda não fez apostas no período selecionado. Aproveite para apostar agora!").fetchSemanticsNode().boundsInRoot
        val density = margin.height / 16f
        assertEquals("CX row_apostas_confirmadas_empty has an 8dp top inset", 8f * density, text.top - margin.bottom, 1f)
        compose.onNodeWithText("Aposte aqui").performClick()
        compose.runOnIdle { assertTrue(betNow) }
    }

    @Test fun periodLabelMatchesNativeCxTextViewMetricsAndSelectsBackendPeriod() {
        lateinit var model: TicketsViewModel
        compose.setContent {
            CaixaDSTheme {
                Column {
                    AndroidView(modifier = Modifier.testTag("native-label"), factory = { context ->
                        TextView(context).apply {
                            text = "Selecione o período"
                            textSize = 12f
                            typeface = ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular)
                            includeFontPadding = true
                        }
                    })
                    val vm = remember {
                        TicketsViewModel(
                            GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
                            GetPurchasesUseCase { AppResult.Success(emptyList()) },
                            GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
                            CheckTicketUseCase { AppResult.Success(emptyList()) },
                            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                            getBetsConfiguration = GetConfirmedBetsConfigurationUseCase {
                                AppResult.Success(ConfirmedBetsConfiguration(periods = listOf(
                                    TicketPeriod(0, description = "Últimos 7 dias"), TicketPeriod(10, 2026, "Outubro de 2026"))))
                            },
                        ).also { model = it }
                    }
                    ConfirmedBetsDataRoute(vm, {}, {})
                }
            }
        }
        val expected = compose.onNodeWithTag("native-label").fetchSemanticsNode().boundsInRoot
        val actual = compose.onNodeWithText("Selecione o período").fetchSemanticsNode().boundsInRoot
        assertEquals("Period label height must match CX TextView", expected.height, actual.height, 1f)
        compose.onNodeWithText("Últimos 7 dias").performClick()
        compose.onNodeWithText("Outubro de 2026").performClick()
        compose.runOnIdle { assertEquals(10, model.state.value.betFilters.period?.month); assertEquals(2026, model.state.value.betFilters.period?.year) }
    }

    @Test fun authenticatedBetsExposeCapturedCartSummaryAndCartNavigation() {
        var cartOpened = false
        val total = mutableStateOf(BigDecimal("39.00"))
        compose.setContent {
            val model = remember {
                TicketsViewModel(
                    GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
                    GetPurchasesUseCase { AppResult.Success(emptyList()) },
                    GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
                    CheckTicketUseCase { AppResult.Success(emptyList()) },
                    FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                )
            }
            CaixaDSTheme {
                ConfirmedBetsDataRoute(model, {}, {}, cartTotal = total.value, cartBetCount = 9,
                    onCart = { cartOpened = true })
            }
        }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("9", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Valor atual do carrinho").performClick()
        compose.runOnIdle { assertTrue(cartOpened); total.value = BigDecimal.ZERO }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertDoesNotExist()
    }
}
