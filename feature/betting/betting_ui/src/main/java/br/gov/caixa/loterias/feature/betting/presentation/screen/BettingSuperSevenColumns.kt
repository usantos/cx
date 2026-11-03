package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun SuperSevenColumnHeadersDelivered(metrics: TextStyle) {
    NativeSuperSevenRowDelivered {
        repeat(COLUMN_COUNT) { column ->
            Box(
                Modifier.padding(
                    1.dp
                ).height(
                    ColumnHeadingHeight
                ).legacyAutomationId(
                    "super-sete-header-$column"
                ).background(LoteriasCaixaDSColorTokens.SuperSevenHeader, RoundedCornerShape(ContentInset)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    (column + 1).toString(),
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = ControlTextFontSize,
                    color = LoteriasCaixaDSColorTokens.White,
                    style = metrics,
                    modifier = Modifier.uiId("bet.0272")
                )
            }
        }
    }
}

@Composable
fun SuperSevenNumberColumnsDelivered(
    state: BettingGameUiState,
    actions: BettingGameActions,
    nativeBallSize: androidx.compose.ui.unit.Dp,
    metrics: TextStyle,
) {
    NativeSuperSevenRowDelivered((Modifier.padding(bottom = ContentInset)).uiId("bet.0274")) {
        repeat(COLUMN_COUNT) { column ->
            Column(
                Modifier.padding(1.dp).background(LoteriasCaixaDSColorTokens.SuperSevenColumn),
                verticalArrangement = Arrangement.spacedBy(CounterText),
                horizontalAlignment = LegacyGridHorizontalAlignment
            ) {
                (0..LAST_COLUMN_DIGIT).forEach { digit ->
                    SuperSevenDigitCellDelivered(
                        digit,
                        column,
                        digit in state.superSevenColumns[column],
                        nativeBallSize,
                        metrics,
                        onClick = { actions.toggleSuperSevenDigit(column, digit) }
                    )
                }
            }
        }
    }
}

@Composable
fun SuperSevenDigitCellDelivered(
    digit: Int,
    column: Int,
    checked: Boolean,
    nativeBallSize: androidx.compose.ui.unit.Dp,
    metrics: TextStyle,
    onClick: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    Box(
        Modifier.size(nativeBallSize)
            .legacyAutomationId("super-sete-$column-$digit")
            .semantics {
                contentDescription = uiStrings.getString(
                    DeliveryR.string.ui_coluna_1_s_numero_2_s_4e3a5f,
                    (column + 1).toString(),
                    digit.toString()
                )
                this.selected = checked
            }
            .uiId("bet.0275").toggleable(value = checked, role = Role.Checkbox) {
                onClick()
            }
            .background(
                if (checked) LoteriasCaixaDSColorTokens.SuperSevenSelection else LoteriasCaixaDSColorTokens.White,
                CircleShape
            )
            .then(
                if (checked) {
                    Modifier
                } else {
                    Modifier.border(
                        with(density) {
                            DIGIT_BORDER_WIDTH_PIXELS.toDp()
                        },
                        LoteriasCaixaDSColorTokens.SuperSevenNumber,
                        CircleShape
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            digit.toString(),
            color = if (checked) {
                LoteriasCaixaDSColorTokens.SuperSevenSelectedNumber
            } else {
                LoteriasCaixaDSColorTokens.SuperSevenNumber
            },
            fontFamily = LoteriasCaixaDSFuturaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = ControlTextFontSize,
            textAlign = TextAlign.Center,
            style = metrics,
            modifier = Modifier.uiId("bet.0276")
        )
    }
}

private val ControlTextFontSize = 14.sp
private val CounterText = 15.dp
private val ContentInset = 16.dp
private val ColumnHeadingHeight = 35.dp

private const val COLUMN_COUNT = 7

private const val DIGIT_BORDER_WIDTH_PIXELS = 3

private const val LAST_COLUMN_DIGIT = 9
