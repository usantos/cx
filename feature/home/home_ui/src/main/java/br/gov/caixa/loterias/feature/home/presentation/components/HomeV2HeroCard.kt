package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable internal fun HomeV2Presentation.Hero(item: HomeLottery) {
    val type = item.tipoModalidade
    val ink = if (type == HomeV2Modality.MEGA_SENA && LocalLoteriasCaixaDSMegaSenaCampaign.current != LoteriasCaixaDSCampaign.DEFAULT) {
        loteriasCaixaDSLotteryStyle(2).content
    } else {
        CxHomeV2Colors.White
    }
    Box(
        Modifier.fillMaxWidth().heightIn(min = 220.dp).clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(colors(type)))
    ) {
        if (type == HomeV2Modality.MEGA_SENA && LocalLoteriasCaixaDSMegaSenaCampaign.current == LoteriasCaixaDSCampaign.DEFAULT) {
            Image(
                painterResource(DeliveryR.drawable.home_v2_mega_corporativo),
                null,
                Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterEnd
            )
            Box(
                Modifier.matchParentSize().background(
                    Brush.horizontalGradient(
                        0f to CxHomeV2Colors.CFF006B43.copy(alpha = HERO_SCRIM_START_ALPHA),
                        HERO_SCRIM_MIDDLE_STOP to CxHomeV2Colors.CFF006B43.copy(alpha = HERO_SCRIM_MIDDLE_ALPHA),
                        HERO_SCRIM_END_STOP to CxHomeV2Colors.Transparent,
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

@Composable
internal fun HomeV2Presentation.HeroButtons(item: HomeLottery) {
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
                    uiStrings.getString(DeliveryR.string.ui_compre_seu_bolao_97d03e),
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
                uiStrings.getString(DeliveryR.string.ui_aposte_0db5db),
                fontFamily = bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .uiId("home.0088")
            )
        }
    }
}

private const val HERO_SCRIM_START_ALPHA = .97f
private const val HERO_SCRIM_MIDDLE_STOP = .45f
private const val HERO_SCRIM_MIDDLE_ALPHA = .85f
private const val HERO_SCRIM_END_STOP = .75f
