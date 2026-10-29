@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.core.designsystem.components.base

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId

/** Contrato básico de RadioButton; a renderização e os padrões pertencem exclusivamente ao CaixaDS. */
@Composable
fun CaixaDSRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    androidx.compose.material3.RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = (modifier).uiId("ds.0067"),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}
