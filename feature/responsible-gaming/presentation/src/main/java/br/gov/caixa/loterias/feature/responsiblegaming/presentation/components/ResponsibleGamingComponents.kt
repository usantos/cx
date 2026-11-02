package br.gov.caixa.loterias.feature.responsiblegaming.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR

@Composable
internal fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = LoteriasCaixaDS.colors.teal)
    }
}

@Composable
internal fun ErrorState(message: String, onRetry: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            LoteriasCaixaDSIcons.Warning,
            contentDescription = null,
            tint = LoteriasCaixaDS.colors.primaryButton,
            modifier = Modifier.uiId("rg.0001")
        )
        Text(
            message,
            modifier = (Modifier.padding(top = 16.dp)).uiId("rg.0002"),
            color = LoteriasCaixaDS.colors.primaryButton,
            textAlign = TextAlign.Center
        )
        LoteriasCaixaDSPrimaryButton(
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onRetry,
            (Modifier.padding(top = 20.dp).width(210.dp)).uiId("rg.0003")
        )
    }
}

@Composable
internal fun LegacyTealButton(
    text: String,
    outlined: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean = true,
    direction: Int = 0,
) {
    val disabledOutline = LoteriasCaixaDSColorTokens.DisabledOutline
    val largeFont = LocalDensity.current.fontScale > 1f
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = (modifier.then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(48.dp))).uiId("rg.0004"),
        shape = RoundedCornerShape(4.dp),
        border = if (outlined) {
            BorderStroke(1.dp, if (enabled) LoteriasCaixaDS.colors.teal else disabledOutline)
        } else {
            null
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) LoteriasCaixaDS.colors.surface else LoteriasCaixaDS.colors.teal,
            contentColor = if (outlined) LoteriasCaixaDS.colors.teal else LoteriasCaixaDSColorTokens.White,
            disabledContainerColor = if (outlined) LoteriasCaixaDS.colors.surface else LoteriasCaixaDSColorTokens.DisabledTealAction,
            disabledContentColor = if (outlined) disabledOutline else LoteriasCaixaDSColorTokens.White
        )
    ) {
        if (direction < 0) {
            Icon(LoteriasCaixaDSIcons.Back, contentDescription = null, modifier = (Modifier.size(18.dp)).uiId("rg.0005"))
            Spacer(Modifier.width(5.dp))
        }
        Text(text, modifier = (if (largeFont) Modifier.weight(1f) else Modifier).uiId("rg.0006"), textAlign = TextAlign.Center)
        if (direction > 0) {
            Spacer(Modifier.width(5.dp))
            Icon(LoteriasCaixaDSIcons.Forward, contentDescription = null, modifier = (Modifier.size(18.dp)).uiId("rg.0007"))
        }
    }
}
