package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.mapper.date
import br.gov.caixa.loterias.feature.home.presentation.mapper.icon
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable internal fun HomeV2Presentation.ModalityCard(item: HomeLottery, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val ink = if (item.tipoModalidade == HomeV2Modality.MEGA_SENA &&
        LocalLoteriasCaixaDSMegaSenaCampaign.current != LoteriasCaixaDSCampaign.DEFAULT
    ) {
        loteriasCaixaDSLotteryStyle(2).content
    } else {
        CxHomeV2Colors.White
    }
    Row(
        modifier.heightIn(min = 78.dp).clip(RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(colors(item.tipoModalidade)))
            .uiId("home.0056").clickable(
                onClickLabel = uiStrings.getString(
                    DeliveryR.string.ui_apostar_em_1_s_74e1cb,
                    name(item.tipoModalidade)
                )
            ) { actions.bet(item) }
            .padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(icon(item.tipoModalidade)), null, Modifier.size(29.dp))
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(name(item.tipoModalidade), fontFamily = bold, fontSize = 13.sp, color = ink, modifier = Modifier.uiId("home.0057"))
            Text(
                if (item.open) {
                    prize(item)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_concurso_1_s_32ee4c, item.contestNumber.toString())
                },
                fontFamily = bold,
                fontSize = 13.sp,
                color = ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.uiId("home.0058")
            )
            Row(
                Modifier.padding(top = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(painterResource(DeliveryR.drawable.ic_calendario), null, (Modifier.size(12.dp)).uiId("home.0059"), tint = ink)
                Text(date(item), fontFamily = regular, fontSize = 10.sp, color = ink, maxLines = 1, modifier = Modifier.uiId("home.0060"))
            }
        }
        Text(uiStrings.getString(DeliveryR.string.ui_texto_7bb37d), fontSize = 24.sp, color = ink, modifier = Modifier.uiId("home.0061"))
    }
}

@Composable internal fun HomeV2Presentation.Shortcut(title: String, detail: String, icon: Int, modifier: Modifier, action: () -> Unit) {
    Column(
        modifier.heightIn(min = 72.dp).shadow(1.dp, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp))
            .background(CxHomeV2Colors.White).semantics { contentDescription = "$title. $detail" }
            .uiId("home.0073").clickable(onClick = action).padding(horizontal = 3.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(painterResource(icon), null, tint = navy, modifier = (Modifier.size(26.dp)).uiId("home.0074"))
        Text(
            title,
            color = navy,
            fontFamily = bold,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("home.0075")
        )
    }
}
