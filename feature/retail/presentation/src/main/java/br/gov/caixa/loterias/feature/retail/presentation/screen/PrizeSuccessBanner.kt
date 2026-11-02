package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.presentation.components.legacyRedemptionGradient

@Composable
internal fun LegacyRedemptionSuccessBanner(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.2.sp,
) {
    Box(
        modifier.fillMaxWidth().height(80.dp).background(legacyRedemptionGradient()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = LoteriasCaixaDSColorTokens.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            modifier = Modifier.uiId("retail.0131")
        )
    }
}
