package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceTab
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab

@Composable
internal fun MarketplaceTabs(selected: MarketplaceTab, onSelect: (MarketplaceTab) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = androidx.compose.ui.platform.LocalDensity.current.fontScale > 1f
    Row(
        Modifier.fillMaxWidth()
            .then(if (largeFont) Modifier.heightIn(min = 66.dp).height(IntrinsicSize.Min) else Modifier.height(66.dp))
            .background(CaixaDS.colors.surface)
    ) {
        MarketplaceTabItem(
            uiStrings.getString(LocalizedR.string.ui_boloes_f052c3),
            selected == MarketplaceTab.POOLS
        ) { onSelect(MarketplaceTab.POOLS) }
        MarketplaceTabItem(
            uiStrings.getString(LocalizedR.string.ui_lotericas_favoritas_8a5e86),
            selected == MarketplaceTab.FAVORITE_RETAILERS
        ) {
            onSelect(MarketplaceTab.FAVORITE_RETAILERS)
        }
    }
}

@Composable
private fun RowScope.MarketplaceTabItem(label: String, selected: Boolean, onClick: () -> Unit) {
    CaixaDSMarketplaceTab(label, selected, onClick, (Modifier.weight(1f)).uiId("pool.0108"))
}
