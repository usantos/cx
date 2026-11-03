package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle

@Composable
fun NativeDetailButtonDelivered(
    label: String,
    style: LoteriasCaixaDSLotteryStyle,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    download: Boolean = false,
) {
    val darkTextGame = style.darkContent != LoteriasCaixaDSColorTokens.White
    val background = if (darkTextGame) style.listText else style.darkContent
    val foreground = if (darkTextGame) style.darkContent else style.dark
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            Modifier.width(246.dp).height(44.dp).background(background, RoundedCornerShape(5.dp))
                .semantics(mergeDescendants = true) { contentDescription = label }
                .uiId("tickets.0193").clickable(enabled = onClick != null) { onClick?.invoke() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (download) Icon(LoteriasCaixaDSIcons.Download, null, (Modifier.size(17.dp)).uiId("tickets.0194"), tint = foreground)
            Text(
                label,
                (Modifier.padding(start = if (download) 8.dp else 0.dp)).uiId("tickets.0195"),
                color = foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
