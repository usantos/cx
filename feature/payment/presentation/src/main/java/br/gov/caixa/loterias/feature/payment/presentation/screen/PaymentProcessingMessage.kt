package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_PROCESSING_TITLE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.legacyProcessingText
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseProcessingMessages

@Composable
internal fun PaymentProcessingMessage() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().padding(start = 40.dp, top = 80.dp, end = 40.dp, bottom = 152.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = LoteriasCaixaDSIcons.PaymentProcessing,
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )
        Text(
            PURCHASE_PROCESSING_TITLE,
            (Modifier.padding(top = 10.dp)).uiId("pay.0114"),
            color = LoteriasCaixaDSColorTokens.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Box(
            Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp)
                .background(LoteriasCaixaDS.colors.successBadge)
        )
        Column(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            purchaseProcessingMessages.forEachIndexed { index, text ->
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_texto_4f8865),
                        color = LoteriasCaixaDSColorTokens.White,
                        modifier = Modifier
                            .uiId("pay.0115")
                    )
                    Text(
                        legacyProcessingText(text),
                        (Modifier.padding(start = 8.dp)).uiId("pay.0116"),
                        color = LoteriasCaixaDSColorTokens.White,
                        fontSize = 16.sp
                    )
                }
                if (index == 1) Spacer(Modifier.height(8.dp))
            }
        }
    }
}
