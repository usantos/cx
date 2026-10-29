package br.gov.caixa.loterias.core.designsystem.components.input

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** CaixaDS checkbox com a faixa vertical de toque da caixa de seleção do app instalado. */
@Composable
fun CaixaDSAccountCheckbox(checked: Boolean, onToggle: (() -> Unit)?, width: Dp) {
    CaixaDSCheckbox(
        checked = checked,
        onClick = onToggle,
        modifier = Modifier.width(width).height(48.dp).uiId("ds.0101"),
        color = if (checked) CaixaDSColorTokens.AccountCheckboxChecked else CaixaDSColorTokens.AccountTermsText
    )
}
