package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerSheet
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.presentation.mapper.toDSCampaign
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeDrawerMenu
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeDrawerRoute
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerConfirmation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerItem
import br.gov.caixa.loterias.feature.home.presentation.style.homeDrawerStyle

@Composable
fun RenderHomeDrawer(
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
    LoteriasCaixaDSDrawerSheet(campaign.toDSCampaign()) {
        DrawerGreeting(userName, onClose)
        Column(Modifier.weight(1f).uiId("home.0025").verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            menu.forEach { group ->
                DrawerMenuGroup(
                    group.item,
                    group.children,
                    when (group.item) {
                        HomeDrawerItem.MY_AREA -> areaExpanded
                        HomeDrawerItem.RESPONSIBLE -> responsibleExpanded
                        else -> false
                    },
                    drawerStyle.accent,
                    onGroupClick = {
                        when (group.item) {
                            HomeDrawerItem.MY_AREA -> areaExpanded = !areaExpanded
                            HomeDrawerItem.RESPONSIBLE -> responsibleExpanded = !responsibleExpanded
                            else -> select(group.item)
                        }
                    },
                    onChildClick = { child ->
                        if (group.item == HomeDrawerItem.MY_AREA) areaExpanded = false else responsibleExpanded = false
                        select(child)
                    }
                )
            }
            DrawerShakeSettings(preferenceState, drawerOpen, drawerStyle, onToggleShake, onConsumeShakeTooltip)
        }
        DrawerFooter(institutionExpanded, { institutionExpanded = !institutionExpanded }, environmentLabel, drawerStyle.footerLabel) {
            confirmationName = HomeDrawerConfirmation.LOGOUT.name
        }
    }
    confirmationName?.let(HomeDrawerConfirmation::valueOf)?.let { confirmation ->
        DrawerConfirmation(confirmation, onPositive = {
            confirmationName = null
            executeDrawerConfirmation(confirmation, onLogout, onExternalFailure, uriHandler::openUri)
        }, onDismiss = { confirmationName = null })
    }
}

private fun executeDrawerConfirmation(
    confirmation: HomeDrawerConfirmation,
    onLogout: () -> Unit,
    onExternalFailure: () -> Unit,
    openUri: (String) -> Unit,
) {
    val url = confirmation.url
    if (url == null) {
        onLogout()
    } else {
        try {
            openUri(url)
        } catch (_: IllegalArgumentException) {
            onExternalFailure()
        }
    }
}
