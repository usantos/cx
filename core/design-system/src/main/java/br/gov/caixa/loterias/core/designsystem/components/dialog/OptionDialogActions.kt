package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSSemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun OptionDialogActions(
    nativeMetrics: Triple<Int, Pair<Int, Int>, Pair<Int, Int>>,
    enabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val density = LocalDensity.current
    val cancelLabel = stringResource(LocalizedR.string.ui_cancelar_bb9dbb)
    val confirmLabel = stringResource(LocalizedR.string.ui_confirmar_717bed)
    if (density.fontScale > 1f) {
        OptionDialogLargeFontActions(cancelLabel, confirmLabel, enabled, onDismiss, onConfirm)
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            // Mantém as dimensões externas nativas sem o recuo extra de texto do Material.
            contentPadding = PaddingValues(0.dp),
            onClick = onDismiss,
            modifier = (
                Modifier.width(with(density) { nativeMetrics.second.first.toDp() })
                    .height(with(density) { nativeMetrics.second.second.toDp() }).legacyAutomationId("betting-option-cancel")
                ).uiId("ds.0080")
        ) {
            Text(
                cancelLabel,
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
        TextButton(
            // Mantém as dimensões externas nativas sem o recuo extra de texto do Material.
            contentPadding = PaddingValues(0.dp),
            onClick = onConfirm,
            enabled = enabled,
            modifier = (
                Modifier.width(with(density) { nativeMetrics.third.first.toDp() })
                    .height(with(density) { nativeMetrics.third.second.toDp() }).legacyAutomationId("betting-option-confirm")
                ).uiId("ds.0081")
        ) {
            Text(
                confirmLabel,
                color = if (enabled) {
                    LoteriasCaixaDSColorTokens.ComboDark
                } else {
                    LoteriasCaixaDSColorTokens.AccountHelper.copy(alpha = .6f)
                },
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSSemiboldFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
    }
}
