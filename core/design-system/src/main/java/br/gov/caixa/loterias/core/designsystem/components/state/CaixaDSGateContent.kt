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
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
fun CaixaDSStatusAction(title: String, message: String, action: String, onAction: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White).padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(130.dp).border(4.dp, CaixaDS.colors.brand, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = CaixaDSIcons.Forward,
                contentDescription = null,
                modifier = (Modifier.size(52.dp).rotate(-90f)).uiId("ds.0125"),
                tint = CaixaDS.colors.brand
            )
        }
        Text(
            title,
            modifier = (Modifier.padding(top = 28.dp)).uiId("ds.0126"),
            color = CaixaDS.colors.brand,
            style = CaixaDS.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            message,
            modifier = (Modifier.padding(vertical = 16.dp)).uiId("ds.0127"),
            color = CaixaDS.colors.mutedText,
            textAlign = TextAlign.Center
        )
        CaixaDSPrimaryButton(action, onAction, (Modifier.fillMaxWidth()).uiId("ds.0128"))
    }
}

@Composable
fun CaixaDSGateMessage(message: String) {
    Column(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White).padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = CaixaDS.colors.brand)
        Text(
            message,
            modifier = (Modifier.padding(top = 20.dp)).uiId("ds.0129"),
            color = CaixaDS.colors.mutedText,
            textAlign = TextAlign.Center
        )
    }
}
