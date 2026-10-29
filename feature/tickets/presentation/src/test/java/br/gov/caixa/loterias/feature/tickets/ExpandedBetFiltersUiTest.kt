package br.gov.caixa.loterias.feature.tickets

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class ExpandedBetFiltersUiTest {
    @get:Rule val compose = createComposeRule()
    private val events = mutableListOf<String>()
    private val options = listOf(TicketFilterOption(1, "Opção inicial"), TicketFilterOption(2, "Outra opção"))
    private val state = TicketsUiState(
        betConfiguration = ConfirmedBetsConfiguration(statuses = options, orderings = options, betTypes = options, contestTypes = options),
        modalities = listOf(
            LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
            LotteryModality(3, "Quina", "QUINA"),
            LotteryModality(8, "Lotofácil", "LOTOFACIL")
        )
    )

    @Test fun eachExpandedPickerDispatchesItsOwnFilterAndCanCollapseAgain() {
        show()
        listOf(
            "Tipo de aposta" to "type:2",
            "Tipo de concurso" to "contest:2",
            "Situação da aposta" to "status:2",
            "Ordenar por" to "ordering:2"
        ).forEach { (label, event) ->
            compose.onNodeWithText(label).performScrollTo().performClick()
            compose.onNodeWithContentDescription(label).performCustomAccessibilityActionWithLabel("Outra opção")
            compose.runOnIdle { assertEquals(event, events.last()) }
            compose.onNodeWithText(label).performScrollTo().performClick()
            compose.onNodeWithContentDescription(label).assertDoesNotExist()
        }
        compose.runOnIdle { assertEquals(listOf("type:2", "contest:2", "status:2", "ordering:2"), events) }
    }

    @Test fun oddModalityRowsDispatchSpecificSelectionAndAllModalities() {
        show()
        compose.onNodeWithText("Modalidades").performScrollTo().performClick()
        compose.onNodeWithText("Todas").assertExists()
        compose.onNodeWithText("Mega-Sena").performScrollTo().performClick()
        compose.onNodeWithText("Quina").performScrollTo().performClick()
        compose.onNodeWithText("Lotofácil").performScrollTo().performClick()
        compose.onNodeWithText("Todas").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf("modality:2", "modality:3", "modality:8", "modality:0"), events) }
    }

    @Test fun selectingCurrentModalityClearsOnlyModalityFilter() {
        show(state.copy(betFilters = state.betFilters.copy(modality = 2)))
        compose.onNodeWithText("Modalidades").performScrollTo().performClick()
        compose.onNodeWithText("Mega-Sena").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf("modality:0"), events) }
    }

    @Test fun additionalFlagsAndDialogActionsKeepIndependentCallbacks() {
        show()
        listOf("Surpresinha", "Teimosinha", "Combos").forEach { compose.onNodeWithText(it).performScrollTo().performClick() }
        compose.onNodeWithText("OK").performClick()
        compose.onNodeWithText("Limpar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(listOf("surprise", "recurring", "combo", "apply", "clear", "close"), events) }
    }

    @Test fun absentConfigurationDoesNotRenderAnUnusableDialog() {
        show(state.copy(betConfiguration = null))
        compose.onNodeWithText("OK").assertDoesNotExist()
        compose.onNodeWithText("Tipo de aposta").assertDoesNotExist()
        compose.runOnIdle { assertEquals(emptyList<String>(), events) }
    }

    private fun show(value: TicketsUiState = state) {
        compose.setContent {
            CaixaDSTheme {
                BetFiltersDialog(
                    value,
                    BetFiltersActions(
                        closeBetFilters = { events += "close" }, selectBetPeriod = { events += "period" },
                        selectBetStatus = { events += "status:$it" }, selectBetOrdering = { events += "ordering:$it" },
                        selectBetType = { events += "type:$it" }, selectContestType = { events += "contest:$it" },
                        selectModality = { events += "modality:$it" }, toggleSurprise = { events += "surprise" },
                        toggleRecurring = { events += "recurring" }, toggleCombo = { events += "combo" },
                        applyBetFilters = { events += "apply" }, clearBetFilters = { events += "clear" }
                    )
                )
            }
        }
    }
}
