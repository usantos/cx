package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteTextStyle

@Composable
internal fun FavoriteRouteHeading(title: String, description: String) {
    Text(
        title,
        (Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp)).uiId("cart.0070"),
        color = CaixaDSColorTokens.FavoriteCartText,
        style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold)
    )
    Text(
        description,
        (Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)).uiId("cart.0071"),
        color = CaixaDSColorTokens.SecondaryText,
        style = favoriteTextStyle
    )
}
