package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.allModalities
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.retailerDisplayLabel
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen.MarketplaceFilterScreen
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Caracterização da tela pública com estado controlado e o redutor existente; nenhum serviço é usado. */
class MarketplaceFilterCoordinationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun cancelAndCloseDiscardDraftNumbersAndReopeningUsesCommittedSelection() {
        val form = Form(initial.copy(wantedNumbers = listOf(7)))
        show(form)
        open("wanted_numbers_selector")
        number("03").performClick()
        compose.runOnIdle { assertTrue(form.actions.isEmpty()) }
        compose.onNodeWithText("Cancelar").performClick()
        compose.onAllNodes(isDialog()).assertCountEquals(0)
        open("wanted_numbers_selector")
        number("01").performClick()
        compose.onNodeWithText("Fechar").performClick()
        compose.runOnIdle { assertTrue(form.actions.isEmpty()) }
        open("wanted_numbers_selector")
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle { assertEquals(listOf(MarketplaceFilterAction.ChangeWantedNumbers(listOf(7))), form.actions) }
    }

    @Test fun wantedAndUnwantedBlockEachOtherAndConfirmSortedSelectionsOnly() {
        val form = Form(initial.copy(wantedNumbers = listOf(3), unwantedNumbers = listOf(7)))
        show(form)
        open("wanted_numbers_selector")
        number("07").performClick()
        compose.onNodeWithText("Não é possível selecionar um número já escolhido no outro filtro.").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        number("05").performClick()
        number("01").performClick()
        compose.runOnIdle { assertTrue(form.actions.isEmpty()) }
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(MarketplaceFilterAction.ChangeWantedNumbers(listOf(1, 3, 5))), form.actions)
        }
        open("unwanted_numbers_selector")
        number("03").performClick()
        compose.onNodeWithText("Entendi").performClick()
        number("09").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertEquals(MarketplaceFilterAction.ChangeUnwantedNumbers(listOf(7, 9)), form.actions.last())
            assertEquals(2, form.actions.size)
        }
    }

    @Test fun repeatedConflictsDismissWithoutChangingThePendingSelection() {
        val form = Form(initial.copy(wantedNumbers = listOf(3), unwantedNumbers = listOf(7, 9)))
        show(form)
        open("wanted_numbers_selector")
        listOf("07", "09", "07").forEach { unavailable ->
            number(unavailable).performClick()
            compose.onNodeWithText("Não é possível selecionar um número já escolhido no outro filtro.")
                .assertIsDisplayed()
            compose.runOnIdle { assertTrue(form.actions.isEmpty()) }
            compose.onNodeWithText("Entendi").performClick()
            compose.onNodeWithText("Não é possível selecionar um número já escolhido no outro filtro.")
                .assertDoesNotExist()
        }
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(MarketplaceFilterAction.ChangeWantedNumbers(listOf(3))), form.actions)
            assertEquals(listOf(7, 9), form.state.value.unwantedNumbers)
        }
    }

    @Test fun quantitySelectionReplacesTheDraftAndEmitsOnlyTheConfirmedValue() {
        val form = Form(initial.copy(selectedNumberQuantity = 6))
        show(form)
        open("number_quantity_selector")
        number("07").performClick()
        number("08").performClick()
        compose.runOnIdle { assertTrue(form.actions.isEmpty()) }
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle { assertEquals(listOf(MarketplaceFilterAction.SelectNumberQuantity(8)), form.actions) }
    }

    @Test fun modalitySelectionPreservesContestTypeAndLotecaHidesWantedSelectors() {
        val special = mega.copy(name = "mega da virada", contestType = 2)
        val loteca = mega.copy(name = "Loteca", baseName = "Loteca", maximumNumber = 14)
        val form = Form(initial.copy(modalities = listOf(allModalities, mega, special, loteca), allModalitiesHighlighted = true))
        show(form)
        compose.onAllNodes(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton) and
                SemanticsMatcher.expectValue(SemanticsProperties.Selected, true)
        ).assertCountEquals(1)
        compose.onNodeWithText("Todas as\nmodalidades").assertIsSelected()
        compose.onNodeWithText(special.cardLabel).performScrollTo().performClick()
        compose.runOnIdle { assertEquals(MarketplaceFilterAction.SelectModality(special), form.actions.last()) }
        compose.onNodeWithTag("wanted_numbers_selector").assertExists()
        compose.onNodeWithText(loteca.cardLabel).performScrollTo().performClick()
        compose.onNodeWithText("Quantidade de Palpites").assertExists()
        compose.onNodeWithTag("wanted_numbers_selector").assertDoesNotExist()
        compose.onNodeWithTag("unwanted_numbers_selector").assertDoesNotExist()
        compose.onNodeWithTag("number_quantity_selector").assertExists()
        compose.runOnIdle { assertEquals(MarketplaceFilterAction.SelectModality(loteca), form.actions.last()) }
    }

    @Test fun retailerSearchScrollsAllSuggestionsWithinNativeViewportAndForwardsTheOriginalRetailer() {
        val retailers = (1..7).map { index ->
            Retailer(index.toLong(), index.toString(), "Lotérica $index", "Razão $index", "Rua $index", "Centro", "Cidade", "DF", false)
        }
        val form = Form(initial.copy(retailerLoading = true, retailerMessage = "Buscando lotéricas", retailerSuggestions = retailers))
        show(form)
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertExists()
        compose.onNodeWithText("Buscando lotéricas").assertExists()
        val suggestions = compose.onNodeWithTag("marketplace_retailer_suggestions")
        suggestions.performScrollTo().assertHeightIsEqualTo(130.dp)
        suggestions.performScrollToNode(hasText(retailerDisplayLabel(retailers[6])))
        compose.onNodeWithText(retailerDisplayLabel(retailers[6])).assertIsDisplayed().performClick()
        compose.runOnIdle {
            assertSame(retailers[6], (form.actions.single() as MarketplaceFilterAction.SelectRetailer).value)
            form.actions.clear()
            form.state.value = form.state.value.copy(retailerSuggestions = retailers)
        }
        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("000004")
        compose.onAllNodes(hasSetTextAction())[0].performTextInputSelection(androidx.compose.ui.text.TextRange(3))
        suggestions.performScrollToNode(hasText(retailerDisplayLabel(retailers[0])))
        compose.onNodeWithText(retailerDisplayLabel(retailers[0])).performClick()
        compose.runOnIdle {
            assertEquals(
                listOf(MarketplaceFilterAction.ChangeLottery("000004"), MarketplaceFilterAction.SelectRetailer(retailers[0])),
                form.actions
            )
            assertSame(retailers[0], (form.actions.last() as MarketplaceFilterAction.SelectRetailer).value)
            form.state.value = form.state.value.copy(lotteryEnabled = false)
        }
        compose.onNodeWithText("Lotéricas").assertDoesNotExist()
        compose.onNodeWithText("Nome ou código").assertDoesNotExist()
        compose.onAllNodes(hasSetTextAction()).assertCountEquals(4)
    }

    @Test fun numericFieldsAndBackForwardTheirExistingActions() {
        val form = Form(initial.copy(lotteryEnabled = false))
        show(form)
        listOf("1234", "5678", "2", "100").forEachIndexed { index, value ->
            compose.onAllNodes(hasSetTextAction())[index].performScrollTo().performTextReplacement(value)
        }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(
                listOf(
                    MarketplaceFilterAction.ChangeMinimumValue("12,34"),
                    MarketplaceFilterAction.ChangeMaximumValue("56,78"),
                    MarketplaceFilterAction.ChangeMinimumShares("2"),
                    MarketplaceFilterAction.ChangeMaximumShares("100"),
                    MarketplaceFilterAction.Back
                ),
                form.actions
            )
        }
    }

    @Test fun configurationErrorHasPriorityAndValidationMessageAloneDoesNotDisableApply() {
        val form = Form(
            initial.copy(
                configurationError = "Configuração indisponível",
                validationMessage = "Confira os filtros",
                minimumSharesError = "Mínimo inválido",
                maximumSharesError = "Máximo inválido"
            )
        )
        show(form)
        compose.onNodeWithText("Configuração indisponível").assertExists()
        compose.onNodeWithText("Confira os filtros").assertDoesNotExist()
        compose.onNodeWithText("Mínimo inválido").assertExists()
        compose.onNodeWithText("Máximo inválido").assertExists()
        compose.onNodeWithText("Aplicar").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Limpar Filtros").assertIsEnabled()
        compose.runOnIdle {
            form.state.value = form.state.value.copy(configurationError = null, minimumSharesError = null, maximumSharesError = null)
        }
        compose.onNodeWithText("Confira os filtros").assertExists()
        compose.onNodeWithText("Aplicar").assertIsEnabled().performClick()
        compose.onNodeWithText("Limpar Filtros").performClick()
        compose.runOnIdle { assertEquals(listOf(MarketplaceFilterAction.Apply, MarketplaceFilterAction.Clear), form.actions) }
    }

    private fun show(form: Form) {
        compose.setContent { CaixaDSTheme { MarketplaceFilterScreen(form.state.value, form::dispatch) } }
    }

    private fun open(tag: String) {
        compose.onNodeWithTag(tag).performScrollTo().performClick()
    }

    private fun number(text: String) = compose.onNode(hasText(text) and hasAnyAncestor(isDialog()))

    private class Form(initial: MarketplaceFilterUiState) {
        val state = mutableStateOf(initial)
        val actions = mutableListOf<MarketplaceFilterAction>()
        fun dispatch(action: MarketplaceFilterAction) {
            actions.add(action)
            val current = state.value
            state.value = when (action) {
                is MarketplaceFilterAction.SelectModality -> current.copy(
                    selectedModality = action.value.takeUnless(MarketplaceFilterModalityUi::isAll),
                    allModalitiesHighlighted = action.value.isAll,
                    numberOptions = action.value.numberOptions,
                    selectedBetQuantity = null,
                    selectedNumberQuantity = null,
                    wantedNumbers = emptyList(),
                    unwantedNumbers = emptyList()
                )

                is MarketplaceFilterAction.ChangeLottery -> current.copy(
                    lottery = action.value,
                    selectedRetailer = current.selectedRetailer?.takeIf { retailerDisplayLabel(it) == action.value }
                )

                is MarketplaceFilterAction.SelectRetailer -> current.copy(
                    lottery = retailerDisplayLabel(action.value),
                    selectedRetailer = action.value,
                    retailerSuggestions = emptyList()
                )

                is MarketplaceFilterAction.ChangeMinimumValue -> current.copy(minimumValue = action.value)

                is MarketplaceFilterAction.ChangeMaximumValue -> current.copy(maximumValue = action.value)

                is MarketplaceFilterAction.ChangeMinimumShares -> current.copy(minimumShares = action.value.filter(Char::isDigit))

                is MarketplaceFilterAction.ChangeMaximumShares -> current.copy(maximumShares = action.value.filter(Char::isDigit))

                is MarketplaceFilterAction.SelectBetQuantity -> current.copy(
                    selectedBetQuantity = action.value.takeUnless { it == current.selectedBetQuantity }
                )

                is MarketplaceFilterAction.SelectNumberQuantity -> current.copy(selectedNumberQuantity = action.value)

                is MarketplaceFilterAction.ChangeWantedNumbers -> current.copy(wantedNumbers = action.value.distinct().sorted())

                is MarketplaceFilterAction.ChangeUnwantedNumbers -> current.copy(unwantedNumbers = action.value.distinct().sorted())

                else -> current
            }
        }
    }

    companion object {
        private val mega = MarketplaceFilterModalityUi(2, "Mega-Sena", "Mega-Sena", 1, (6..8).toList(), 60)
        private val initial = MarketplaceFilterUiState(
            modalities = listOf(allModalities, mega),
            selectedModality = mega,
            betOptions = listOf(1, 2, 3),
            numberOptions = (6..8).toList()
        )
    }
}
