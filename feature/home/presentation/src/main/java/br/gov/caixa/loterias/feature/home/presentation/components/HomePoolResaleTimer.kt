package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomePoolResaleCountdown
import br.gov.caixa.loterias.feature.home.presentation.mapper.homePoolResaleCountdown
import kotlinx.coroutines.delay

@Composable
internal fun rememberHomePoolResale(individualClosing: String, resaleClosing: String): State<HomePoolResaleCountdown?> {
    val clock = br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration.current.serverTime
    return produceState(homePoolResaleCountdown(individualClosing, resaleClosing, clock.now()), individualClosing, resaleClosing, clock) {
        while (true) {
            value = homePoolResaleCountdown(individualClosing, resaleClosing, clock.now())
            delay(1_000)
        }
    }
}

@Composable
internal fun HomePoolResaleTimer(countdown: HomePoolResaleCountdown, modifier: Modifier = Modifier) {
    if (LocalDensity.current.fontScale > 1f) {
        HomeLargeFontPoolTimer(countdown, modifier)
        return
    }
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val blue = CaixaDSColorTokens.NotificationAction
    val labelStyle = homeLegacyTextStyle(12.sp, FontWeight.SemiBold)
    val titleWidthPx = rememberTextMeasurer().measure(uiStrings.getString(LocalizedR.string.ui_voce_tem_1d937c), labelStyle).size.width
    val titleWidth = with(LocalDensity.current) { titleWidthPx.toDp() }
    val counterStyle = homeLegacyTextStyle(16.sp, FontWeight.Bold)
    val counterHeightPx = rememberTextMeasurer().measure(countdown.label, counterStyle).size.height
    val counterOverflow = with(LocalDensity.current) { counterHeightPx.toDp() - 18.dp }.coerceAtLeast(0.dp) / 2
    Column(
        modifier.fillMaxWidth().height(80.dp).legacyAutomationId("home-pool-resale-timer"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_voce_tem_1d937c),
            color = blue,
            style = labelStyle,
            modifier = Modifier
                .uiId("home.0043")
        )
        Spacer(Modifier.height(5.dp))
        BoxWithConstraints(Modifier.fillMaxWidth().height(18.dp)) {
            val clockX = (maxWidth - titleWidth) / 2 - 15.dp
            Image(
                painterResource(R.drawable.legacy_home_pool_timer),
                null,
                Modifier.offset(x = clockX).size(18.dp),
                colorFilter = ColorFilter.tint(Color(countdown.colorArgb))
            )
            Text(
                countdown.label,
                (Modifier.offset(x = clockX + 28.dp).align(Alignment.CenterStart).wrapContentHeight(unbounded = true)).uiId("home.0044"),
                color = Color(countdown.colorArgb),
                style = counterStyle
            )
        }
        Spacer(Modifier.height(counterOverflow))
        Text(
            uiStrings.getString(LocalizedR.string.ui_para_comprar_o_seu_bolao_58bb1a),
            color = blue,
            style = homeLegacyTextStyle(12.sp),
            modifier = Modifier.uiId("home.0045")
        )
    }
}

@Composable
private fun HomeLargeFontPoolTimer(countdown: HomePoolResaleCountdown, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val blue = CaixaDSColorTokens.NotificationAction
    Column(
        modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).legacyAutomationId("home-pool-resale-timer"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_voce_tem_1d937c),
            color = blue,
            style = homeLegacyTextStyle(12.sp, FontWeight.SemiBold),
            modifier = Modifier.uiId("home.0046")
        )
        Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.legacy_home_pool_timer),
                null,
                Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(Color(countdown.colorArgb))
            )
            Text(
                countdown.label,
                (Modifier.padding(start = 10.dp)).uiId("home.0047"),
                color = Color(countdown.colorArgb),
                style = homeLegacyTextStyle(16.sp, FontWeight.Bold)
            )
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_para_comprar_o_seu_bolao_58bb1a),
            color = blue,
            style = homeLegacyTextStyle(12.sp),
            modifier = Modifier.uiId("home.0048")
        )
    }
}
