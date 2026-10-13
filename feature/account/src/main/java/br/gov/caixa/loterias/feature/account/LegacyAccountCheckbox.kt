package br.gov.caixa.loterias.feature.account

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCheckbox

/** CaixaDS checkbox com a faixa vertical de toque da caixa de seleção do app instalado. */
@Composable
internal fun LegacyAccountCheckbox(checked: Boolean, onToggle: () -> Unit, width: Dp) {
    CaixaDSLegacyCheckbox(
        checked = checked,
        onClick = onToggle,
        modifier = Modifier.width(width).height(48.dp),
        color = if (checked) CaixaDSColorTokens.LegacyAccountCheckboxChecked else CaixaDSColorTokens.LegacyAccountTermsText,
    )
}
