package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun RapidPreset(label: String, selected: Boolean, enabled: Boolean, circular: Boolean, onClick: () -> Unit) {
    val outline = if (circular) LoteriasCaixaDSColorTokens.RapidWheel else LoteriasCaixaDSColorTokens.MegaSenaDark
    val shape = if (circular) CircleShape else RoundedCornerShape(if (selected) 10.dp else 4.dp)
    Box(
        Modifier.padding(if (circular) 10.dp else 5.dp)
            .then(if (circular) Modifier.size(60.dp) else Modifier.heightIn(min = 48.dp))
            .background(if (selected) LoteriasCaixaDSColorTokens.RapidWheel else LoteriasCaixaDSColorTokens.White, shape)
            .border(if (selected) 0.dp else 1.dp, outline, shape)
            .then(
                if (enabled) {
                    Modifier.uiId("bet.0140").selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
                } else {
                    Modifier.semantics(mergeDescendants = true) {
                        this.selected = selected
                        role = Role.RadioButton
                        disabled()
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            (Modifier.padding(horizontal = if (circular) 0.dp else 10.dp)).uiId("bet.0141"),
            color = if (selected) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSColorTokens.RapidWheel,
            fontFamily = if (circular) LoteriasCaixaDSFontFamily else LoteriasCaixaDSRegularFontFamily,
            fontWeight = if (circular) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (circular) 14.sp else 17.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RapidNumberCells(numbers: List<Int>, selected: Set<Int>, danger: Boolean, onToggle: ((Int) -> Unit)?, modifier: Modifier = Modifier) {
    val color = if (danger) LoteriasCaixaDSColorTokens.RapidExcluded else LoteriasCaixaDS.colors.teal
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = (maxWidth.value / RAPID_MINIMUM_NUMBER_CELL_WIDTH).toInt().coerceAtLeast(1)
        val cellWidth = maxWidth / columns
        FlowRow(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(17.dp)) {
            numbers.forEach { number ->
                Box(Modifier.width(cellWidth), contentAlignment = Alignment.Center) {
                    val filled = number in selected
                    Box(
                        Modifier.size(40.dp).background(if (filled) color else LoteriasCaixaDS.colors.surface, CircleShape)
                            .border(1.dp, color, CircleShape)
                            .then(
                                if (onToggle !=
                                    null
                                ) {
                                    Modifier.uiId("bet.0142").toggleable(value = filled, role = Role.Checkbox) { onToggle(number) }
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            number.toString().padStart(2, '0'),
                            color = if (filled) LoteriasCaixaDSColorTokens.White else color,
                            fontSize = 14.1.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LoteriasCaixaDSFontFamily,
                            modifier = Modifier.uiId("bet.0143")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RapidSectionHeader(
    title: String,
    description: String,
    titleTop: androidx.compose.ui.unit.Dp = 16.dp,
    descriptionTop: androidx.compose.ui.unit.Dp = 16.dp,
    descriptionPadding: androidx.compose.ui.unit.Dp = 0.dp,
    descriptionWidth: androidx.compose.ui.unit.Dp? = null,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            (Modifier.padding(top = titleTop, start = 8.dp, end = 8.dp)).uiId("bet.0144"),
            color = LoteriasCaixaDSColorTokens.ExtendedToolbarLine,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LoteriasCaixaDSFontFamily,
            textAlign = TextAlign.Center
        )
        Text(
            description,
            (
                Modifier.padding(top = descriptionTop)
                    .then(if (descriptionWidth != null) Modifier.width(descriptionWidth) else Modifier.fillMaxWidth())
                    .padding(descriptionPadding)
                ).uiId("bet.0145"),
            color = LoteriasCaixaDS.colors.graphite,
            fontSize = 14.sp,
            fontFamily = LoteriasCaixaDSFontFamily,
            textAlign = TextAlign.Center
        )
    }
}

private const val RAPID_MINIMUM_NUMBER_CELL_WIDTH = 56
