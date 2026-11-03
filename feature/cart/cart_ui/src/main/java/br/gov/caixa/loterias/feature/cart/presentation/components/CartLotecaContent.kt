package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotecaSelection
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartText
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR
import java.util.Locale

/** Seleções do carrinho somente para leitura; o componente de resultado da aposta é interno à sua funcionalidade. */
@Composable
fun CartLotecaContent(matches: List<CartLotecaSelection>, groupedReadout: Boolean = false) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var showLegend by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_legenda_61154f),
            (
                Modifier.align(Alignment.End)
                    .uiId("cart.0039").clickable { showLegend = true }.padding(vertical = 8.dp)
                    .semantics { contentDescription = uiStrings.getString(DeliveryR.string.ui_legenda_da_loteca_8b6284) }
                ).uiId("cart.0040"),
            color = LoteriasCaixaDSColorTokens.Blue700,
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
                    color = LoteriasCaixaDSColorTokens.SecondaryText,
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

private const val LOTECA_TEXT_SIZE = 14
private const val LOTECA_DRAW_COLUMN_WIDTH = 68
