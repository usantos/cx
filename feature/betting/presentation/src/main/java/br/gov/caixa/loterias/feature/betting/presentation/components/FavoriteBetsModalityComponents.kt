package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.loteriasCaixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle

@Composable
fun FavoriteModalityGrid(modalities: List<Pair<Int?, String>>, selected: Int?, onSelect: (Int?) -> Unit) {
    modalities.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth()) {
            row.forEach { (id, name) ->
                val style = loteriasCaixaDSLotteryStyle(id ?: 0, name)
                val isSelected = selected == id
                val selectedColor = if (id == null) LoteriasCaixaDSColorTokens.ComboDark else style.light
                val background = if (isSelected) selectedColor else LoteriasCaixaDSColorTokens.FavoriteFilterCanvas
                val border = if (isSelected) selectedColor else LoteriasCaixaDSColorTokens.DrawerDivider
                val content = when {
                    !isSelected -> LoteriasCaixaDSColorTokens.TicketChoiceMuted
                    id == null -> LoteriasCaixaDSColorTokens.White
                    else -> style.content
                }
                Row(
                    Modifier.weight(1f).padding(4.dp).heightIn(min = 62.dp)
                        .background(background, RoundedCornerShape(8.dp))
                        .border(1.dp, border, RoundedCornerShape(8.dp))
                        .uiId("bet.0119").selectable(selected = isSelected, role = Role.RadioButton) { onSelect(id) }
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (id != null) Image(loteriasCaixaDSLotteryClover(id, onDark = isSelected), null, Modifier.size(20.dp))
                    Text(
                        if (id == null) name else name.lowercase(),
                        (Modifier.weight(1f).padding(horizontal = 4.dp)).uiId("bet.0120"),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LoteriasCaixaDSFuturaFontFamily,
                        color = content,
                        style = favoriteLegacyTextStyle
                    )
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

val favoriteLegacyTextStyle =
    TextStyle(fontFamily = LoteriasCaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true))
