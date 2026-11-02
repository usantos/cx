@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSBrandTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerHeader
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerInstitution
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerItem
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerSheet
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSHomeTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSModalityTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBarColor
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

internal val navigationPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSTopBar") { state ->
        with(state) {
            LoteriasCaixaDSTopBar(
                title = sample,
                onBack = action,
                subtitle = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSBrandTopBar") { state ->
        with(state) {
            LoteriasCaixaDSBrandTopBar(title = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerSheet") { state ->
        with(state) {
            LoteriasCaixaDSDrawerSheet(content = {
                LoteriasCaixaDSDrawerItem(
                    sample,
                    action
                )
            })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerHeader") { state ->
        with(state) {
            LoteriasCaixaDSDrawerHeader(
                greeting = sample,
                onClose = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerItem") { state ->
        with(state) {
            LoteriasCaixaDSDrawerItem(
                label = sample,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerInstitution") { state ->
        with(state) {
            LoteriasCaixaDSDrawerInstitution(
                expanded = checked,
                onClick = toggle,
                details = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSHomeTopBar") { state ->
        with(state) {
            LoteriasCaixaDSHomeTopBar(
                unreadNotificationCount = count,
                octoberRose = false,
                quickAccessExpanded = checked,
                cloverRotation = 0f,
                onMenu = action,
                onNotifications = action,
                onCampaign = action,
                onQuickAccess = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSModalityTopBar") { state ->
        with(state) {
            LoteriasCaixaDSModalityTopBar(
                title = sample,
                backgroundColor = LoteriasCaixaDS.colors.brand,
                contentColor = LoteriasCaixaDSColorTokens.White,
                contestNumber = "1234",
                drawLabel = sample,
                drawDate = "22/10/2026",
                onBack = action,
                onHelp = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBarColor") { state ->
        with(state) {
            LoteriasCaixaDSStatusBarColor(
                color = LoteriasCaixaDS.colors.brand,
                darkIcons = false
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBar") { state ->
        with(state) {
            LoteriasCaixaDSStatusBar(content = { LoteriasCaixaDSText(sample) })
        }
    }
)
