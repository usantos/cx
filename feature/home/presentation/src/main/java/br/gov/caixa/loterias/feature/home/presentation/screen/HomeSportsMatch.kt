package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR

@Composable
internal fun HomeSportsMatch(
    match: br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch,
    lotogol: Boolean,
    color: Color,
    page: Int,
    matchCount: Int,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    // A visualização legada rotula estas partidas de resultado como Domingo, independentemente da data do concurso.
    Text(
        uiStrings.getString(LocalizedR.string.ui_domingo_424e93),
        color = color,
        style = homeResultTextStyle.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier.uiId("home.0136")
    )
    val legend = match.legendDescription.takeIf {
        it.length > SPORTS_LEGEND_PREFIX_LENGTH
    }?.substring(SPORTS_LEGEND_PREFIX_LENGTH)?.let { " ($it)" }.orEmpty()
    Row(verticalAlignment = Alignment.Top) {
        HomeResultTeam(
            match.home,
            match.home.won == true && !lotogol,
            color,
            homeResultTeamDescription(match.home, match.homeScoreIndex, lotogol, legend, uiStrings),
            lotogol
        )
        HomeResultVersus(lotogol, match.draw == true, color)
        HomeResultTeam(
            match.away,
            match.away.won == true && !lotogol,
            color,
            homeResultTeamDescription(match.away, match.awayScoreIndex, lotogol, legend, uiStrings),
            lotogol
        )
    }
    Text(
        uiStrings.getString(LocalizedR.string.ui_1_s_de_2_s_ec1761, (page + 1).toString(), matchCount.toString()),
        (Modifier.padding(top = 20.dp)).uiId("home.0138"),
        color = color,
        style = homeResultTextStyle.copy(fontSize = 14.sp)
    )
}

@Composable
internal fun HomeResultVersus(lotogol: Boolean, draw: Boolean, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (lotogol) {
        Image(
            painterResource(R.drawable.legacy_home_result_time_versus_white),
            null,
            Modifier.padding(start = 25.dp, end = 21.dp, top = 60.dp).size(width = 18.dp, height = 17.dp)
        )
    } else {
        Column(
            Modifier.width(80.dp).heightIn(min = 115.dp).background(
                if (draw) {
                    LoteriasCaixaDSColorTokens.SportsResultSelection
                } else {
                    Color.Transparent
                }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.legacy_home_result_time_versus_white),
                null,
                Modifier.padding(top = 16.dp, bottom = 10.dp).size(40.dp)
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_empate_0b5021),
                color = color,
                style = homeResultTextStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .uiId("home.0137")
            )
        }
    }
}

private const val SPORTS_LEGEND_PREFIX_LENGTH = 4
