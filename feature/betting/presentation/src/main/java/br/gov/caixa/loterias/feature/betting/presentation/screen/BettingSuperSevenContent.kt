package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.legacyPromptInset
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

// GridView centraliza por divisão inteira; o alinhamento por viés do Compose arredonda restos ímpares.
private val LegacyGridHorizontalAlignment = object : Alignment.Horizontal {
    override fun align(size: Int, space: Int, layoutDirection: androidx.compose.ui.unit.LayoutDirection): Int = (space - size) / 2
}

/**
 * LinearLayout atribui a cada peso sucessivo sua parcela dos pixels restantes. Row.weight distribui os pixels
 * restantes a partir da primeira coluna, deslocando as bolas.
 */
@Composable
private fun NativeSuperSevenRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier.fillMaxWidth(), content = content) { measurables, constraints ->
        val margins = 2 * 1.dp.roundToPx()
        var remaining = constraints.maxWidth - margins * measurables.size
        val children = measurables.mapIndexed { index, measurable ->
            val share = remaining / (measurables.size - index)
            remaining -= share
            val width = share + margins
            measurable.measure(constraints.copy(minWidth = width, maxWidth = width, minHeight = 0))
        }
        layout(constraints.maxWidth, children.maxOfOrNull { it.height } ?: 0) {
            var left = 0
            children.forEach { child ->
                child.placeRelative(left, 0)
                left += child.width
            }
        }
    }
}

@Composable
internal fun SuperSevenMatrix(state: BettingGameUiState, actions: BettingGameActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val promptInset = legacyPromptInset()
    val nativeBallSize = with(density) { (44.dp.toPx().toInt()).toDp() }
    val metrics = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    Column(Modifier.fillMaxSize()) {
        val selectedCount = state.superSevenColumns.sumOf(Set<Int>::size)
        Text(
            if (selectedCount == 0) {
                uiStrings.getString(LocalizedR.string.ui_selecione_os_numeros_44f9f7)
            } else {
                uiStrings.getString(
                    LocalizedR.string.ui_selecionados_1_s_2_s_0a48ce,
                    selectedCount.toString(),
                    state.numberQuantity.toString()
                )
            },
            (Modifier.padding(start = promptInset, top = 16.dp, bottom = 8.dp)).uiId("bet.0271"),
            fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp,
            color = CaixaDSColorTokens.SocialGraphite,
            style = metrics
        )
        NativeSuperSevenRow {
            repeat(7) { column ->
                Box(
                    Modifier.padding(
                        1.dp
                    ).height(
                        35.dp
                    ).legacyAutomationId(
                        "super-sete-header-$column"
                    ).background(CaixaDSColorTokens.SuperSevenHeader, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (column + 1).toString(),
                        fontFamily = CaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CaixaDSColorTokens.White,
                        style = metrics,
                        modifier = Modifier.uiId("bet.0272")
                    )
                }
            }
        }
        // Cabeçalhos nativos ficam acima de NestedScrollView. A margem de cltGeral rola
        // com os números; a orientação da atividade e os sete cabeçalhos permanecem fixos.
        Column(Modifier.weight(1f).fillMaxWidth().uiId("bet.0273").verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))
            NativeSuperSevenRow((Modifier.padding(bottom = 16.dp)).uiId("bet.0274")) {
                repeat(7) { column ->
                    Column(
                        Modifier.padding(1.dp).background(CaixaDSColorTokens.SuperSevenColumn),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        horizontalAlignment = LegacyGridHorizontalAlignment
                    ) {
                        (0..9).forEach { digit ->
                            SuperSevenDigitCell(
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
    }
}

@Composable
private fun SuperSevenDigitCell(
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
                    LocalizedR.string.ui_coluna_1_s_numero_2_s_4e3a5f,
                    (column + 1).toString(),
                    digit.toString()
                )
                this.selected = checked
            }
            .uiId("bet.0275").toggleable(value = checked, role = Role.Checkbox) {
                onClick()
            }
            .background(
                if (checked) CaixaDSColorTokens.SuperSevenSelection else CaixaDSColorTokens.White,
                CircleShape
            )
            .then(
                if (checked) {
                    Modifier
                } else {
                    Modifier.border(with(density) { 3.toDp() }, CaixaDSColorTokens.SuperSevenNumber, CircleShape)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            digit.toString(),
            color = if (checked) {
                CaixaDSColorTokens.SuperSevenSelectedNumber
            } else {
                CaixaDSColorTokens.SuperSevenNumber
            },
            fontFamily = CaixaDSFuturaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = metrics,
            modifier = Modifier.uiId("bet.0276")
        )
    }
}
