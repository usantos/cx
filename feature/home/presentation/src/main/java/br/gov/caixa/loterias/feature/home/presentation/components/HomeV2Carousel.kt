package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
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

@Composable
fun HomeV2Presentation.HeroCarousel(cards: List<HomeLottery>) {
    val heroCards = cards.take(4)
    if (heroCards.isNotEmpty()) {
        val pager = rememberPagerState(pageCount = { heroCards.size })
        Box(Modifier.padding(top = 12.dp)) {
            HorizontalPager(
                pager,
                contentPadding = PaddingValues(horizontal = 12.dp),
                pageSpacing = 10.dp
            ) { Hero(heroCards[it]) }
        }
        Dots(heroCards.size, pager.currentPage)
    }
}

@Composable private fun HomeV2Presentation.Hero(item: HomeLottery) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val type = item.tipoModalidade
    val ink = if (type == HomeV2Modality.MEGA_SENA && LocalCaixaDSMegaSenaCampaign.current != CaixaDSCampaign.DEFAULT) {
        caixaDSLotteryStyle(2).content
    } else {
        CxHomeV2Colors.White
    }
    Box(
        Modifier.fillMaxWidth().heightIn(min = 220.dp).clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(colors(type)))
    ) {
        if (type == HomeV2Modality.MEGA_SENA && LocalCaixaDSMegaSenaCampaign.current == CaixaDSCampaign.DEFAULT) {
            Image(
                painterResource(R.drawable.home_v2_mega_corporativo),
                null,
                Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterEnd
            )
            Box(
                Modifier.matchParentSize().background(
                    Brush.horizontalGradient(
                        0f to CxHomeV2Colors.CFF006B43.copy(alpha = .97f),
                        .45f to CxHomeV2Colors.CFF006B43.copy(alpha = .85f),
                        .75f to CxHomeV2Colors.Transparent,
                        1f to CxHomeV2Colors.Transparent
                    )
                )
            )
        }
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HeroInformation(item, ink)
            HeroButtons(item)
        }
    }
}

@Composable private fun HomeV2Presentation.Dots(count: Int, selected: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), horizontalArrangement = Arrangement.Center) {
        repeat(count) {
            Box(
                Modifier.padding(horizontal = 4.dp).size(if (it == selected) 12.dp else 6.dp, 6.dp)
                    .background(if (it == selected) blue else CxHomeV2Colors.CFFCAD5E7, CircleShape)
            )
        }
    }
}

@Composable
private fun HomeV2Presentation.HeroInformation(item: HomeLottery, ink: androidx.compose.ui.graphics.Color) {
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
                Modifier.clip(CircleShape).background(CxHomeV2Colors.C44003322).border(1.dp, ink.copy(alpha = .45f), CircleShape)
                    .uiId("home.0077").clickable { actions.modalityResults(type) }.padding(8.dp)
                ).uiId("home.0078")
        )
    }
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

@Composable
private fun HomeV2Presentation.HeroButtons(item: HomeLottery) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val type = item.tipoModalidade
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (actions.canPool(type)) {
            Button(
                onClick = { actions.pool(item) },
                modifier = (Modifier.weight(1f).heightIn(min = 44.dp)).uiId("home.0085"),
                shape = RoundedCornerShape(7.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CxHomeV2Colors.White, contentColor = navy)
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_compre_seu_bolao_97d03e),
                    fontFamily = bold,
                    fontSize = 12.sp,
                    modifier = Modifier.uiId("home.0086")
                )
            }
        }
        Button(
            onClick = { actions.bet(item) },
            modifier = (Modifier.weight(1f).heightIn(min = 44.dp)).uiId("home.0087"),
            shape = RoundedCornerShape(7.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CxHomeV2Colors.White, contentColor = navy)
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_aposte_0db5db),
                fontFamily = bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .uiId("home.0088")
            )
        }
    }
}
