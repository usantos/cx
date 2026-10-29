package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.home.R
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
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

@Composable internal fun HomeV2Presentation.ModalityCard(item: HomeLottery, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val ink = if (item.tipoModalidade == HomeV2Modality.MEGA_SENA && LocalCaixaDSMegaSenaCampaign.current != CaixaDSCampaign.DEFAULT) {
        caixaDSLotteryStyle(2).content
    } else {
        CxHomeV2Colors.White
    }
    Row(
        modifier.heightIn(min = 78.dp).clip(RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(colors(item.tipoModalidade)))
            .uiId("home.0056").clickable(
                onClickLabel = uiStrings.getString(
                    LocalizedR.string.ui_apostar_em_1_s_74e1cb,
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
                Icon(painterResource(R.drawable.ic_calendario), null, (Modifier.size(12.dp)).uiId("home.0059"), tint = ink)
                Text(date(item), fontFamily = regular, fontSize = 10.sp, color = ink, maxLines = 1, modifier = Modifier.uiId("home.0060"))
            }
        }
        Text(uiStrings.getString(LocalizedR.string.ui_texto_7bb37d), fontSize = 24.sp, color = ink, modifier = Modifier.uiId("home.0061"))
    }
}

@Composable internal fun HomeV2Presentation.ResultCard(item: HomeLottery, result: DrawResult?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val ink = colors(item.tipoModalidade).first()
    Column(
        Modifier.width(190.dp).heightIn(min = 128.dp).fillMaxHeight().shadow(2.dp, RoundedCornerShape(9.dp)).clip(RoundedCornerShape(9.dp))
            .background(CxHomeV2Colors.White).uiId("home.0062").clickable { actions.modalityResults(item.tipoModalidade) }.padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(icon(item.tipoModalidade)), null, Modifier.size(22.dp))
            Spacer(Modifier.width(6.dp))
            Text(name(item.tipoModalidade), color = ink, fontFamily = bold, fontSize = 12.sp, modifier = Modifier.uiId("home.0063"))
        }
        if (result == null) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_consultar_ultimo_resultado_a43072),
                fontSize = 11.sp,
                color = navy,
                modifier = (Modifier.padding(top = 12.dp)).uiId("home.0064")
            )
        } else {
            Text(
                uiStrings.getString(LocalizedR.string.ui_concurso_1_s_32ee4c, result.contest.toString()),
                fontSize = 10.sp,
                color = navy,
                modifier = Modifier.uiId("home.0065")
            )
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                result.firstDrawNumbers.take(6).forEach { number ->
                    Box(Modifier.size(25.dp).background(ink, CircleShape), contentAlignment = Alignment.Center) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_02d_b2dd46).format(number),
                            color = CxHomeV2Colors.White,
                            fontSize = 11.sp,
                            lineHeight = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                            maxLines = 1, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier.uiId("home.0066")
                        )
                    }
                }
            }
            result.clovers.takeIf { it.isNotEmpty() }?.let {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_trevos_1_s_946f5c, it.joinToString(" • ")),
                    fontSize = 10.sp,
                    color = ink,
                    modifier = Modifier.uiId("home.0067")
                )
            }
        }
        Spacer(Modifier.weight(1f))
        HorizontalDivider((Modifier.padding(top = 12.dp, bottom = 7.dp)).uiId("home.0068"), color = CxHomeV2Colors.CFFE1E8EF)
        Text(
            uiStrings.getString(LocalizedR.string.ui_ver_resultado_209476),
            color = blue,
            fontFamily = bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = (Modifier.fillMaxWidth()).uiId("home.0069")
        )
    }
}

@Composable internal fun HomeV2Presentation.PoolBanner(item: HomeLottery?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (item == null) return
    Box(
        Modifier.fillMaxWidth().heightIn(min = 64.dp).clip(RoundedCornerShape(9.dp))
            .uiId("home.0070")
            .clickable(onClickLabel = uiStrings.getString(LocalizedR.string.ui_compre_seu_bolao_97d03e)) { actions.pool(item) }
    ) {
        Image(
            painterResource(R.drawable.home_v2_bolao_corporativo),
            null,
            Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.horizontalGradient(listOf(CxHomeV2Colors.CFF003B71.copy(alpha = .50f), CxHomeV2Colors.CFF003B71.copy(alpha = .12f)))
            )
        )
        Column(Modifier.align(Alignment.CenterStart).fillMaxWidth(.78f).padding(horizontal = 14.dp, vertical = 14.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_compre_seu_bolao_97d03e),
                color = CxHomeV2Colors.White,
                fontFamily = bold,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                modifier = Modifier.uiId("home.0071")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_jogue_com_amigos_e_aumente_suas_chan_264701),
                color = CxHomeV2Colors.White,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                modifier = Modifier.uiId("home.0072")
            )
        }
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
