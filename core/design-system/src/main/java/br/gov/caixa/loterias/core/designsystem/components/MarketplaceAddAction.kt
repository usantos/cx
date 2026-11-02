package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId

@Composable
internal fun MarketplaceAddAction(
    adding: Boolean,
    enabled: Boolean,
    color: Color,
    contentColor: Color,
    onAdd: () -> Unit,
    modifier: Modifier,
) {
    val resources = LocalResources.current
    Box(
        modifier.background(
            color,
            RoundedCornerShape(MarketplacePurchaseLayout.actionCorner)
        ).uiId("ds.0042").clickable(enabled = enabled, onClick = onAdd),
        contentAlignment = Alignment.Center
    ) {
        Text(
            resources.getString(if (adding) R.string.ui_adicionando_6ae81e else R.string.ui_adicionar_ao_carrinho_2f424d),
            color = contentColor,
            style = LoteriasCaixaDSMarketplaceStyle.text(MarketplacePurchaseLayout.ACTION_TEXT_SIZE)
        )
    }
}
