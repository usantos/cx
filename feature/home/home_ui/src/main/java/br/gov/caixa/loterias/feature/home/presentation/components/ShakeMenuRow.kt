package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntRect
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState

@Composable
internal fun ShakeMenuRow(
    settings: HomePreferencesUiState,
    onToggle: (Boolean?) -> Unit,
    onConsumeTooltip: () -> Unit,
    active: Boolean,
    accent: Color = LoteriasCaixaDS.colors.brand,
    labelColor: Color = accent,
    iconColor: Color = accent,
    onAccent: Color = LoteriasCaixaDSColorTokens.White,
    trackBackground: Color = LoteriasCaixaDSColorTokens.SocialCanvas,
    tooltipBackground: Color = accent,
    invertSwitchColors: Boolean = false,
) {
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
        ShakeToggleControl(
            enabled, labelColor, iconColor, accent, onAccent, trackBackground, invertSwitchColors,
            onToggle = ::toggle,
            info = {
                ShakeInfo(iconColor, tooltip, tooltipBackground, rowBounds, {
                    automaticTooltip = false
                    tooltip = !tooltip
                }, { tooltip = false })
            }
        )
    }
    if (confirm) {
        ShakeConfirmation(enabled, hideWarning, { hideWarning = it }, {
            onToggle(hideWarning)
            confirm = false
        }, { confirm = false })
    }
}
