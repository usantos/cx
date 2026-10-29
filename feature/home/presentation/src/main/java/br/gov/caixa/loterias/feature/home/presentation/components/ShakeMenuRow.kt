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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnchoredTooltip
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerDefaults
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState

@Composable
internal fun ShakeMenuRow(
    settings: HomePreferencesUiState,
    onToggle: (Boolean?) -> Unit,
    onConsumeTooltip: () -> Unit,
    active: Boolean,
    accent: Color = CaixaDS.colors.brand,
    labelColor: Color = accent,
    iconColor: Color = accent,
    onAccent: Color = CaixaDSColorTokens.White,
    trackBackground: Color = CaixaDSColorTokens.SocialCanvas,
    tooltipBackground: Color = accent,
    invertSwitchColors: Boolean = false,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val enabled = settings.shakeEnabled
    var confirm by rememberSaveable { mutableStateOf(false) }
    var hideWarning by rememberSaveable { mutableStateOf(false) }
    var tooltip by rememberSaveable { mutableStateOf(false) }
    var automaticTooltip by rememberSaveable { mutableStateOf(false) }
    var rowBounds by remember { mutableStateOf<IntRect?>(null) }
    val bringIntoView = remember { BringIntoViewRequester() }
    LaunchedEffect(active, settings.shakeTooltipPending) {
        if (active && settings.shakeTooltipPending) {
            bringIntoView.bringIntoView()
            automaticTooltip = true
            tooltip = true
            onConsumeTooltip()
        } else if (!active) {
            tooltip = false
        }
    }
    LaunchedEffect(tooltip, automaticTooltip) {
        if (tooltip && automaticTooltip) {
            kotlinx.coroutines.delay(5000)
            tooltip = false
        }
    }
    fun toggle() {
        tooltip = false
        automaticTooltip = false
        if (settings.shakeWarningHidden) {
            onToggle(null)
        } else {
            hideWarning = false
            confirm = true
        }
    }
    Box(
        Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoView).onGloballyPositioned {
            val bounds = it.boundsInWindow()
            rowBounds = IntRect(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        }
    ) {
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
                        .uiId("home.0104").clickable { toggle() }
                    ).uiId("home.0105"),
                style = CaixaDSDrawerDefaults.itemTextStyle.copy(fontSize = 14.sp),
                color = labelColor,
                maxLines = 1,
                softWrap = false
            )
            ShakeInfo(iconColor, tooltip, tooltipBackground, rowBounds, {
                automaticTooltip = false
                tooltip = !tooltip
            }, { tooltip = false })
            Spacer(Modifier.weight(1f))
            CaixaDSCheckSwitch(
                enabled = enabled,
                accent = accent,
                onAccent = onAccent,
                trackBackground = trackBackground,
                invertColors = invertSwitchColors,
                onClick = ::toggle
            )
        }
    }
    if (confirm) {
        ShakeConfirmation(enabled, hideWarning, { hideWarning = it }, {
            onToggle(hideWarning)
            confirm = false
        }, { confirm = false })
    }
}

@Composable
private fun ShakeConfirmation(
    enabled: Boolean,
    hideWarning: Boolean,
    onWarningChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSDialog(
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
                    style = CaixaDS.typography.bodySmall,
                    modifier = Modifier.uiId("home.0109")
                )
            }
        }
    )
}

@Composable
private fun ShakeInfo(
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
            CaixaDSAnchoredTooltip(
                text = uiStrings.getString(LocalizedR.string.ui_agite_o_celular_para_preencher_sua_a_1bfa20),
                onDismiss = onDismiss,
                color = tooltipBackground,
                rowBounds = rowBounds
            )
        }
    }
}
