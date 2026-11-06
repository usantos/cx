package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSMarketplacePoolExpansion(
    contest: String? = null,
    prognostics: androidx.compose.ui.text.AnnotatedString,
    availability: androidx.compose.ui.text.AnnotatedString,
    shares: Int,
    canDecrease: Boolean = shares > 1,
    canIncrease: Boolean,
    adding: Boolean = false,
    canAdd: Boolean = shares > 0,
    color: Color,
    contentColor: Color,
    detailColor: Color,
    onDetail: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAdd: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
        contest?.let {
            Text(
                it,
                color = detailColor,
                style = LoteriasCaixaDSMarketplaceStyle.text(CONTEST_AND_PROGNOSTICS_TEXT_SIZE, FontWeight.SemiBold)
            )
        }
        Text(
            prognostics,
            Modifier.padding(top = 12.dp),
            color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
            style = LoteriasCaixaDSMarketplaceStyle.text(CONTEST_AND_PROGNOSTICS_TEXT_SIZE)
        )
        Box(
            Modifier.padding(top = 16.dp).size(88.dp, 28.dp).border(1.dp, detailColor, RoundedCornerShape(4.dp))
                .uiId("ds.0027").clickable(onClick = onDetail),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_detalhes_62c950),
                color = detailColor,
                style = LoteriasCaixaDSMarketplaceStyle.text(DETAIL_ACTION_TEXT_SIZE)
            )
        }
        Text(
            availability,
            Modifier.padding(top = 16.dp),
            color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
            style = LoteriasCaixaDSMarketplaceStyle.text(SHARE_AVAILABILITY_TEXT_SIZE)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_quantidades_de_cotas_434de0),
            Modifier.align(Alignment.Start).padding(start = 16.dp, top = 19.dp),
            color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
            style = LoteriasCaixaDSMarketplaceStyle.text(SHARE_AVAILABILITY_TEXT_SIZE)
        )
        LoteriasCaixaDSMarketplacePurchaseControls(
            shares, canDecrease && !adding, canIncrease && !adding, adding, canAdd && !adding,
            color, contentColor, onDecrease, onIncrease, onAdd
        )
    }
}

private const val CONTEST_AND_PROGNOSTICS_TEXT_SIZE = 16
private const val DETAIL_ACTION_TEXT_SIZE = 14
private const val SHARE_AVAILABILITY_TEXT_SIZE = 18
