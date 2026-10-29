package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
internal fun RapidPreset(label: String, selected: Boolean, enabled: Boolean, circular: Boolean, onClick: () -> Unit) {
    val outline = if (circular) CaixaDSColorTokens.RapidWheel else CaixaDSColorTokens.MegaSenaDark
    val shape = if (circular) CircleShape else RoundedCornerShape(if (selected) 10.dp else 4.dp)
    Box(
        Modifier.padding(if (circular) 10.dp else 5.dp)
            .then(if (circular) Modifier.size(60.dp) else Modifier.heightIn(min = 48.dp))
            .background(if (selected) CaixaDSColorTokens.RapidWheel else CaixaDSColorTokens.White, shape)
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
            color = if (selected) CaixaDSColorTokens.White else CaixaDSColorTokens.RapidWheel,
            fontFamily = if (circular) CaixaDSFontFamily else br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily,
            fontWeight = if (circular) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (circular) 14.sp else 17.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun RapidNumberCells(
    numbers: List<Int>,
    selected: Set<Int>,
    danger: Boolean,
    onToggle: ((Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val color = if (danger) CaixaDSColorTokens.RapidExcluded else CaixaDS.colors.teal
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = (maxWidth.value / 56).toInt().coerceAtLeast(1)
        val cellWidth = maxWidth / columns
        FlowRow(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(17.dp)) {
            numbers.forEach { number ->
                Box(Modifier.width(cellWidth), contentAlignment = Alignment.Center) {
                    val filled = number in selected
                    Box(
                        Modifier.size(40.dp).background(if (filled) color else CaixaDS.colors.surface, CircleShape)
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
                            color = if (filled) CaixaDSColorTokens.White else color,
                            fontSize = 14.1.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CaixaDSFontFamily,
                            modifier = Modifier.uiId("bet.0143")
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun RapidSectionHeader(
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
            color = CaixaDSColorTokens.ExtendedToolbarLine,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = CaixaDSFontFamily,
            textAlign = TextAlign.Center
        )
        Text(
            description,
            (
                Modifier.padding(top = descriptionTop)
                    .then(if (descriptionWidth != null) Modifier.width(descriptionWidth) else Modifier.fillMaxWidth())
                    .padding(descriptionPadding)
                ).uiId("bet.0145"),
            color = CaixaDS.colors.graphite,
            fontSize = 14.sp,
            fontFamily = CaixaDSFontFamily,
            textAlign = TextAlign.Center
        )
    }
}
