package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
fun CaixaDSHomeTopBar(
    unreadNotificationCount: Int,
    octoberRose: Boolean,
    quickAccessExpanded: Boolean,
    cloverRotation: Float,
    onMenu: () -> Unit,
    onNotifications: () -> Unit,
    onCampaign: () -> Unit,
    onQuickAccess: () -> Unit,
    quickAccessToggleEnabled: Boolean = true,
    campaignRotation: Float = 0f,
    actionsVisible: Boolean = true,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(start = 4.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (actionsVisible) {
                IconButton(onClick = onMenu, shape = CircleShape, modifier = Modifier.uiId("ds.0111")) {
                    Image(
                        painter = painterResource(R.drawable.caixads_home_menu),
                        contentDescription = uiStrings.getString(LocalizedR.string.ui_abrir_menu_7db0b2),
                        modifier = Modifier.size(width = 18.dp, height = 12.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = if (octoberRose) {
                            ColorFilter.tint(CaixaDSColorTokens.OctoberRose)
                        } else {
                            null
                        }
                    )
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onNotifications, shape = CircleShape, modifier = Modifier.uiId("ds.0112")) {
                    NotificationBell(unreadNotificationCount)
                }
                if (octoberRose) {
                    IconButton(
                        onClick = onCampaign,
                        enabled = quickAccessToggleEnabled,
                        shape = CircleShape,
                        modifier = Modifier
                            .uiId("ds.0113")
                    ) {
                        br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseRibbon(
                            (Modifier.size(29.dp).rotate(campaignRotation)).uiId("ds.0114")
                        )
                    }
                }
                IconButton(
                    onClick = onQuickAccess,
                    enabled = quickAccessToggleEnabled,
                    shape = CircleShape,
                    modifier = Modifier
                        .uiId("ds.0115")
                ) {
                    Image(
                        painter = painterResource(
                            if (octoberRose) {
                                br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_home_icon_trevo_outubro_rosa
                            } else {
                                R.drawable.caixads_home_clover
                            }
                        ),
                        contentDescription = if (quickAccessExpanded) {
                            uiStrings.getString(
                                LocalizedR.string.ui_ocultar_atalhos_ba8e63
                            )
                        } else {
                            uiStrings.getString(
                                LocalizedR.string.ui_apresentar_atalhos_7381c6
                            )
                        },
                        modifier = Modifier.size(30.dp).rotate(cloverRotation),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationBell(unreadNotificationCount: Int) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(29.dp)) {
        Image(
            painterResource(R.drawable.caixads_home_bell),
            uiStrings.getString(LocalizedR.string.ui_notificacoes_6b8e9e),
            Modifier.size(29.dp)
        )
        if (unreadNotificationCount > 0) {
            Image(
                painterResource(R.drawable.caixads_home_unread_badge),
                uiStrings.getString(LocalizedR.string.ui_notificacoes_nao_lidas_24be56),
                Modifier.size(12.dp).align(Alignment.TopEnd)
            )
        }
    }
}
