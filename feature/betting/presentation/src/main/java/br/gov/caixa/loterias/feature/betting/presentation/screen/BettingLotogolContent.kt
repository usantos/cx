package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import coil3.compose.AsyncImage

@Composable
internal fun LotogolBetContent(state: BettingGameUiState, actions: BettingGameActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val game = requireNotNull(state.game)
    Column(
        Modifier.fillMaxSize().uiId("bet.0254").verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_informe_o_placar_de_cada_jogo_4cf784),
            color = CaixaDSColorTokens.Lotogol,
            fontWeight = FontWeight.Bold,
            modifier = (Modifier.padding(top = 16.dp, bottom = 4.dp)).uiId("bet.0255")
        )
        game.matches.forEach { match ->
            Text(
                uiStrings.getString(LocalizedR.string.ui_jogo_1_s_aa1e61, match.number.toString().padStart(2, '0')),
                color = CaixaDSColorTokens.Blue700,
                fontSize = 16.sp,
                modifier = (Modifier.padding(top = 20.dp, bottom = 8.dp)).uiId("bet.0256")
            )
            val scores = state.lotogolScores[match.number] ?: (null to null)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LotogolTeamScore(
                    team = match.home,
                    selected = scores.first,
                    onScore = { actions.selectLotogolScore(match.number, true, it) },
                    modifier = (Modifier.weight(1f)).uiId("bet.0257")
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_x_4b68ab),
                    color = CaixaDSColorTokens.Blue700,
                    fontWeight = FontWeight.Bold,
                    modifier = (Modifier.padding(8.dp)).uiId("bet.0258")
                )
                LotogolTeamScore(
                    team = match.away,
                    selected = scores.second,
                    onScore = { actions.selectLotogolScore(match.number, false, it) },
                    modifier = (Modifier.weight(1f)).uiId("bet.0259")
                )
            }
            CaixaDSDivider()
        }
        OutlinedLegacyButton(uiStrings.getString(LocalizedR.string.ui_limpar_aposta_aa71b7), actions.clearSelection)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LotogolTeamScore(team: SportsTeam, selected: Int?, onScore: (Int) -> Unit, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = team.shieldBase64?.let { if (it.startsWith("data:")) it else "data:image/png;base64,$it" },
            contentDescription = uiStrings.getString(LocalizedR.string.ui_escudo_1_s_661b15, team.name),
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            team.name,
            color = CaixaDSColorTokens.TutorialGray,
            fontSize = 14.2.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .uiId("bet.0260")
        )
        FlowRow(
            Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            (0..5).forEach { score ->
                Box(
                    Modifier.size(28.dp)
                        .background(
                            if (selected == score) CaixaDSColorTokens.Lotogol else CaixaDS.colors.background,
                            CircleShape
                        )
                        .border(1.dp, CaixaDSColorTokens.Lotogol, CircleShape)
                        .uiId("bet.0261").selectable(selected = selected == score, role = Role.RadioButton) { onScore(score) }
                        .semantics {
                            contentDescription =
                                if (score ==
                                    5
                                ) {
                                    "${team.name}, 5 ou mais gols"
                                } else {
                                    "${team.name}, $score ${if (score == 1) "gol" else "gols"}"
                                }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (score == 5) uiStrings.getString(LocalizedR.string.ui_5_fa676d) else score.toString(),
                        color = if (selected == score) CaixaDSColorTokens.White else CaixaDSColorTokens.Lotogol,
                        fontSize = 10.sp,
                        modifier = Modifier.uiId("bet.0262")
                    )
                }
            }
        }
    }
}

@Composable
private fun OutlinedLegacyButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = (Modifier.fillMaxWidth().height(54.dp)).uiId("bet.0263"),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CaixaDS.colors.brand),
        contentPadding = PaddingValues(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CaixaDS.colors.brand)
    ) { Text(text, modifier = Modifier.uiId("bet.0264")) }
}
