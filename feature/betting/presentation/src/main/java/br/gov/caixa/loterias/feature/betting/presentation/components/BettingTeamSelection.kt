package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

/** Busca e seleção de times em Compose, com tipografia, cores e métricas do app Caixa. */
@Composable
internal fun BettingTeamsContent(state: BettingGameUiState, onQueryChanged: (String) -> Unit, onTeamSelected: (SportsTeam) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val teams = state.game?.teams.orEmpty()
    val query = state.teamQuery.trim()
    val selectedLabel = state.selectedTeam?.let {
        listOf(it.name, it.state).filter(String::isNotBlank).joinToString("-")
    }
    val filtered = if (query.isBlank() || query == selectedLabel) {
        teams
    } else {
        teams.filter { it.name.contains(query, true) || it.state.contains(query, true) }
    }

    Column(Modifier.fillMaxWidth()) {
        TeamQueryField(state.teamQuery, onQueryChanged)

        Column(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 16.dp)) {
            filtered.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { team ->
                        val checked = state.selectedTeam?.number == team.number
                        TeamSelectionCard(
                            team = team,
                            selected = checked,
                            onClick = { onTeamSelected(team) },
                            modifier = (Modifier.weight(1f).padding(8.dp)).uiId("bet.0067")
                        )
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f).padding(8.dp)) }
                }
            }
        }
        if (teams.isEmpty()) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_times_indisponiveis_para_este_concur_e0d3b5),
                (Modifier.padding(16.dp)).uiId("bet.0068"),
                color = CaixaDSColorTokens.SocialGraphite,
                fontFamily = CaixaDSFontFamily
            )
        }
    }
}

@Composable
private fun TeamSelectionCard(team: SportsTeam, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val selectionColor = if (selected) CaixaDSColorTokens.TimemaniaTeamSelection else Color.Transparent
    Column(
        modifier
            .height(115.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .then(if (selected) Modifier.border(2.dp, selectionColor, RoundedCornerShape(8.dp)) else Modifier)
            .uiId("bet.0069").selectable(selected = selected, role = Role.Checkbox, onClick = onClick)
            .semantics { contentDescription = team.name },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TeamShield(team, Modifier.padding(top = 16.dp).size(40.dp))
        Text(
            team.name,
            color = if (selected) selectionColor else CaixaDSColorTokens.BetAction,
            fontFamily = CaixaDSFontFamily,
            fontSize = 14.2.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)), modifier = Modifier.uiId("bet.0070")
        )
        Text(
            team.state,
            color = if (selected) selectionColor else CaixaDSColorTokens.BetAction,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 14.2.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)), modifier = Modifier.uiId("bet.0071")
        )
    }
}

@Composable
private fun TeamQueryField(query: String, onQueryChanged: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = (
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CaixaDSColorTokens.White)
                .border(1.dp, CaixaDSColorTokens.SimulationMuted, RoundedCornerShape(8.dp))
                .semantics { contentDescription = uiStrings.getString(LocalizedR.string.ui_nome_do_time_c897b8) }
            ).uiId("bet.0065"),
        singleLine = true,
        textStyle = TextStyle(
            color = CaixaDSColorTokens.SocialGraphite,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 16.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        decorationBox = { innerTextField ->
            Row(
                Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(R.drawable.installed_betting_ic_lupa), null, Modifier.size(18.dp))
                Box(Modifier.weight(1f).padding(start = 8.dp)) {
                    if (query.isEmpty()) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_nome_do_time_c897b8),
                            color = CaixaDSColorTokens.SocialGraphite,
                            fontFamily = CaixaDSRegularFontFamily,
                            fontSize = 16.sp,
                            modifier = Modifier.uiId("bet.0066")
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}
