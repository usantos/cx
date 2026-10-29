@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalBottomSheet
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalNavigationDrawer
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSProvideTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSPullToRefreshBox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSSurface
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

internal val basePreviews1 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton") { state ->
        with(state) {
            CaixaDSIconButton(
                onClick = action,
                content = {
                    CaixaDSIcon(
                        imageVector = CaixaDSNotificationPlaceholder,
                        contentDescription = actionLabel
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalBottomSheet") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSModalBottomSheet(
                    onDismissRequest = dismiss,
                    content = {
                        CaixaDSText(
                            sample,
                            Modifier.padding(32.dp)
                        )
                    }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalNavigationDrawer") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSModalNavigationDrawer(
                    drawerState = androidx.compose.material3.rememberDrawerState(androidx.compose.material3.DrawerValue.Open),
                    drawerContent = {
                        CaixaDSButton(onClick = dismiss) {
                            CaixaDSText(closeLabel)
                        }
                    },
                    modifier = Modifier.height(180.dp),
                    content = { CaixaDSText(hint) }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSOutlinedButton") { state ->
        with(state) {
            CaixaDSOutlinedButton(
                onClick = action,
                content = { CaixaDSText(actionLabel) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSProvideTextStyle") { state ->
        with(state) {
            CaixaDSProvideTextStyle(
                value = CaixaDS.typography.headlineSmall,
                content = { CaixaDSText(sample) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSPullToRefreshBox") { state ->
        with(state) {
            CaixaDSPullToRefreshBox(
                isRefreshing = false,
                onRefresh = action,
                modifier = Modifier.height(160.dp),
                content = { CaixaDSText(hint) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSRadioButton") { state ->
        with(state) {
            CaixaDSRadioButton(
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSScaffold") { state ->
        with(state) {
            CaixaDSScaffold(
                modifier = Modifier.height(180.dp),
                content = { padding ->
                    CaixaDSText(
                        sample,
                        Modifier.padding(padding)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSSurface") { state ->
        with(state) {
            CaixaDSSurface(
                onClick = toggle,
                selected = checked,
                content = {
                    CaixaDSText(
                        sample,
                        Modifier.padding(20.dp)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText") { state ->
        with(state) {
            CaixaDSText(text = sample)
        }
    }
)
