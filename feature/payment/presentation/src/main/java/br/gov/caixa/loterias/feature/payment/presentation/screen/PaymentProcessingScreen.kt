package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseProcessingDestination

@Composable
internal fun ProcessingScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White)) {
        Box(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().background(
                Brush.verticalGradient(
                    listOf(
                        LoteriasCaixaDS.colors.successGradientStart,
                        LoteriasCaixaDS.colors.successGradientEnd
                    )
                )
            )
        ) {
            Row(
                Modifier.align(Alignment.TopCenter).fillMaxWidth().height(64.dp)
                    .background(LoteriasCaixaDS.colors.brand),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_confirmacao_3ae809),
                    (Modifier.weight(1f)).uiId("pay.0113"),
                    textAlign = TextAlign.Center,
                    color = LoteriasCaixaDSColorTokens.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                LegacyHelpIcon { onNavigate("support/terms") }
            }
            PaymentProcessingMessage()
            Box(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(41.dp)
                    .background(LoteriasCaixaDS.colors.surface)
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
        border = if (primary) null else BorderStroke(1.dp, LoteriasCaixaDS.colors.filterBlue),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) LoteriasCaixaDS.colors.actionHighlight else LoteriasCaixaDS.colors.surface,
            contentColor = if (primary) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDS.colors.brand
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
            LoteriasCaixaDSIcons.Help,
            contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
            tint = LoteriasCaixaDSColorTokens.White,
            modifier = Modifier.uiId("pay.0122")
        )
    }
}
