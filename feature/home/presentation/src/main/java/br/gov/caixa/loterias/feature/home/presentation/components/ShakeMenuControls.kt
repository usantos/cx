package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAnchoredTooltip
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerDefaults
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR

@Composable
internal fun ShakeToggleControl(
    enabled: Boolean,
    labelColor: Color,
    iconColor: Color,
    accent: Color,
    onAccent: Color,
    trackBackground: Color,
    invertSwitchColors: Boolean,
    onToggle: () -> Unit,
    info: @Composable () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().heightIn(min = 55.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
            Image(
                painterResource(R.drawable.ic_celular),
                null,
                Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_aposta_shake_317269),
            (
                Modifier.padding(start = 12.dp)
                    .uiId("home.0104").clickable { onToggle() }
                ).uiId("home.0105"),
            style = LoteriasCaixaDSDrawerDefaults.itemTextStyle.copy(fontSize = 14.sp),
            color = labelColor,
            maxLines = 1,
            softWrap = false
        )
        info()
        Spacer(Modifier.weight(1f))
        LoteriasCaixaDSCheckSwitch(
            enabled = enabled,
            accent = accent,
            onAccent = onAccent,
            trackBackground = trackBackground,
            invertColors = invertSwitchColors,
            onClick = onToggle
        )
    }
}

@Composable
internal fun ShakeConfirmation(
    enabled: Boolean,
    hideWarning: Boolean,
    onWarningChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSDialog(
        title = if (enabled) {
            uiStrings.getString(
                LocalizedR.string.ui_desativar_aposta_shake_a8e7d9
            )
        } else {
            uiStrings.getString(LocalizedR.string.ui_ativar_aposta_shake_4cd4b9)
        },
        message = if (enabled) {
            uiStrings.getString(
                LocalizedR.string.ui_deseja_desativar_a_aposta_shake_nas_b0bc75
            )
        } else {
            uiStrings.getString(
                LocalizedR.string.ui_deseja_ativar_a_aposta_shake_nas_mod_b76591
            )
        },
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
        onPositive = {
            onConfirm()
        },
        onNegative = onDismiss,
        extraContent = {
            Row(
                Modifier.fillMaxWidth()
                    .uiId("home.0107").clickable { onWarningChange(!hideWarning) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(hideWarning, onCheckedChange = onWarningChange, modifier = Modifier.uiId("home.0108"))
                Text(
                    uiStrings.getString(LocalizedR.string.ui_nao_exibir_o_aviso_novamente_56667d),
                    style = LoteriasCaixaDS.typography.bodySmall,
                    modifier = Modifier.uiId("home.0109")
                )
            }
        }
    )
}

@Composable
internal fun ShakeInfo(
    iconColor: Color,
    tooltip: Boolean,
    tooltipBackground: Color,
    rowBounds: IntRect?,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.size(width = 40.dp, height = 48.dp).uiId("home.0106").clickable {
            onClick()
        }
            .semantics { contentDescription = uiStrings.getString(LocalizedR.string.ui_informacoes_sobre_aposta_shake_c2fa3c) },
        contentAlignment = Alignment.Center
    ) {
        Image(painterResource(R.drawable.ic_info), null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(iconColor))
        if (tooltip) {
            LoteriasCaixaDSAnchoredTooltip(
                text = uiStrings.getString(LocalizedR.string.ui_agite_o_celular_para_preencher_sua_a_1bfa20),
                onDismiss = onDismiss,
                color = tooltipBackground,
                rowBounds = rowBounds
            )
        }
    }
}
