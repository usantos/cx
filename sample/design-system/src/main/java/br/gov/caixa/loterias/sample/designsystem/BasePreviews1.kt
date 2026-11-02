@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSModalBottomSheet
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSModalNavigationDrawer
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSProvideTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSPullToRefreshBox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSSurface
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

internal val basePreviews1 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton") { state ->
        with(state) {
            LoteriasCaixaDSIconButton(
                onClick = action,
                content = {
                    LoteriasCaixaDSIcon(
                        imageVector = LoteriasCaixaDSNotificationPlaceholder,
                        contentDescription = actionLabel
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSModalBottomSheet") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSModalBottomSheet(
                    onDismissRequest = dismiss,
                    content = {
                        LoteriasCaixaDSText(
                            sample,
                            Modifier.padding(32.dp)
                        )
                    }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSModalNavigationDrawer") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSModalNavigationDrawer(
                    drawerState = androidx.compose.material3.rememberDrawerState(androidx.compose.material3.DrawerValue.Open),
                    drawerContent = {
                        LoteriasCaixaDSButton(onClick = dismiss) {
                            LoteriasCaixaDSText(closeLabel)
                        }
                    },
                    modifier = Modifier.height(180.dp),
                    content = { LoteriasCaixaDSText(hint) }
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSOutlinedButton") { state ->
        with(state) {
            LoteriasCaixaDSOutlinedButton(
                onClick = action,
                content = { LoteriasCaixaDSText(actionLabel) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSProvideTextStyle") { state ->
        with(state) {
            LoteriasCaixaDSProvideTextStyle(
                value = LoteriasCaixaDS.typography.headlineSmall,
                content = { LoteriasCaixaDSText(sample) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSPullToRefreshBox") { state ->
        with(state) {
            LoteriasCaixaDSPullToRefreshBox(
                isRefreshing = false,
                onRefresh = action,
                modifier = Modifier.height(160.dp),
                content = { LoteriasCaixaDSText(hint) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSRadioButton") { state ->
        with(state) {
            LoteriasCaixaDSRadioButton(
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSScaffold") { state ->
        with(state) {
            LoteriasCaixaDSScaffold(
                modifier = Modifier.height(180.dp),
                content = { padding ->
                    LoteriasCaixaDSText(
                        sample,
                        Modifier.padding(padding)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSSurface") { state ->
        with(state) {
            LoteriasCaixaDSSurface(
                onClick = toggle,
                selected = checked,
                content = {
                    LoteriasCaixaDSText(
                        sample,
                        Modifier.padding(20.dp)
                    )
                }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText") { state ->
        with(state) {
            LoteriasCaixaDSText(text = sample)
        }
    }
)
