package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.date
import br.gov.caixa.loterias.feature.home.presentation.mapper.icon
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery

@Composable
internal fun HomeV2Presentation.HeroInformation(item: HomeLottery, ink: androidx.compose.ui.graphics.Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val type = item.tipoModalidade
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_proximo_sorteio_f25cee),
            fontFamily = bold,
            fontSize = 10.sp,
            color = ink,
            modifier = (Modifier.padding(vertical = 6.dp)).uiId("home.0076")
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_ultimo_resultado_68e65f),
            color = ink,
            fontSize = 11.sp,
            modifier = (
                Modifier.clip(
                    CircleShape
                ).background(CxHomeV2Colors.C44003322).border(1.dp, ink.copy(alpha = HERO_RESULT_OUTLINE_ALPHA), CircleShape)
                    .uiId("home.0077").clickable { actions.modalityResults(type) }.padding(8.dp)
                ).uiId("home.0078")
        )
    }
    HeroPrizeInformation(item, ink)
}

@Composable
internal fun HomeV2Presentation.HeroPrizeInformation(item: HomeLottery, ink: androidx.compose.ui.graphics.Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val type = item.tipoModalidade
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(icon(type)), null, Modifier.size(26.dp))
            Spacer(Modifier.width(7.dp))
            Text(name(type), fontFamily = bold, fontSize = 24.sp, color = ink, modifier = Modifier.uiId("home.0079"))
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_premio_estimado_699ac4),
            fontFamily = bold,
            fontSize = 13.sp,
            color = ink,
            modifier = Modifier.uiId("home.0080")
        )
        Text(
            prize(item),
            fontFamily = bold,
            fontSize = 25.sp,
            color = ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.uiId("home.0081")
        )
        Row(
            Modifier.padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(painterResource(R.drawable.ic_calendario), null, (Modifier.size(17.dp)).uiId("home.0082"), tint = ink)
            Text(date(item), color = ink, fontFamily = regular, fontSize = 11.sp, modifier = Modifier.uiId("home.0083"))
            Text(
                uiStrings.getString(LocalizedR.string.ui_concurso_1_s_32ee4c, item.contestNumber.toString()),
                color = ink,
                fontFamily = regular,
                fontSize = 11.sp,
                modifier = Modifier.uiId("home.0084")
            )
        }
    }
}

private const val HERO_RESULT_OUTLINE_ALPHA = .45f
