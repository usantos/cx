package br.gov.caixa.loterias.core.designsystem.components.state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSStatusAction(title: String, message: String, action: String, onAction: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White).padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(130.dp).border(4.dp, LoteriasCaixaDS.colors.brand, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = LoteriasCaixaDSIcons.Forward,
                contentDescription = null,
                modifier = (Modifier.size(52.dp).rotate(-GateContentMetrics.BACK_ICON_ROTATION_DEGREES)).uiId("ds.0125"),
                tint = LoteriasCaixaDS.colors.brand
            )
        }
        Text(
            title,
            modifier = (Modifier.padding(top = 28.dp)).uiId("ds.0126"),
            color = LoteriasCaixaDS.colors.brand,
            style = LoteriasCaixaDS.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            message,
            modifier = (Modifier.padding(vertical = 16.dp)).uiId("ds.0127"),
            color = LoteriasCaixaDS.colors.mutedText,
            textAlign = TextAlign.Center
        )
        LoteriasCaixaDSPrimaryButton(action, onAction, (Modifier.fillMaxWidth()).uiId("ds.0128"))
    }
}

@Composable
fun LoteriasCaixaDSGateMessage(message: String) {
    Column(
        Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White).padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
        Text(
            message,
            modifier = (Modifier.padding(top = 20.dp)).uiId("ds.0129"),
            color = LoteriasCaixaDS.colors.mutedText,
            textAlign = TextAlign.Center
        )
    }
}

private object GateContentMetrics {
    const val BACK_ICON_ROTATION_DEGREES = 90f
}
