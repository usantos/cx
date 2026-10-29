package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.mapper.formatCompactPrize
import br.gov.caixa.loterias.feature.home.presentation.mapper.formatHomeDrawDate
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind

@Composable
internal fun CollapsedLotteryContent(lottery: HomeLottery) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (lottery.kind != HomeLotteryKind.LOTTERY) {
        SpecialLotteryContent(lottery.kind)
        return
    }
    val poolResale by rememberHomePoolResale(lottery.closingDateTime, lottery.poolResaleClosingDateTime)
    val remainingTime by rememberHomeCardCountdown(lottery.closingDateTime)
    val drawDate = if (lottery.resultCode == "loteca") lottery.closingDateTime else lottery.drawDate
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
        Image(
            painterResource(br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevos_background_card),
            null,
            Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            HomeEstimatedPrize(lottery)
            if (drawDate.isNotBlank() || lottery.closingDateTime.isNotBlank()) {
                Box(
                    Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 8.dp).height(1.dp)
                        .background(CaixaDS.colors.line)
                )
            }
            HomeCardDrawDate(lottery, drawDate)
            if (lottery.closingDateTime.isNotBlank() && poolResale == null) {
                HomeCardClosingTime(lottery, remainingTime)
            }
            Spacer(Modifier.weight(1f))
        }
        poolResale?.let { HomePoolResaleTimer(it, Modifier.align(Alignment.Center)) }
    }
}

@Composable
internal fun HomeEstimatedPrize(lottery: HomeLottery, enlarged: Boolean = false) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (lottery.estimatedPrize.signum() > 0) {
        Text(
            formatBrazilianCurrency(lottery.estimatedPrize),
            (Modifier.then(if (enlarged) Modifier.fillMaxWidth().padding(horizontal = 16.dp) else Modifier).padding(top = 25.dp))
                .uiId("home.0001"),
            color = lottery.listText,
            textAlign = TextAlign.Center,
            style = homeLegacyTextStyle(20.sp, FontWeight.Bold, xmlTextSize = false)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_1_s_4c75be, formatCompactPrize(lottery.estimatedPrize)),
            color = lottery.listText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = homeLegacyTextStyle(16.sp, FontWeight.Bold, xmlTextSize = false),
            modifier = Modifier.uiId("home.0002")
        )
        Text(
            buildAnnotatedString {
                append(uiStrings.getString(LocalizedR.string.ui_premio_estimado_do_concurso_4dddfc))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(lottery.contestNumber.toString()) }
            },
            (Modifier.padding(top = 3.dp)).uiId("home.0003"),
            color = CaixaDSColorTokens.NotificationText,
            style = homeLegacyTextStyle(13.8.sp)
        )
    } else {
        Text(
            buildAnnotatedString {
                append(uiStrings.getString(LocalizedR.string.ui_proximo_concurso_749d77))
                withStyle(SpanStyle(color = CaixaDSColorTokens.NotificationText, fontSize = 18.sp)) {
                    append(lottery.contestNumber.toString())
                }
                append(uiStrings.getString(LocalizedR.string.ui_aguardando_estimativa_de_premio_efd8b1))
            },
            (Modifier.padding(top = 25.dp)).uiId("home.0004"),
            color = lottery.listText,
            style = homeLegacyTextStyle(14.sp, FontWeight.Bold, xmlTextSize = false),
            textAlign = TextAlign.Center
        )
        // A legenda legada fica INVISIBLE, preservando sua altura medida e margem.
        Text(
            uiStrings.getString(LocalizedR.string.ui_premio_estimado_do_concurso_b1224f),
            (Modifier.padding(top = 3.dp).alpha(0f).clearAndSetSemantics { }).uiId("home.0005"),
            style = homeLegacyTextStyle(13.8.sp)
        )
    }
}

@Composable
internal fun HomeCardDrawDate(lottery: HomeLottery, drawDate: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (drawDate.isNotBlank()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 19.dp, end = 19.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.installed_home_calendario),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                colorFilter = ColorFilter.tint(lottery.listText)
            )
            Column(Modifier.padding(start = 10.dp)) {
                Text(
                    if (lottery.resultCode in listOf("loteca", "lotogol")) {
                        uiStrings.getString(LocalizedR.string.ui_encerramento_f0dcba)
                    } else {
                        uiStrings.getString(LocalizedR.string.ui_sorteio_a49bf4)
                    },
                    color = CaixaDSColorTokens.NotificationText,
                    style = homeLegacyTextStyle(12.sp),
                    modifier = Modifier.uiId("home.0006")
                )
                Text(
                    formatHomeDrawDate(drawDate),
                    color = CaixaDSColorTokens.NotificationText,
                    style = homeLegacyTextStyle(12.sp, FontWeight.SemiBold),
                    modifier = Modifier.uiId("home.0007")
                )
            }
        }
    }
}

@Composable
internal fun HomeCardClosingTime(lottery: HomeLottery, remainingTime: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val salesClosed = remainingTime == "Encerradas"
    Row(
        Modifier.fillMaxWidth().padding(start = 19.dp, end = 19.dp, top = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(lottery.clockRes),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            colorFilter = ColorFilter.tint(lottery.listText)
        )
        Column(Modifier.padding(start = 10.dp)) {
            Text(
                if (salesClosed) {
                    uiStrings.getString(LocalizedR.string.ui_apostas_e73ece)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_apostas_se_encerram_em_d9ca00)
                },
                color = CaixaDSColorTokens.NotificationText,
                style = homeLegacyTextStyle(12.sp),
                modifier = Modifier.uiId("home.0008")
            )
            Text(
                remainingTime,
                color = CaixaDSColorTokens.NotificationText,
                style = homeLegacyTextStyle(12.sp, FontWeight.SemiBold),
                modifier = Modifier.uiId("home.0009")
            )
        }
    }
}
