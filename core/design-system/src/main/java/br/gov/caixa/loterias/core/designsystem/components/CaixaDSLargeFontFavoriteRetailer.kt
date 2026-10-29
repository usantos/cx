package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
internal fun CaixaDSLargeFontFavoriteRetailer(name: String, city: String, onOpen: () -> Unit, onRemove: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                name,
                Modifier.fillMaxWidth(),
                color = CaixaDSColorTokens.RetailSearchText,
                style = CaixaDSMarketplaceStyle.text(RETAILER_TEXT_SIZE, FontWeight.Bold)
            )
            Text(
                city,
                Modifier.fillMaxWidth(),
                color = CaixaDSColorTokens.RetailSearchText,
                style = CaixaDSMarketplaceStyle.text(RETAILER_TEXT_SIZE).copy(fontFamily = CaixaDSFuturaFontFamily)
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    stringResource(R.string.ui_ver_boloes_30719c),
                    Modifier.weight(1f).border(1.dp, CaixaDSColorTokens.CaixaBlue, RoundedCornerShape(4.dp))
                        .uiId("ds.0020").clickable(role = Role.Button, onClick = onOpen).padding(8.dp),
                    color = CaixaDSColorTokens.CaixaBlue,
                    style = CaixaDSMarketplaceStyle.text(RETAILER_TEXT_SIZE),
                    textAlign = TextAlign.Center
                )
                Image(
                    painterResource(R.drawable.legacy_marketplace_trash),
                    stringResource(R.string.ui_excluir_1_s_5fd47f, name),
                    Modifier.size(48.dp).uiId("ds.0021").clickable(role = Role.Button, onClick = onRemove).padding(12.dp)
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSMarketplaceStyle.divider))
    }
}

private const val RETAILER_TEXT_SIZE = 14
