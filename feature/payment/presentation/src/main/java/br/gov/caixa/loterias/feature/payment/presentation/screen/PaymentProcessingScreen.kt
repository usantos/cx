package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_PROCESSING_TITLE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.legacyProcessingText
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseProcessingDestination
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseProcessingMessages

@Composable
internal fun ProcessingScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
      Box(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().background(
          Brush.verticalGradient(
            listOf(
              CaixaDS.colors.successGradientStart,
              CaixaDS.colors.successGradientEnd
                    )
                )
        )
      ) {
            Row(
                Modifier.align(Alignment.TopCenter).fillMaxWidth().height(64.dp)
                    .background(CaixaDS.colors.brand),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_confirmacao_3ae809),
                    (Modifier.weight(1f)).uiId("pay.0113"),
                    textAlign = TextAlign.Center,
                    color = CaixaDSColorTokens.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                LegacyHelpIcon { onNavigate("support/terms") }
            }
            Column(
                Modifier.fillMaxSize().padding(start = 40.dp, top = 80.dp, end = 40.dp, bottom = 152.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = CaixaDSIcons.PaymentProcessing,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    PURCHASE_PROCESSING_TITLE,
                    (Modifier.padding(top = 10.dp)).uiId("pay.0114"),
                    color = CaixaDSColorTokens.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Box(
                    Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp)
                        .background(CaixaDS.colors.successBadge)
                )
                Column(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    purchaseProcessingMessages.forEachIndexed { index, text ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                uiStrings.getString(LocalizedR.string.ui_texto_4f8865),
                                color = CaixaDSColorTokens.White,
                                modifier = Modifier
                                    .uiId("pay.0115")
                            )
                            Text(
                                legacyProcessingText(text),
                                (Modifier.padding(start = 8.dp)).uiId("pay.0116"),
                                color = CaixaDSColorTokens.White,
                                fontSize = 16.sp
                            )
                        }
                        if (index == 1) Spacer(Modifier.height(8.dp))
                    }
                }
            }
            Box(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(41.dp)
                    .background(CaixaDS.colors.surface)
            )
            ProcessingActionButton(
                text = spec.primaryAction,
                primary = true,
                modifier = (Modifier.align(Alignment.BottomCenter).padding(bottom = 84.dp)).uiId("pay.0117")
            ) { onNavigate(purchaseProcessingDestination()) }
            spec.secondaryAction?.let { text ->
                ProcessingActionButton(
                    text = text,
                    primary = false,
                    modifier = (Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)).uiId("pay.0118")
                ) { onNavigate("home") }
            }
        }
    }
}

@Composable
private fun ProcessingActionButton(text: String, primary: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = (modifier.width(274.dp).height(52.dp)).uiId("pay.0119"),
        shape = RoundedCornerShape(if (primary) 5.dp else 4.dp),
        border = if (primary) null else BorderStroke(1.dp, CaixaDS.colors.filterBlue),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) CaixaDS.colors.actionHighlight else CaixaDS.colors.surface,
            contentColor = if (primary) CaixaDSColorTokens.White else CaixaDS.colors.brand
        )
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, modifier = Modifier.uiId("pay.0120"))
    }
}

@Composable
private fun LegacyHelpIcon(onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    IconButton(onClick = onClick, modifier = Modifier.uiId("pay.0121")) {
        Icon(
            CaixaDSIcons.Help,
            contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
            tint = CaixaDSColorTokens.White,
            modifier = Modifier.uiId("pay.0122")
        )
    }
}
