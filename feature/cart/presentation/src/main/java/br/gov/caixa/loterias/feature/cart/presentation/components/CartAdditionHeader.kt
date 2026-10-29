package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.presentation.mapper.drawDateLabel
import br.gov.caixa.loterias.feature.cart.presentation.mapper.label
import br.gov.caixa.loterias.feature.cart.presentation.mapper.toDisplayModality

@Composable
internal fun PoolHeader(
    modality: String,
    contest: Int,
    drawDate: String,
    color: Color,
    contentColor: Color,
    onBack: () -> Unit,
    onHelp: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(48.dp).background(color), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(24.dp))
        IconButton(
            onClick = onBack,
            modifier = (Modifier.width(30.dp).height(48.dp).focusProperties { canFocus = false }).uiId("cart.0007")
        ) {
            Icon(
                CaixaDSIcons.Back,
                contentDescription = uiStrings.getString(LocalizedR.string.ui_voltar_59dc92),
                modifier = (Modifier.size(18.dp)).uiId("cart.0008"),
                tint = contentColor
            )
        }
        Text(
            modality.toDisplayModality().ifBlank { uiStrings.getString(LocalizedR.string.ui_bolao_6bdc7b) },
            (Modifier.weight(1f).padding(start = 24.dp, end = 24.dp)).uiId("cart.0009"),
            color = contentColor,
            fontFamily = CaixaDSFuturaFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        if (contest > 0) {
            HeaderValue(
                stringResource(LocalizedR.string.format_concurso),
                contest.toString(),
                contentColor,
                modifier = Modifier
                    .uiId("cart.0010")
            )
            HeaderValue("Sorteio", drawDateLabel(drawDate), contentColor, (Modifier.padding(end = 8.dp)).uiId("cart.0011"))
        }
        if (onHelp != null) {
            IconButton(onClick = onHelp, modifier = (Modifier.padding(end = 4.dp).size(32.dp)).uiId("cart.0012")) {
                Icon(
                    painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_duvida2),
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    (Modifier.size(32.dp)).uiId("cart.0013"),
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
private fun HeaderValue(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.padding(start = 8.dp), horizontalAlignment = Alignment.Start) {
        Text(label, color = color, fontSize = 14.sp, lineHeight = 17.sp, modifier = Modifier.uiId("cart.0014"))
        Text(
            value,
            color = color,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .uiId("cart.0015")
        )
    }
}
