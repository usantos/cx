package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultSportsSpokenDescription

internal fun sportsResultLabel(match: DrawSportsMatch, lotogol: Boolean): String {
    fun score(value: String, index: Int?) = value.ifBlank {
        when {
            index == null -> ""
            index >= LOTOGOL_OPEN_SCORE_THRESHOLD -> "4 ou mais"
            else -> index.toString()
        }
    }
    val homeScore = score(match.home.score, if (lotogol) match.homeScoreIndex else null)
    val awayScore = score(match.away.score, if (lotogol) match.awayScoreIndex else null)
    return listOfNotNull(
        match.number?.let { "$it." },
        match.home.name,
        homeScore.takeIf(String::isNotBlank),
        "×",
        awayScore.takeIf(String::isNotBlank),
        match.away.name
    ).joinToString(" ")
}

internal fun sportsWinnerLabel(match: DrawSportsMatch): String = when {
    match.draw == true -> "Empate"
    match.home.won == true -> "Vencedor: ${match.home.name}"
    match.away.won == true -> "Vencedor: ${match.away.name}"
    else -> ""
}

@Composable
internal fun DrawSportsResults(result: DrawResult, color: Color) {
    if (result.lotecaMatches.isNotEmpty()) DrawLotecaResults(result.lotecaMatches, color)
    result.lotogolMatches.forEach { match ->
        Column(
            Modifier.fillMaxWidth().padding(vertical = 8.dp).clearAndSetSemantics {
                contentDescription = resultSportsSpokenDescription(match, lotogol = true)
            }
        ) {
            Text(sportsResultLabel(match, true), color = color, fontWeight = FontWeight.Bold, modifier = Modifier.uiId("results.0011"))
            sportsWinnerLabel(match).takeIf(String::isNotBlank)?.let { Text(it, color = color, modifier = Modifier.uiId("results.0012")) }
            if (match.legendDescription.isNotBlank()) {
                Text(
                    listOf(match.legendLetter, match.legendDescription).filter(String::isNotBlank).joinToString(" • "),
                    color = color,
                    modifier = Modifier.uiId("results.0013")
                )
            }
        }
    }
}

private const val LOTOGOL_OPEN_SCORE_THRESHOLD = 4
