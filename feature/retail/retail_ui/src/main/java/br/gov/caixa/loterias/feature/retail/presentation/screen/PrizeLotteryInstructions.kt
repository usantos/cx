package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun PrizeLotteryInstruction(number: Int, text: String, modifier: Modifier = Modifier, highlightedText: String? = null) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).background(LoteriasCaixaDSColorTokens.White, CircleShape)
                .border(1.dp, LoteriasCaixaDS.colors.line, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number.toString(),
                color = LoteriasCaixaDS.colors.brand,
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("retail.0079")
            )
        }
        val instruction = buildAnnotatedString {
            if (highlightedText == null || highlightedText !in text) {
                append(text)
            } else {
                val start = text.indexOf(highlightedText)
                append(text.substring(0, start))
                withStyle(SpanStyle(color = LoteriasCaixaDSColorTokens.PrizeGreen, fontWeight = FontWeight.Bold)) {
                    append(highlightedText)
                }
                append(text.substring(start + highlightedText.length))
            }
        }
        Text(
            text = instruction,
            modifier = (Modifier.weight(1f).padding(start = 8.dp)).uiId("retail.0080"),
            color = LoteriasCaixaDS.colors.graphite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
