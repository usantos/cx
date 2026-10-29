package br.gov.caixa.loterias.feature.home.presentation.state

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.home.presentation.mapper.name

enum class HomeSpecialBanner { MEGA_THIRTY_YEARS, LOTECA_FATHERS_DAY }

enum class HomeLotteryKind { LOTTERY, INSTANT, COMBO }

data class HomeLottery(
    val modalityId: Int,
    val contestType: String,
    val resultCode: String,
    val name: String,
    val contestNumber: Int,
    val estimatedPrize: BigDecimal,
    val minimumBetAmount: BigDecimal,
    val drawDate: String,
    val closingDateTime: String,
    val open: Boolean,
    val kind: HomeLotteryKind,
    val color: Color,
    val darkColor: Color,
    val headerContentColor: Color,
    @DrawableRes val cloverRes: Int,
    @DrawableRes val calendarRes: Int,
    @DrawableRes val clockRes: Int,
    val octoberRoseTheme: Boolean = false,
    val listText: Color = color,
    val darkContent: Color = headerContentColor,
    val specialBanner: HomeSpecialBanner? = null,
    val poolResaleClosingDateTime: String = "",
)
