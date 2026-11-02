package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.state.RetailSearchUiState

@Composable
internal fun RetailSearchResults(state: RetailSearchUiState, actions: RetailSearchActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    when {
        state.loading -> Unit

        state.retailers.isEmpty() -> Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_nenhuma_loterica_encontrada_8bfcf1),
                color = LoteriasCaixaDSColorTokens.RetailSearchText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSFuturaFontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                ),
                modifier = Modifier.uiId("retail.0037")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_verifique_o_nome_ou_codigo_digitado_4d0aaa),
                color = LoteriasCaixaDS.colors.mutedText,
                fontSize = 16.sp,
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSFuturaFontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                ),
                modifier = Modifier.uiId("retail.0038")
            )
        }

        else -> LazyColumn(contentPadding = PaddingValues(bottom = 8.dp), modifier = Modifier.uiId("retail.0039")) {
            items(state.retailers, key = { it.id }) { retailer ->
                RetailerResultRow(retailer, actions.toggleFavorite)
            }
        }
    }
}
