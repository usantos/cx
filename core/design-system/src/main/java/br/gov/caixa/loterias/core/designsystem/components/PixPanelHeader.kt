package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun PixPanelHeader(formattedAmount: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_pix_fa7d3d),
        Modifier.fillMaxWidth().padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
        color = LoteriasCaixaDS.colors.cartDark,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        style = pixPanelStyle
    )
    Row(Modifier.fillMaxWidth().height(68.dp)) {
        Box(Modifier.width(5.dp).fillMaxHeight().background(LoteriasCaixaDS.colors.pix))
        Row(
            Modifier.weight(1f).fillMaxHeight().padding(start = 42.dp, end = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LoteriasCaixaDSPixBrand((Modifier.size(100.dp, 36.dp)).uiId("ds.0044"))
            Text(
                formattedAmount,
                color = LoteriasCaixaDSColorTokens.NotificationText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                style = pixPanelStyle
            )
        }
    }
    Box(Modifier.fillMaxWidth().height(.2.dp).background(LoteriasCaixaDS.colors.graphite))
}
