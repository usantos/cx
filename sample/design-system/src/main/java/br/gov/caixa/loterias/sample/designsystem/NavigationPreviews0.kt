@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSBrandTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerHeader
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerInstitution
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerItem
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerSheet
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSHomeTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSModalityTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

internal val navigationPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSTopBar") { state ->
        with(state) {
            CaixaDSTopBar(
                title = sample,
                onBack = action,
                subtitle = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSBrandTopBar") { state ->
        with(state) {
            CaixaDSBrandTopBar(title = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerSheet") { state ->
        with(state) {
            CaixaDSDrawerSheet(content = {
                CaixaDSDrawerItem(
                    sample,
                    action
                )
            })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerHeader") { state ->
        with(state) {
            CaixaDSDrawerHeader(
                greeting = sample,
                onClose = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerItem") { state ->
        with(state) {
            CaixaDSDrawerItem(
                label = sample,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerInstitution") { state ->
        with(state) {
            CaixaDSDrawerInstitution(
                expanded = checked,
                onClick = toggle,
                details = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSHomeTopBar") { state ->
        with(state) {
            CaixaDSHomeTopBar(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSModalityTopBar") { state ->
        with(state) {
            CaixaDSModalityTopBar(
                title = sample,
                backgroundColor = CaixaDS.colors.brand,
                contentColor = CaixaDSColorTokens.White,
                contestNumber = "1234",
                drawLabel = sample,
                drawDate = "22/10/2026",
                onBack = action,
                onHelp = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor") { state ->
        with(state) {
            CaixaDSStatusBarColor(
                color = CaixaDS.colors.brand,
                darkIcons = false
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBar") { state ->
        with(state) {
            CaixaDSStatusBar(content = { CaixaDSText(sample) })
        }
    }
)
