package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR

@Composable
internal fun PrizeLoadingScreen(onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        uiStrings.getString(LocalizedR.string.ui_resgatar_premio_e68bcd),
        onBack,
        modifier = Modifier
            .uiId("retail.0028")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.brand)
        }
    }
}

@Composable
internal fun LegacyRedemptionActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color,
    cornerRadius: Int = 10,
    compactContent: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = (modifier.size(184.dp, 84.dp)).uiId("retail.0029"),
        shape = RoundedCornerShape(cornerRadius.dp),
        contentPadding = if (compactContent) PaddingValues(0.dp) else ButtonDefaults.ContentPadding,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = CaixaDSColorTokens.White)
    ) {
        Text(
            text,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 17.7.sp,
            maxLines = if (compactContent) 1 else 2,
            modifier = Modifier.uiId("retail.0030")
        )
    }
}

@Composable
internal fun PrizeHelpAction(onHelp: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0031")) {
        Icon(
            CaixaDSIcons.Help,
            uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
            tint = CaixaDSColorTokens.White,
            modifier = (Modifier.size(24.dp)).uiId("retail.0032")
        )
    }
}

internal fun legacyRedemptionGradient() = Brush.horizontalGradient(
    listOf(CaixaDSColorTokens.RedemptionButton, CaixaDSColorTokens.Blue700)
)
