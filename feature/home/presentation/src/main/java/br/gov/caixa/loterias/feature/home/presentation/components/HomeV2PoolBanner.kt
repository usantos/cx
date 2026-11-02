package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery

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
                Brush.horizontalGradient(
                    listOf(
                        CxHomeV2Colors.CFF003B71.copy(alpha = POOL_BANNER_SCRIM_START_ALPHA),
                        CxHomeV2Colors.CFF003B71.copy(alpha = POOL_BANNER_SCRIM_END_ALPHA)
                    )
                )
            )
        )
        Column(
            Modifier.align(
                Alignment.CenterStart
            ).fillMaxWidth(POOL_BANNER_TEXT_WIDTH_FRACTION).padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
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

private const val POOL_BANNER_SCRIM_START_ALPHA = .50f
private const val POOL_BANNER_SCRIM_END_ALPHA = .12f
private const val POOL_BANNER_TEXT_WIDTH_FRACTION = .78f
