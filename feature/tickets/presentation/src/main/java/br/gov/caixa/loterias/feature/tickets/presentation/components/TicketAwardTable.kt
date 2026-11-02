package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR

@Composable
internal fun TicketAwardTable(rows: List<TicketResultAwardRow>, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp)) {
        AwardTableRow(
            listOf(
                stringResource(LocalizedR.string.ui_concurso_1997f5),
                uiStrings.getString(LocalizedR.string.ui_situacao_cf7d46),
                stringResource(LocalizedR.string.format_faixa),
                stringResource(LocalizedR.string.format_qtde),
                stringResource(LocalizedR.string.format_sorteio),
                stringResource(LocalizedR.string.format_valor)
            ),
            color,
            true
        )
        rows.forEach { row -> AwardTableRow(listOf(row.contest, row.status, row.award, row.quantity, row.draw, row.value), color, false) }
    }
}

@Composable
private fun AwardTableRow(values: List<String>, color: Color, header: Boolean) {
    val weights =
        listOf(
            CONTEST_COLUMN_WEIGHT,
            STATUS_COLUMN_WEIGHT,
            AWARD_COLUMN_WEIGHT,
            QUANTITY_COLUMN_WEIGHT,
            DRAW_COLUMN_WEIGHT,
            VALUE_COLUMN_WEIGHT
        )
    val headers = listOf("Concurso", "Situação", "Faixa", "Qtde", "Sorteio", "Valor")
    Row(
        Modifier.fillMaxWidth().then(
            if (header) {
                Modifier
            } else {
                Modifier.semantics(mergeDescendants = true) {
                    contentDescription = headers.zip(values).joinToString("; ") { (label, value) -> "$label: $value" }
                }
            }
        )
    ) {
        values.forEachIndexed { index, value ->
            Text(
                value,
                (Modifier.weight(weights[index])).uiId("tickets.0104"),
                color = color,
                style = resultText(12.sp, if (header) FontWeight.Bold else FontWeight.Normal),
                textAlign = TextAlign.Center
            )
        }
    }
}

private const val CONTEST_COLUMN_WEIGHT = 2.3f
private const val STATUS_COLUMN_WEIGHT = 2.5f
private const val AWARD_COLUMN_WEIGHT = 1.4f
private const val QUANTITY_COLUMN_WEIGHT = 1.5f
private const val DRAW_COLUMN_WEIGHT = 1.8f
private const val VALUE_COLUMN_WEIGHT = 2.5f
