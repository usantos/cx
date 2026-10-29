package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSSemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
internal fun OptionDialogLargeFontActions(
    cancelLabel: String,
    confirmLabel: String,
    enabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
        TextButton(
            onClick = onDismiss,
            modifier = (Modifier.fillMaxWidth().heightIn(min = 48.dp).legacyAutomationId("betting-option-cancel")).uiId("ds.0099")
        ) {
            Text(
                cancelLabel,
                Modifier.fillMaxWidth(),
                color = CaixaDSColorTokens.SocialGraphite,
                fontSize = 16.sp,
                fontFamily = CaixaDSRegularFontFamily,
                textAlign = TextAlign.Center
            )
        }
        TextButton(
            onClick = onConfirm,
            enabled = enabled,
            modifier = (Modifier.fillMaxWidth().heightIn(min = 48.dp).legacyAutomationId("betting-option-confirm")).uiId("ds.0100")
        ) {
            Text(
                confirmLabel,
                Modifier.fillMaxWidth(),
                color = if (enabled) CaixaDSColorTokens.ComboDark else CaixaDSColorTokens.AccountHelper.copy(alpha = .6f),
                fontSize = 16.sp,
                fontFamily = CaixaDSSemiboldFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}
