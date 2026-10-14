package br.gov.caixa.loterias.feature.betting

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.clearAndSetSemantics
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ColorFilter
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
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import br.gov.caixa.loterias.core.domain.betting.SportsTeam

/** Busca e seleção de times em Compose, com tipografia, cores e métricas do app Caixa. */
@Composable
internal fun BettingLegacyTeams(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val teams = state.game?.teams.orEmpty()
    val query = state.teamQuery.trim()
    val selectedLabel = state.selectedTeam?.let {
        listOf(it.name, it.state).filter(String::isNotBlank).joinToString("-")
    }
    val filtered = if (query.isBlank() || query == selectedLabel) teams
    else teams.filter { it.name.contains(query, true) || it.state.contains(query, true) }

    Column(Modifier.fillMaxWidth()) {
        BasicTextField(
            value = state.teamQuery,
            onValueChange = viewModel::updateTeamQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CaixaDSColorTokens.White)
                .border(1.dp, CaixaDSColorTokens.LegacySimulationMuted, RoundedCornerShape(8.dp))
                .semantics { contentDescription = "Nome do time" },
            singleLine = true,
            textStyle = TextStyle(
                color = CaixaDSColorTokens.LegacySocialGraphite,
                fontFamily = CaixaDSLegacyRegularFontFamily,
                fontSize = 16.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox = { innerTextField ->
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painterResource(R.drawable.installed_betting_ic_lupa), null, Modifier.size(18.dp))
                    Box(Modifier.weight(1f).padding(start = 8.dp)) {
                        if (state.teamQuery.isEmpty()) {
                            Text(
                                "Nome do time",
                                color = CaixaDSColorTokens.LegacySocialGraphite,
                                fontFamily = CaixaDSLegacyRegularFontFamily,
                                fontSize = 16.sp,
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )

        Column(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 16.dp)) {
            filtered.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { team ->
                        val checked = state.selectedTeam?.number == team.number
                        TeamSelectionCard(
                            team = team,
                            selected = checked,
                            onClick = { viewModel.selectTeam(team) },
                            modifier = Modifier.weight(1f).padding(8.dp),
                        )
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f).padding(8.dp)) }
                }
            }
        }
        if (teams.isEmpty()) {
            Text(
                "Times indisponíveis para este concurso.",
                Modifier.padding(16.dp),
                color = CaixaDSColorTokens.LegacySocialGraphite,
                fontFamily = CaixaDSFontFamily,
            )
        }
    }
}

@Composable
private fun TeamSelectionCard(
    team: SportsTeam,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectionColor = if (selected) CaixaDSColorTokens.LegacyTimemaniaTeamSelection else Color.Transparent
    Column(
        modifier
            .height(115.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .then(if (selected) Modifier.border(2.dp, selectionColor, RoundedCornerShape(8.dp)) else Modifier)
            .selectable(selected = selected, role = Role.Checkbox, onClick = onClick)
            .semantics { contentDescription = team.name },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TeamShield(team, Modifier.padding(top = 16.dp).size(40.dp))
        Text(
            team.name,
            color = if (selected) selectionColor else CaixaDSColorTokens.LegacyBetAction,
            fontFamily = CaixaDSFontFamily,
            fontSize = 14.2.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
        )
        Text(
            team.state,
            color = if (selected) selectionColor else CaixaDSColorTokens.LegacyBetAction,
            fontFamily = CaixaDSLegacyRegularFontFamily,
            fontSize = 14.2.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
        )
    }
}

@Composable
private fun TeamShield(team: SportsTeam, modifier: Modifier = Modifier) {
    val bitmap = remember(team.shieldBase64) {
        team.shieldBase64?.let { encoded ->
            runCatching {
                val bytes = Base64.decode(encoded.substringAfter("base64,", encoded), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }.getOrNull()
        }
    }
    if (bitmap != null) {
        Image(bitmap, contentDescription = null, modifier = modifier.padding(bottom = 8.dp))
    } else {
        Image(painterResource(R.drawable.installed_betting_time_padrao), null, modifier.padding(bottom = 8.dp))
    }
}

/** Grade de partidas da Loteca em Compose, com bordas unidas e estados de seleção do legado. */
@Composable
internal fun BettingLegacyLoteca(state: BettingGameUiState, viewModel: BettingGameViewModel, onQuantity: () -> Unit, onHelp: () -> Unit) {
    val game = requireNotNull(state.game)
    val metrics = TextStyle(lineHeight = 16.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().background(CaixaDSColorTokens.LegacySimulationToolbar).padding(horizontal = 16.dp),
        ) {
            Box(Modifier.fillMaxWidth().padding(top = 14.dp)) {
                Text(
                    "Suas apostas",
                    Modifier.padding(start = 8.dp, end = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = CaixaDSFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = CaixaDSColorTokens.LegacySocialGraphite,
                    style = metrics,
                )
                Row(Modifier.matchParentSize(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Row(Modifier.clickable(onClick = onHelp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Como jogar", Modifier.padding(end = 8.dp), fontFamily = CaixaDSLegacyRegularFontFamily,
                            fontSize = 16.sp, color = CaixaDSColorTokens.LegacySocialGraphite, style = metrics)
                        Image(painterResource(R.drawable.ic_info), null, Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(CaixaDSColorTokens.LegacySocialGraphite))
                    }
                }
            }
            FlowRow(
                Modifier.fillMaxWidth().padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val counts = listOf(
                    "Partidas: " to state.lotecaOutcomes.count { it.value.isNotEmpty() },
                    "Simples: " to state.lotecaOutcomes.values.count { it.size == 1 },
                    "Duplas: " to state.lotecaOutcomes.values.count { it.size == 2 },
                    "Triplas: " to state.lotecaOutcomes.values.count { it.size == 3 },
                )
                counts.forEachIndexed { index, (label, count) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (index > 0) {
                            Box(
                                Modifier.padding(end = 8.dp).width(1.dp).height(20.dp)
                                    .background(CaixaDSColorTokens.LegacySocialGraphite.copy(alpha = .3f)),
                            )
                        }
                        Text(label, fontFamily = CaixaDSLegacyRegularFontFamily, fontSize = 14.sp, color = CaixaDSColorTokens.LegacySocialGraphite, style = metrics)
                        Text(count.toString(), Modifier.testTag("loteca-count-${listOf("partidas", "simples", "duplas", "triplas")[index]}"), fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = CaixaDSColorTokens.LegacyLotecaCounter, style = metrics)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp).height(45.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Box(Modifier.width(112.dp).height(45.dp).testTag("loteca-quantity")) {
                BettingDSButton("${state.numberQuantity} palpites", onQuantity,
                    Modifier.fillMaxWidth().padding(vertical = 6.dp).fillMaxHeight(),
                    CaixaDSColorTokens.Loteca, CaixaDSColorTokens.White, textSize = 14f, corner = 12f,
                    icon = R.drawable.ic_arrow_down, semiBold = true)
            }
            Box(Modifier.height(40.dp), contentAlignment = Alignment.Center) {
                Text("Selecionados: ${state.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size)}/${state.numberQuantity}",
                    fontFamily = CaixaDSLegacyRegularFontFamily, fontSize = 14.sp,
                    color = CaixaDSColorTokens.LegacySocialGraphite, style = metrics)
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp).weight(1f).verticalScroll(rememberScrollState())) {
            game.matches.forEach { match ->
                val selected = state.lotecaOutcomes[match.number].orEmpty()
                val suffix = when {
                    match.legendDescription.contains("FEM.") || match.legendDescription.contains("FEMININO") -> " (feminino)"
                    match.legendDescription.contains("SUB17") -> " (sub 17)"
                    match.legendDescription.contains("SUB20") -> " (sub 20)"
                    else -> ""
                }
                Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(
                        "Jogo ${match.number.toString().padStart(2, '0')}$suffix",
                        Modifier.padding(bottom = 8.dp),
                        fontFamily = CaixaDSLegacyRegularFontFamily,
                        fontSize = 16.sp,
                        color = CaixaDSColorTokens.LegacyCaixaBlue,
                        style = metrics,
                    )
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        LotecaOutcome.entries.forEachIndexed { index, outcome ->
                            val team = when (index) { 0 -> match.home; 2 -> match.away; else -> null }
                            LotecaOutcomeCard(
                                index = index,
                                team = team,
                                selected = outcome in selected,
                                onClick = { viewModel.toggleLotecaOutcome(match.number, outcome) },
                                modifier = Modifier.weight(1f).fillMaxHeight().testTag("loteca-${match.number}-$outcome"),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(119.dp))
        }
    }
}

@Composable
private fun LotecaOutcomeCard(
    index: Int,
    team: SportsTeam?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = when (index) {
        0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
        2 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
        else -> RoundedCornerShape(0.dp)
    }
    val label = team?.name ?: "Empate"
    Column(
        modifier
            .clip(shape)
            .background(if (selected) CaixaDSColorTokens.LegacyLotecaSelectedBackground else CaixaDSColorTokens.White)
            .border(2.dp, if (selected) CaixaDSColorTokens.LegacyLotecaCounter else CaixaDSColorTokens.LegacyLotecaChoiceBorder, shape)
            .clickable(onClick = onClick)
            .padding(8.dp)
            .semantics { contentDescription = label; this.selected = selected },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (team == null) {
            Image(
                painterResource(R.drawable.installed_betting_ic_empate),
                null,
                Modifier.padding(top = 16.dp, bottom = 10.dp).size(18.dp),
                colorFilter = ColorFilter.tint(if (selected) CaixaDSColorTokens.Blue700 else CaixaDSColorTokens.LegacyBetAction),
            )
        } else {
            TeamShield(team, Modifier.padding(top = 12.dp).size(35.dp))
        }
        Text(
            label,
            Modifier.fillMaxWidth(),
            fontFamily = CaixaDSLegacyFuturaFontFamily,
            fontSize = 14.sp,
            color = if (selected) CaixaDSColorTokens.Blue700 else CaixaDSColorTokens.LegacyBetAction,
            textAlign = TextAlign.Center,
            style = TextStyle(lineHeight = 14.sp, platformStyle = PlatformTextStyle(includeFontPadding = true)),
        )
        if (team == null) Text("placeholder", Modifier.fillMaxWidth().clearAndSetSemantics {},
            fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.sp, color = Color.Transparent,
            style = TextStyle(lineHeight = 14.sp, platformStyle = PlatformTextStyle(includeFontPadding = true)))
        if (team != null && !selected && !team.selectionIndicator) {
            Text(
                if (team.countryCode.isNotBlank() && (!team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true))) "/${team.countryCode}" else team.state.takeIf(String::isNotBlank)?.let { "/$it" }.orEmpty(),
                Modifier.fillMaxWidth(),
                fontFamily = CaixaDSLegacyFuturaFontFamily,
                fontSize = if (index == 0) 12.sp else 14.sp,
                color = CaixaDSColorTokens.LegacyBetAction,
                textAlign = TextAlign.Center,
                style = TextStyle(lineHeight = 14.sp, platformStyle = PlatformTextStyle(includeFontPadding = true)),
            )
        }
    }
}
