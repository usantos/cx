package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import kotlin.math.roundToInt

private const val LOTECA_LEGEND_PREFIX_LENGTH = 4
private const val LOTECA_CHOICE_TEXT_SIZE = 14

private fun detailTeamName(team: SportsTeam): String {
    if (team.selectionIndicator) return team.name
    val suffix = if (team.countryCode.isNotBlank() &&
        (!team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true))
    ) {
        team.countryCode
    } else {
        team.state
    }
    return team.name + suffix.takeIf { it.isNotBlank() }?.let { "/$it" }.orEmpty()
}

@Composable
internal fun DetailLotecaMatch(index: Int, selection: LotecaMatchSelection) {
    val legend = selection.match.legendDescription.takeIf {
        it.length > LOTECA_LEGEND_PREFIX_LENGTH
    }?.drop(LOTECA_LEGEND_PREFIX_LENGTH)?.let { "  ($it)" }.orEmpty()
    Column(Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailLotecaChoice("${detailTeamName(selection.match.home)}$legend", selection.homeWin, "pool-detail-loteca-$index-home")
        DetailLotecaChoice("X", selection.draw, "pool-detail-loteca-$index-draw")
        DetailLotecaChoice(
            "${detailTeamName(selection.match.away)}$legend",
            selection.awayWin,
            "pool-detail-loteca-$index-away",
            endAtLabelBottom = true
        )
        DetailDivider(Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun DetailLotecaChoice(label: String, checked: Boolean, tag: String, endAtLabelBottom: Boolean = false) {
    Layout(modifier = Modifier.fillMaxWidth().padding(start = 16.dp), content = {
        Box(
            Modifier.size(28.dp, if (checked) 28.dp else 27.dp).legacyAutomationId(tag)
                .semantics {
                    selected = checked
                    role = Role.RadioButton
                    contentDescription = label
                }
        ) {
            // ImageView da CX desenha este vetor translúcido como fundo e como imagem tonalizada.
            repeat(2) {
                Icon(
                    painterResource(
                        if (checked) R.drawable.legacy_marketplace_ic_radio_checked else R.drawable.legacy_marketplace_ic_radio_unchecked
                    ),
                    null,
                    (Modifier.fillMaxSize()).uiId("pool.0051"),
                    tint = LoteriasCaixaDSColorTokens.Black
                )
            }
        }
        Text(
            label,
            (Modifier.fillMaxWidth()).uiId("pool.0052"),
            style = detailText(LOTECA_CHOICE_TEXT_SIZE, FontWeight.Bold),
            color = LoteriasCaixaDSColorTokens.Black
        )
    }) { measurables, constraints ->
        val indicator = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val gap = 8.dp.roundToPx()
        val textWidth = (constraints.maxWidth - indicator.width - gap).coerceAtLeast(0)
        val text = measurables[1].measure(Constraints(maxWidth = textWidth))
        val textTop = ((indicator.height - text.height) / 2f).roundToInt()
        // O divisor da CX é restrito abaixo do TextView do visitante, e não do botão de opção mais alto.
        val height = if (endAtLabelBottom) textTop + text.height else indicator.height
        layout(constraints.maxWidth, height) {
            indicator.placeRelative(0, 0)
            text.placeRelative(indicator.width + gap, textTop)
        }
    }
}
