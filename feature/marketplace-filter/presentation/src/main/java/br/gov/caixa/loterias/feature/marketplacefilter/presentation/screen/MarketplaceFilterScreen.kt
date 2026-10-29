package br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen

import br.gov.caixa.loterias.core.designsystem.automation.automationId

import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.components.MarketplaceFilterForm
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.wantedNumberOptions
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.components.LegacyNumberSelectionDialog
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.components.NumberDialog

/** Conversão Compose do `activity_filtra_marketplace.xml`, preservando a geometria legada. */
@Composable
fun MarketplaceFilterScreen(
    state: MarketplaceFilterUiState,
    onAction: (MarketplaceFilterAction) -> Unit,
    modifier: Modifier = Modifier,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var numberDialog by remember { mutableStateOf<NumberDialog?>(null) }
    val showWantedFilters = state.selectedModality?.hidesWantedNumbers != true

    MarketplaceFilterForm(
        state = state,
        onAction = onAction,
        modifier = modifier,
        cartTotal = cartTotal,
        cartBetCount = cartBetCount,
        onCart = onCart,
        showWantedFilters = showWantedFilters,
        onOpenNumberDialog = { numberDialog = it }
    )

    numberDialog?.let { dialog ->
        val options = when (dialog) {
            NumberDialog.Quantity -> state.numberOptions
            NumberDialog.Wanted, NumberDialog.Unwanted -> wantedNumberOptions(state.selectedModality)
        }
        val selected = when (dialog) {
            NumberDialog.Quantity -> state.selectedNumberQuantity?.let(::listOf).orEmpty()
            NumberDialog.Wanted -> state.wantedNumbers
            NumberDialog.Unwanted -> state.unwantedNumbers
        }
        val unavailable = when (dialog) {
            NumberDialog.Wanted -> state.unwantedNumbers.toSet()
            NumberDialog.Unwanted -> state.wantedNumbers.toSet()
            NumberDialog.Quantity -> emptySet()
        }
        LegacyNumberSelectionDialog(
            title = when (dialog) {
                NumberDialog.Quantity -> uiStrings.getString(LocalizedR.string.ui_quantidade_de_dezenas_daa269)
                NumberDialog.Wanted -> uiStrings.getString(LocalizedR.string.ui_numeros_que_quero_b83043)
                NumberDialog.Unwanted -> uiStrings.getString(LocalizedR.string.ui_numeros_que_nao_quero_417744)
            },
            options = options,
            selected = selected,
            unavailable = unavailable,
            singleSelection = dialog == NumberDialog.Quantity,
            onDismiss = { numberDialog = null },
            onConfirm = { values ->
                when (dialog) {
                    NumberDialog.Quantity -> onAction(
                        MarketplaceFilterAction.SelectNumberQuantity(values.firstOrNull())
                    )

                    NumberDialog.Wanted -> onAction(MarketplaceFilterAction.ChangeWantedNumbers(values))

                    NumberDialog.Unwanted -> onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(values))
                }
                numberDialog = null
            }
        )
    }
}
