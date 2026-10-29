package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam

@Composable
internal fun HomeSportsResults(result: DrawResult, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lotogol = result.lotogolMatches.isNotEmpty()
    val matches = if (lotogol) result.lotogolMatches else result.lotecaMatches
    val pager = key(result.modality, result.contest) { rememberPagerState(pageCount = { matches.size }) }
    HorizontalPager(
        pager,
        Modifier.legacyAutomationId(
            "home-result-sports-pager"
        ).fillMaxWidth().padding(top = if (lotogol) 49.dp else 36.dp, bottom = if (lotogol) 0.dp else 20.dp)
    ) { page ->
        val match = matches[page]
        Column(
            Modifier.fillMaxWidth().graphicsLayer {
                val distance = kotlin.math.abs(pager.currentPage - page + pager.currentPageOffsetFraction).coerceIn(0f, 1f)
                val scale = 1f - 0.2f * distance
                scaleX = scale
                scaleY = scale
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // A visualização legada rotula estas partidas de resultado como Domingo, independentemente da data do concurso.
            Text(
                uiStrings.getString(LocalizedR.string.ui_domingo_424e93),
                color = color,
                style = homeResultTextStyle.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.uiId("home.0136")
            )
            val legend = match.legendDescription.takeIf { it.length > 4 }?.substring(4)?.let { " ($it)" }.orEmpty()
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
                uiStrings.getString(LocalizedR.string.ui_1_s_de_2_s_ec1761, (page + 1).toString(), matches.size.toString()),
                (Modifier.padding(top = 20.dp)).uiId("home.0138"),
                color = color,
                style = homeResultTextStyle.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
private fun HomeResultTeam(team: DrawSportsTeam, selected: Boolean, color: Color, description: String, lotogol: Boolean) {
    if (lotogol) {
        Column(Modifier.width(105.dp).height(150.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            HomeResultShield(team.shieldBase64, (Modifier.padding(top = 24.dp, bottom = 10.dp).size(40.dp)).uiId("home.0139"))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(team.name) }
                    append(" / " + if (team.nationalTeam == true) team.countryCode else team.state)
                },
                color = color,
                textAlign = TextAlign.Center,
                style = homeResultTextStyle.copy(fontSize = 12.sp),
                modifier = Modifier
                    .uiId("home.0140")
            )
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(40.dp).background(CaixaDSColorTokens.White, CircleShape), contentAlignment = Alignment.Center) {
                Text(
                    description,
                    color = CaixaDSColorTokens.Lotogol,
                    style = homeResultTextStyle.copy(fontSize = 14.1.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.uiId("home.0141")
                )
            }
        }
        return
    }
    Column(
        Modifier.width(
            80.dp
        ).heightIn(min = 115.dp).background(if (selected) CaixaDSColorTokens.SportsResultSelection else Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeResultShield(team.shieldBase64, (Modifier.padding(top = 16.dp, bottom = 10.dp).size(40.dp)).uiId("home.0142"))
        Text(
            team.name,
            color = color,
            textAlign = TextAlign.Center,
            style = homeResultTextStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.uiId("home.0143")
        )
        Text(
            description,
            color = color,
            textAlign = TextAlign.Center,
            style = homeResultTextStyle.copy(fontSize = 13.sp),
            modifier = Modifier.uiId("home.0144")
        )
    }
}

@Composable
internal fun HomeResultShield(base64: String?, modifier: Modifier = Modifier) {
    val bitmap = androidx.compose.runtime.remember(base64) {
        base64?.let { encoded ->
            try {
                val bytes = android.util.Base64.decode(encoded.substringAfter("base64,", encoded), android.util.Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
    val bottomPadding = with(LocalDensity.current) { 12.toDp() }
    if (bitmap !=
        null
    ) {
        Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = modifier.padding(bottom = bottomPadding))
    } else {
        Image(painterResource(R.drawable.legacy_home_result_time_padrao), null, modifier.padding(bottom = bottomPadding))
    }
}

/** Utils.getUfTime(EquipeDTO), incluindo o comportamento original para país vazio. */
internal fun homeSportsTeamRegion(team: DrawSportsTeam): String = when {
    team.countryCode.isBlank() -> ""
    !team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true) -> "/${team.countryCode}"
    team.state.isNotBlank() -> "/${team.state}"
    else -> ""
}

@Composable
private fun HomeResultVersus(lotogol: Boolean, draw: Boolean, color: Color) {
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
                    CaixaDSColorTokens.SportsResultSelection
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

private fun homeResultTeamDescription(
    team: DrawSportsTeam,
    scoreIndex: Int?,
    lotogol: Boolean,
    legend: String,
    uiStrings: android.content.res.Resources,
): String = if (lotogol) {
    team.score.ifBlank {
        scoreIndex?.let { if (it >= 4) uiStrings.getString(LocalizedR.string.ui_4_ou_mais_4bb81b) else "$it" }.orEmpty()
    }
} else {
    homeSportsTeamRegion(team) + " " + legend
}
