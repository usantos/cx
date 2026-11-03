package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.mapper.icon
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

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
        ResultPreview(result, ink)
        Spacer(Modifier.weight(1f))
        HorizontalDivider((Modifier.padding(top = 12.dp, bottom = 7.dp)).uiId("home.0068"), color = CxHomeV2Colors.CFFE1E8EF)
        Text(
            uiStrings.getString(DeliveryR.string.ui_ver_resultado_209476),
            color = blue,
            fontFamily = bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = (Modifier.fillMaxWidth()).uiId("home.0069")
        )
    }
}

@Composable
internal fun HomeV2Presentation.ResultPreview(result: DrawResult?, ink: androidx.compose.ui.graphics.Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (result == null) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_consultar_ultimo_resultado_a43072),
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
            result.firstDrawNumbers.take(RESULT_PREVIEW_NUMBER_COUNT).forEach { number ->
                Box(Modifier.size(25.dp).background(ink, CircleShape), contentAlignment = Alignment.Center) {
                    Text(
                        uiStrings.getString(DeliveryR.string.ui_02d_b2dd46).format(number),
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
}

private const val RESULT_PREVIEW_NUMBER_COUNT = 6
