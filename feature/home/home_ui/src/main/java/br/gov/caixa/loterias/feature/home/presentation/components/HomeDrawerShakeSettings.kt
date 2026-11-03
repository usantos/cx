package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerDefaults
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun DrawerShakeSettings(
    preferenceState: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState?,
    drawerOpen: Boolean,
    drawerStyle: br.gov.caixa.loterias.feature.home.presentation.style.HomeDrawerStyle,
    onToggleShake: (Boolean?) -> Unit,
    onConsumeShakeTooltip: () -> Unit,
) {
    preferenceState?.let {
        Box(Modifier.fillMaxWidth()) {
            ShakeMenuRow(
                it, onToggleShake, onConsumeShakeTooltip, drawerOpen, drawerStyle.shakeAccent, labelColor = drawerStyle.rowLabel,
                iconColor = drawerStyle.accent,
                onAccent = drawerStyle.shakeOnAccent,
                trackBackground = if (drawerStyle.invertShakeColors) {
                    LoteriasCaixaDSColorTokens.White
                } else {
                    LoteriasCaixaDSDrawerDefaults.expandedBackground
                },
                tooltipBackground = drawerStyle.shakeTooltipBackground,
                invertSwitchColors = drawerStyle.invertShakeColors
            )
        }
    }
}
