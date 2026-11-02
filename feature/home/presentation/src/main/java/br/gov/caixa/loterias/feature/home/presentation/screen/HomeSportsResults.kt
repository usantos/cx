package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam

@Composable
internal fun HomeSportsResults(result: DrawResult, color: Color) {
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
            HomeSportsMatch(match, lotogol, color, page, matches.size)
        }
    }
}

/** Utils.getUfTime(EquipeDTO), incluindo o comportamento original para país vazio. */
internal fun homeSportsTeamRegion(team: DrawSportsTeam): String = when {
    team.countryCode.isBlank() -> ""
    !team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true) -> "/${team.countryCode}"
    team.state.isNotBlank() -> "/${team.state}"
    else -> ""
}

internal fun homeResultTeamDescription(
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
