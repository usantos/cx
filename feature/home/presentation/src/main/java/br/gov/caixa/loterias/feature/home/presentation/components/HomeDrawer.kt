package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerDefaults
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerHeader
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerItem
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerSheet
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.DrawerLogout
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.presentation.mapper.toDSCampaign
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeDrawerMenu
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeDrawerRoute
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeDrawerUserName
import br.gov.caixa.loterias.feature.home.presentation.mapper.icon
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerConfirmation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerItem
import br.gov.caixa.loterias.feature.home.presentation.style.homeDrawerStyle

@Composable
fun HomeDrawer(
    features: HomeFeatureConfiguration,
    preferenceState: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState? = null,
    onToggleShake: (Boolean?) -> Unit = {},
    onConsumeShakeTooltip: () -> Unit = {},
    drawerOpen: Boolean = true,
    userName: String?,
    environmentLabel: String?,
    campaign: br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign? = null,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit,
    onExternalFailure: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var areaExpanded by rememberSaveable { mutableStateOf(false) }
    var responsibleExpanded by rememberSaveable { mutableStateOf(false) }
    var institutionExpanded by rememberSaveable { mutableStateOf(false) }
    var confirmationName by rememberSaveable { mutableStateOf<String?>(null) }
    val uriHandler = LocalUriHandler.current
    val drawerStyle = homeDrawerStyle(campaign)
    val menu = remember(features) { homeDrawerMenu(features) }
    val select: (HomeDrawerItem) -> Unit = { item ->
        when (item) {
            HomeDrawerItem.POLICY -> {
                onClose()
                confirmationName = HomeDrawerConfirmation.POLICY.name
            }

            HomeDrawerItem.ABOUT -> confirmationName = HomeDrawerConfirmation.ABOUT.name

            else -> homeDrawerRoute(item)?.let(onOpen)
        }
    }
    CaixaDSDrawerSheet(campaign.toDSCampaign()) {
        DrawerGreeting(userName, onClose)
        Column(Modifier.weight(1f).uiId("home.0025").verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            menu.forEach { group ->
                val expanded = when (group.item) {
                    HomeDrawerItem.MY_AREA -> areaExpanded
                    HomeDrawerItem.RESPONSIBLE -> responsibleExpanded
                    else -> false
                }
                CaixaDSDrawerItem(
                    label = group.item.label,
                    expanded = expanded.takeIf { group.children.isNotEmpty() },
                    onClick = {
                        when (group.item) {
                            HomeDrawerItem.MY_AREA -> areaExpanded = !areaExpanded
                            HomeDrawerItem.RESPONSIBLE -> responsibleExpanded = !responsibleExpanded
                            else -> select(group.item)
                        }
                    },
                    icon = { DrawerItemIcon(group.item, drawerStyle.accent) },
                    modifier = Modifier.uiId("home.0026")
                )
                if (expanded) {
                    group.children.forEach { child ->
                        CaixaDSDrawerItem(label = child.label, child = true, onClick = {
                            if (group.item == HomeDrawerItem.MY_AREA) areaExpanded = false else responsibleExpanded = false
                            select(child)
                        }, modifier = Modifier.uiId("home.0027"))
                    }
                }
            }
            preferenceState?.let {
                Box(Modifier.fillMaxWidth()) {
                    ShakeMenuRow(
                        it, onToggleShake, onConsumeShakeTooltip, drawerOpen, drawerStyle.shakeAccent, labelColor = drawerStyle.rowLabel,
                        iconColor = drawerStyle.accent,
                        onAccent = drawerStyle.shakeOnAccent,
                        trackBackground = if (drawerStyle.invertShakeColors) {
                            CaixaDSColorTokens.White
                        } else {
                            CaixaDSDrawerDefaults.expandedBackground
                        },
                        tooltipBackground = drawerStyle.shakeTooltipBackground,
                        invertSwitchColors = drawerStyle.invertShakeColors
                    )
                }
            }
        }
        Column(Modifier.fillMaxWidth().background(CaixaDSDrawerDefaults.footerBackground)) {
            br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerInstitution(
                expanded = institutionExpanded,
                onClick = { institutionExpanded = !institutionExpanded },
                details = uiStrings.getString(LocalizedR.string.ui_cnpj_24_038_490_0001_83_ed_jose_alen_ade8ce)
            )
            Row(
                Modifier.fillMaxWidth().heightIn(min = 50.dp)
                    .automationId("simlo.home.sair").clickable(role = Role.Button) { confirmationName = HomeDrawerConfirmation.LOGOUT.name }
                    .padding(start = 24.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(CaixaDSIcons.DrawerLogout, null, (Modifier.size(18.dp)).uiId("home.0029"), tint = drawerStyle.footerLabel)
                Text(
                    uiStrings.getString(LocalizedR.string.ui_sair_2d023d),
                    (Modifier.padding(start = 8.dp)).uiId("home.0030"),
                    color = drawerStyle.footerLabel,
                    style = CaixaDS.typography.bodyMedium.copy(fontSize = 18.sp)
                )
                Spacer(Modifier.weight(1f))
                if (environmentLabel != null) {
                    Text(
                        environmentLabel,
                        (Modifier.padding(horizontal = 12.dp)).uiId("home.0031"),
                        color = drawerStyle.footerLabel,
                        style = CaixaDS.typography.bodySmall.copy(fontSize = 14.sp)
                    )
                }
                Text(
                    LocalHomeRuntimeConfiguration.current.versionName,
                    color = drawerStyle.footerLabel,
                    style = CaixaDS.typography.bodySmall.copy(fontSize = 14.sp),
                    modifier = Modifier.uiId("home.0032")
                )
            }
        }
    }
    confirmationName?.let(HomeDrawerConfirmation::valueOf)?.let { confirmation ->
        CaixaDSDialog(
            title = confirmation.title,
            message = confirmation.message,
            positiveLabel = if (
                confirmation == HomeDrawerConfirmation.LOGOUT
            ) {
                uiStrings.getString(LocalizedR.string.ui_sim_931345)
            } else {
                uiStrings.getString(LocalizedR.string.ui_confirmar_717bed)
            },
            onPositive = {
                confirmationName = null
                val url = confirmation.url
                if (url == null) {
                    onLogout()
                } else {
                    try {
                        uriHandler.openUri(url)
                    } catch (_: IllegalArgumentException) {
                        onExternalFailure()
                    }
                }
            },
            negativeLabel = if (
                confirmation == HomeDrawerConfirmation.LOGOUT
            ) {
                uiStrings.getString(LocalizedR.string.ui_nao_9bc947)
            } else {
                uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb)
            },
            onNegative = { confirmationName = null }
        )
    }
}

@Composable
private fun DrawerGreeting(userName: String?, onClose: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val displayName = remember(userName) { homeDrawerUserName(userName) }
    CaixaDSDrawerHeader(
        greeting = if (displayName.isEmpty()) {
            uiStrings.getString(
                LocalizedR.string.ui_ola_seja_bem_vindo_ao_app_loterias_6cb198
            )
        } else {
            uiStrings.getString(LocalizedR.string.ui_ola_1_s_9c8eeb, displayName)
        },
        onClose = onClose
    )
}

@Composable
private fun DrawerItemIcon(item: HomeDrawerItem, tint: androidx.compose.ui.graphics.Color) {
    val icon = when (item) {
        HomeDrawerItem.MY_AREA -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_my_area
        HomeDrawerItem.RESULTS -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_results
        HomeDrawerItem.RAPID -> R.drawable.menu4_rapidao
        HomeDrawerItem.TICKETS -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_tickets
        HomeDrawerItem.RESPONSIBLE -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_responsible
        HomeDrawerItem.SOCIAL -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_social
        HomeDrawerItem.TERMS -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_terms
        HomeDrawerItem.QUESTIONS -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_questions
        HomeDrawerItem.ABOUT -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_about
        else -> return
    }
    // Mantém a arte original do menu, tonalizada para a superfície azul institucional.
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(tint)
    )
}
