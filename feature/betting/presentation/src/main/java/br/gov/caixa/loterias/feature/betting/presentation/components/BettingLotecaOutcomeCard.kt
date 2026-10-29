package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("CyclomaticComplexMethod")
@Composable
internal fun LotecaOutcomeCard(
    index: Int,
    team: SportsTeam?,
    selected: Boolean,
    descriptionBindingKey: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    // bindTextos nativo decide a visibilidade uma vez. Atualizações visuais já montadas apenas alteram sua cor.
    // Limpar ou reassociar invalida explicitamente este retrato; selecionar ou desmarcar não o invalida.
    val descriptionVisible = remember(team, descriptionBindingKey) { team != null && !selected && !team.selectionIndicator }
    val shape = when (index) {
        0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
        2 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
        else -> RoundedCornerShape(0.dp)
    }
    val label = team?.name ?: "Empate"
    Column(
        modifier
            .clip(shape)
            .background(if (selected) CaixaDSColorTokens.LotecaSelectedBackground else CaixaDSColorTokens.White)
            .border(2.dp, if (selected) CaixaDSColorTokens.LotecaCounter else CaixaDSColorTokens.LotecaChoiceBorder, shape)
            .uiId("bet.0036").clickable(onClick = onClick)
            .padding(8.dp)
            .semantics {
                contentDescription = label
                this.selected = selected
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (team == null) {
            Image(
                painterResource(R.drawable.installed_betting_ic_empate),
                null,
                Modifier.padding(top = 16.dp, bottom = 10.dp).size(18.dp),
                colorFilter = ColorFilter.tint(if (selected) CaixaDSColorTokens.Blue700 else CaixaDSColorTokens.BetAction)
            )
        } else {
            TeamShield(team, Modifier.padding(top = 12.dp).size(35.dp), bottomPadding = 0.dp)
        }
        Text(
            label,
            (Modifier.fillMaxWidth()).uiId("bet.0037"),
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = if (selected) CaixaDSColorTokens.Blue700 else CaixaDSColorTokens.BetAction,
            textAlign = TextAlign.Center,
            style = TextStyle(lineHeight = 14.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        if (team == null) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_placeholder_409788),
                (Modifier.fillMaxWidth().clearAndSetSemantics {}).uiId("bet.0038"),
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = Color.Transparent,
                style = TextStyle(lineHeight = 14.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
        }
        if (team != null && descriptionVisible) {
            Text(
                if (team.countryCode.isNotBlank() &&
                    (!team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true))
                ) {
                    uiStrings.getString(LocalizedR.string.ui_1_s_db4c40, team.countryCode)
                } else {
                    team.state.takeIf(String::isNotBlank)?.let { "/$it" }.orEmpty()
                },
                (Modifier.fillMaxWidth()).uiId("bet.0039"),
                fontFamily = FontFamily.SansSerif,
                fontSize = if (index == 0) 12.sp else 14.sp,
                color = if (selected) CaixaDSColorTokens.Blue700 else CaixaDSColorTokens.BetAction,
                textAlign = TextAlign.Center,
                style = TextStyle(lineHeight = 14.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
        }
    }
}
