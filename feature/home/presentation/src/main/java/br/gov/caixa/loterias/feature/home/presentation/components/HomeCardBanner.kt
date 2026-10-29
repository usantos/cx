package br.gov.caixa.loterias.feature.home.presentation.components

import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

import androidx.compose.runtime.getValue
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeSpecialBannerResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeClassicTitle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeTrapezoidResource

@Composable
internal fun LotteryBanner(lottery: HomeLottery) {
    val specialArtwork = homeSpecialBannerResource(lottery)
    if (specialArtwork != null) {
        Box(Modifier.fillMaxWidth().height(71.dp).testTag("home-special-banner"), contentAlignment = Alignment.CenterStart) {
            Image(painterResource(specialArtwork), null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
            BasicText(
                when (lottery.specialBanner) {
                    br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.MEGA_THIRTY_YEARS ->
                        androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.home.R.string.home_mega_sena_30_anos_duas_linhas)
                    br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.LOTECA_FATHERS_DAY ->
                        androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.home.R.string.home_loteca_pais_duas_linhas)
                    null -> lottery.name.lowercase(java.util.Locale.forLanguageTag("pt-BR"))
                },
                Modifier.fillMaxWidth().padding(start = 90.dp, end = 10.dp).testTag("home-special-banner-title"),
                style = homeLegacyTextStyle(18.sp, FontWeight.Bold).copy(
                    fontFamily = CaixaDSFuturaFontFamily, color = CaixaDSColorTokens.White,
                ),
                autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 18.sp, stepSize = 1.sp),
            )
        }
        return
    }
    Row(
        Modifier.fillMaxWidth().height(71.dp).background(lottery.color).testTag("home-normal-banner"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.width(85.dp).fillMaxSize(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Image(
                painter = rememberHomeTrapezoidPainter(br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable(
                    lottery.modalityId, lottery.resultCode, br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork.TRAPEZE) ?: homeTrapezoidResource(lottery)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
            Image(
                painter = painterResource(lottery.cloverRes),
                contentDescription = null,
                modifier = Modifier.padding(start = 23.dp).size(23.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Box(Modifier.weight(1f).fillMaxSize().padding(end = 10.dp), contentAlignment = Alignment.CenterStart) {
            BasicText(
                homeClassicTitle(lottery), Modifier.fillMaxWidth(),
                style = homeLegacyTextStyle(17.sp, FontWeight.Bold).copy(
                    fontFamily = CaixaDSFuturaFontFamily, color = lottery.headerContentColor,
                ),
                autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 17.sp, stepSize = 1.sp),
            )
        }
    }
}

