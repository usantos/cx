package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotecaSelection
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartText
import java.util.Locale

/** Seleções do carrinho somente para leitura; o componente de resultado da aposta é interno à sua funcionalidade. */
@Composable
internal fun CartLotecaContent(matches: List<CartLotecaSelection>, groupedReadout: Boolean = false) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var showLegend by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_legenda_61154f),
            (
                Modifier.align(Alignment.End)
                    .uiId("cart.0039").clickable { showLegend = true }.padding(vertical = 8.dp)
                    .semantics { contentDescription = uiStrings.getString(LocalizedR.string.ui_legenda_da_loteca_8b6284) }
                ).uiId("cart.0040"),
            color = CaixaDSColorTokens.Blue700,
            style = cartText(LOTECA_TEXT_SIZE, FontWeight.Bold)
        )
        Column(if (groupedReadout) Modifier.clearAndSetSemantics {} else Modifier) {
            matches.forEach { match ->
                val title = "Jogo %02d".format(Locale.ROOT, match.number)
                val description = when {
                    match.description.contains("FEM.") || match.description.contains("FEMININO") -> " (feminino)"
                    match.description.contains("SUB17") -> " (sub 17)"
                    match.description.contains("SUB20") -> " (sub 20)"
                    else -> ""
                }
                Text(
                    title + description,
                    (Modifier.padding(top = 8.dp, bottom = 6.dp)).uiId("cart.0041"),
                    color = CaixaDSColorTokens.SecondaryText,
                    style = cartText()
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CartLotecaOutcome(
                        match.homeLabel,
                        match.homeSelected,
                        "$title, ${match.homeLabel}",
                        (Modifier.weight(1f)).uiId("cart.0042"),
                        compact = true
                    )
                    CartLotecaOutcome(
                        "Empate",
                        match.drawSelected,
                        "$title, Empate",
                        (Modifier.width(LOTECA_DRAW_COLUMN_WIDTH.dp)).uiId("cart.0043"),
                        compact = true
                    )
                    CartLotecaOutcome(
                        match.awayLabel,
                        match.awaySelected,
                        "$title, ${match.awayLabel}",
                        (Modifier.weight(1f)).uiId("cart.0044"),
                        compact = true
                    )
                }
            }
        }
    }
    if (showLegend) {
        CartLotecaLegend(onDismiss = { showLegend = false })
    }
}

@Composable
private fun CartLotecaOutcome(
    label: String,
    chosen: Boolean,
    description: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier.heightIn(
            min = if (compact) LOTECA_TEAM_CELL_HEIGHT.dp else 0.dp
        ).padding(vertical = 4.dp).semantics(mergeDescendants = true) {
            contentDescription = description
            selected = chosen
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(Modifier.size(16.dp).border(1.dp, CaixaDSColorTokens.SecondaryText, CircleShape), contentAlignment = Alignment.Center) {
            if (chosen) Box(Modifier.size(10.dp).background(CaixaDSColorTokens.Blue700, CircleShape))
        }
        Text(
            label,
            modifier = (if (compact) Modifier.weight(1f) else Modifier).uiId("cart.0047"),
            color = CaixaDSColorTokens.SecondaryText,
            maxLines = if (compact) 1 else Int.MAX_VALUE,
            softWrap = !compact,
            overflow = if (compact) TextOverflow.Ellipsis else TextOverflow.Clip,
            autoSize = if (compact) {
                TextAutoSize.StepBased(
                    minFontSize = LOTECA_TEAM_MIN_TEXT_SIZE.sp,
                    maxFontSize = LOTECA_TEAM_TEXT_SIZE.sp
                )
            } else {
                null
            },
            style = cartText(if (compact) LOTECA_TEAM_TEXT_SIZE else LOTECA_TEXT_SIZE, if (chosen) FontWeight.Bold else FontWeight.Normal)
        )
    }
}

private const val LOTECA_TEXT_SIZE = 14

private const val LOTECA_TEAM_TEXT_SIZE = 12
private const val LOTECA_TEAM_MIN_TEXT_SIZE = 10
private const val LOTECA_TEAM_CELL_HEIGHT = 28
private const val LOTECA_DRAW_COLUMN_WIDTH = 68

@Composable
private fun CartLotecaLegend(onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_legenda_61154f),
        message = "",
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
        onPositive = onDismiss,
        onDismiss = onDismiss,
        extraContent = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CartLotecaOutcome(
                    uiStrings.getString(LocalizedR.string.ui_palpite_escolhido_fea20f),
                    true,
                    uiStrings.getString(LocalizedR.string.ui_palpite_escolhido_fea20f),
                    modifier = Modifier.uiId("cart.0045")
                )
                CartLotecaOutcome(
                    uiStrings.getString(LocalizedR.string.ui_palpite_nao_escolhido_9dfb91),
                    false,
                    uiStrings.getString(LocalizedR.string.ui_palpite_nao_escolhido_9dfb91),
                    modifier = Modifier.uiId("cart.0046")
                )
            }
        }
    )
}
