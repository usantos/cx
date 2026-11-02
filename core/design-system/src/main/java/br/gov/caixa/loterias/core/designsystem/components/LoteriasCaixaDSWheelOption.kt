package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId

/** Um ciclo lógico participa da acessibilidade; os demais preservam a repetição visual por ponteiro. */
@Composable
internal fun LoteriasCaixaDSWheelOption(
    label: String,
    selected: Boolean,
    centralCycle: Boolean,
    itemHeight: Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    color: androidx.compose.ui.graphics.Color,
    fontFamily: FontFamily,
    onClick: () -> Unit,
) {
    val presentation = if (centralCycle) {
        Modifier
    } else {
        Modifier
            .clearAndSetSemantics { }
            .focusProperties { canFocus = false }
    }
    Box(
        presentation.fillMaxWidth().height(itemHeight)
            .uiId("ds.0058").selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = color,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
