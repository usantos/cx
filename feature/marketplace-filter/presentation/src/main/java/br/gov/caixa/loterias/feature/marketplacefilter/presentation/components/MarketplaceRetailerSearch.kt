package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterInput as Input
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterLabel as Label
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.caixaDSMarketplaceFilterText as filterText
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSLineHeight
import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.retailerDisplayLabel
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

@Composable
internal fun MarketplaceRetailerSearch(state: MarketplaceFilterUiState, onAction: (MarketplaceFilterAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Label(uiStrings.getString(LocalizedR.string.ui_lotericas_8c948f), (Modifier.padding(start = 10.dp, top = 10.dp)).uiId("filter.0031"))
    Input(
        value = state.lottery,
        onValueChange = { onAction(MarketplaceFilterAction.ChangeLottery(it)) },
        placeholder = uiStrings.getString(LocalizedR.string.ui_nome_ou_codigo_47d071),
        keyboardType = KeyboardType.Text,
        fontSize = 16,
        modifier = (Modifier.padding(start = 12.dp, top = 10.dp, end = 20.dp)).uiId("filter.0032"),
        accessibilityLabel = uiStrings.getString(LocalizedR.string.ui_lotericas_8c948f)
    )
    RetailerSearchFeedback(
        loading = state.retailerLoading,
        message = state.retailerMessage,
        suggestions = state.retailerSuggestions,
        onSelect = { onAction(MarketplaceFilterAction.SelectRetailer(it)) }
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_insira_o_nome_da_loterica_ou_o_codig_9a4d4f) +
            uiStrings.getString(LocalizedR.string.ui_numeros_do_segundo_campo_com_6_digit_08a1a7) +
            uiStrings.getString(LocalizedR.string.ui_e_loterico_10_000004_01_digite_00000_fbe38b),
        modifier = (Modifier.fillMaxWidth().padding(start = 12.dp, top = 10.dp, end = 20.dp)).uiId("filter.0033"),
        color = CaixaDSMarketplaceFilterStyle.hint,
        style = filterText(12).copy(lineHeight = caixaDSLineHeight(12.sp, 4.dp))
    )
}

@Composable
private fun RetailerSearchFeedback(loading: Boolean, message: String?, suggestions: List<Retailer>, onSelect: (Retailer) -> Unit) {
    if (loading) {
        Box(
            Modifier.fillMaxWidth().height(38.dp).padding(end = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = CaixaDSMarketplaceFilterStyle.accent,
                strokeWidth = 2.dp
            )
        }
    }
    message?.let {
        Text(
            it,
            modifier = (Modifier.padding(start = 12.dp, top = 6.dp, end = 20.dp)).uiId("filter.0034"),
            color = CaixaDSMarketplaceFilterStyle.hint,
            fontSize = 12.sp
        )
    }
    if (suggestions.isNotEmpty()) {
        LazyColumn(
            modifier = (
                Modifier.fillMaxWidth().height(130.dp)
                    .legacyAutomationId("marketplace_retailer_suggestions")
                ).uiId("filter.0035")
        ) {
            items(suggestions) { retailer ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                        .padding(start = 12.dp, end = 20.dp)
                        .uiId("filter.0036").clickable { onSelect(retailer) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        retailerDisplayLabel(retailer),
                        modifier = (Modifier.weight(1f)).uiId("filter.0037"),
                        color = CaixaDSMarketplaceFilterStyle.hint,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
                HorizontalDivider(
                    modifier = (Modifier.padding(start = 12.dp, end = 20.dp)).uiId("filter.0038"),
                    color = CaixaDSMarketplaceFilterStyle.line
                )
            }
        }
    }
}
