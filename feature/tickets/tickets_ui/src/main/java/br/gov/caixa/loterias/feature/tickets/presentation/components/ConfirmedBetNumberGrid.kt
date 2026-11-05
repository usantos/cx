package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.superSevenDrawColumns
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun NativeNumberGrid(numbers: List<Int>, color: Color, background: Color, matched: Set<Int> = emptySet(), plain: Boolean = false) {
    Column(Modifier.padding(top = 3.dp)) {
        numbers.chunked(NUMBERS_PER_ROW).forEach { row ->
            Row {
                row.forEach { number ->
                    val hit = !plain && number in matched
                    Box(
                        Modifier.sizeIn(minWidth = 43.dp, minHeight = 43.dp).padding(4.dp)
                            .then(
                                if (plain || matched.isEmpty()) {
                                    Modifier
                                } else {
                                    Modifier.semantics(mergeDescendants = true) {
                                        contentDescription =
                                            "Número ${number.toString().padStart(2, '0')}, ${if (hit) "acertou" else "não acertou"}"
                                    }
                                }
                            ).then(
                                if (plain) {
                                    Modifier
                                } else {
                                    Modifier.border(1.dp, color, CircleShape)
                                        .background(if (hit) color else background, CircleShape)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            number.toString().padStart(2, '0'),
                            color = if (hit) background else color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.uiId("tickets.0015")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuperSevenSelection(bet: ConfirmedBet, contentColor: Color, background: Color, result: DrawResult?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(DeliveryR.string.ui_suas_colunas_69913f),
        (Modifier.padding(top = 38.dp)).uiId("tickets.0016"),
        color = contentColor,
        fontSize = 17.sp
    )
    SuperSevenColumns(bet.numberColumns, contentColor, background, result?.let(::superSevenDrawColumns).orEmpty())
}

@Composable
fun SuperSevenColumns(columns: List<List<Int>>, contentColor: Color, background: Color, drawn: List<List<Int>> = emptyList()) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(top = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        columns.take(SUPER_SEVEN_COLUMN_COUNT).forEachIndexed { index, column ->
            Column(
                Modifier.semantics(mergeDescendants = true) {
                    contentDescription = "Coluna ${index + 1}: " + column.joinToString(", ") { digit ->
                        "$digit" +
                            if (drawn.isEmpty()) {
                                ""
                            } else if (drawn.getOrNull(index)?.contains(digit) == true) {
                                " acertou"
                            } else {
                                " não acertou"
                            }
                    }
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_1_s_d020a2, (index + 1).toString()),
                    color = contentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0017")
                )
                column.forEach { digit ->
                    val hit = drawn.getOrNull(index)?.contains(digit) == true
                    Box(
                        Modifier.padding(top = 4.dp).sizeIn(minWidth = 31.dp, minHeight = 31.dp)
                            .border(1.dp, contentColor, RoundedCornerShape(3.dp))
                            .background(if (hit) contentColor else background, RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            digit.toString(),
                            color = if (hit) background else contentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.uiId("tickets.0018")
                        )
                    }
                }
            }
        }
    }
}

private const val NUMBERS_PER_ROW = 6
private const val SUPER_SEVEN_COLUMN_COUNT = 7
