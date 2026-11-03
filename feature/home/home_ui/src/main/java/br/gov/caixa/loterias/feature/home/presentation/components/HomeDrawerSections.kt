package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerHeader
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerItem
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeDrawerUserName
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerItem
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable
internal fun DrawerGreeting(userName: String?, onClose: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val displayName = remember(userName) { homeDrawerUserName(userName) }
    LoteriasCaixaDSDrawerHeader(
        greeting = if (displayName.isEmpty()) {
            uiStrings.getString(
                DeliveryR.string.ui_ola_seja_bem_vindo_ao_app_loterias_6cb198
            )
        } else {
            uiStrings.getString(DeliveryR.string.ui_ola_1_s_9c8eeb, displayName)
        },
        onClose = onClose
    )
}

@Composable
internal fun DrawerItemIcon(item: HomeDrawerItem, tint: androidx.compose.ui.graphics.Color) {
    val icon = when (item) {
        HomeDrawerItem.MY_AREA -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_my_area
        HomeDrawerItem.RESULTS -> br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_drawer_results
        HomeDrawerItem.RAPID -> DeliveryR.drawable.menu4_rapidao
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

@Composable
internal fun DrawerMenuGroup(
    item: HomeDrawerItem,
    children: List<HomeDrawerItem>,
    expanded: Boolean,
    accent: Color,
    onGroupClick: () -> Unit,
    onChildClick: (HomeDrawerItem) -> Unit,
) {
    LoteriasCaixaDSDrawerItem(
        label = item.label,
        expanded = expanded.takeIf { children.isNotEmpty() },
        onClick = onGroupClick,
        icon = { DrawerItemIcon(item, accent) },
        modifier = Modifier.uiId("home.0026")
    )
    if (expanded) {
        children.forEach { child ->
            LoteriasCaixaDSDrawerItem(label = child.label, child = true, onClick = {
                onChildClick(child)
            }, modifier = Modifier.uiId("home.0027"))
        }
    }
}
