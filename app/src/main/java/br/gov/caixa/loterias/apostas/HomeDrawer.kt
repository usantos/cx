package br.gov.caixa.loterias.apostas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.home.HomeDrawerItem
import br.gov.caixa.loterias.feature.home.homeDrawerMenu
import br.gov.caixa.loterias.feature.home.homeDrawerUserName

@Composable
internal fun HomeDrawer(
    features: HomeFeatureConfiguration,
    shakeSettings: br.gov.caixa.loterias.core.domain.betting.ShakeSettingsRepository? = null,
    drawerOpen: Boolean = true,
    userName: String?,
    environmentLabel: String?,
    octoberRoseTheme: Boolean = false,
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
    val drawerStyle = homeDrawerStyle(octoberRoseTheme)
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
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = CaixaDSColorTokens.White,
        drawerShape = androidx.compose.ui.graphics.RectangleShape,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            DrawerGreeting(userName, octoberRoseTheme, drawerStyle.accent)
            // Calibrado contra o include legado: o header intrínseco do Compose mede diferente do TextView.
            Column(Modifier.weight(1f).padding(top = 5.dp).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)) {
                menu.forEachIndexed { index, group ->
                    val expanded = when (group.item) {
                        HomeDrawerItem.MY_AREA -> areaExpanded
                        HomeDrawerItem.RESPONSIBLE -> responsibleExpanded
                        else -> false
                    }
                    // A captura no XCover7 exige meio dp a menos para igualar a altura efetiva da lista legada.
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 54.5.dp).drawBehind {
                            if (index != menu.lastIndex) {
                                val stroke = 1.dp.toPx()
                                drawLine(CaixaDSColorTokens.LegacyDrawerDivider,
                                    Offset(0f, size.height - stroke / 2), Offset(size.width, size.height - stroke / 2), stroke)
                            }
                        }
                            .semantics { if (group.children.isNotEmpty()) stateDescription = if (expanded) "Expandido" else "Recolhido" }
                            .clickable(role = Role.Button) {
                                when (group.item) {
                                    HomeDrawerItem.MY_AREA -> areaExpanded = !areaExpanded
                                    HomeDrawerItem.RESPONSIBLE -> responsibleExpanded = !responsibleExpanded
                                    else -> select(group.item)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DrawerItemIcon(group.item, drawerStyle.accent)
                        Text(group.item.label, Modifier.padding(start = 20.dp, end = 2.dp).weight(1f),
                            style = drawerTextStyle(16.sp, FontWeight.SemiBold),
                            color = drawerStyle.rowLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (group.children.isNotEmpty()) DrawerChevron(expanded, drawerStyle.accent)
                    }
                    if (expanded) group.children.forEach { child ->
                        Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.LegacyDrawerChild).drawBehind {
                            val inset = 15.dp.toPx()
                            val stroke = 1.dp.toPx()
                            drawLine(CaixaDSColorTokens.White, Offset(inset, size.height - stroke / 2),
                                Offset(size.width - inset, size.height - stroke / 2), stroke)
                        }
                            .clickable(role = Role.Button) {
                                if (group.item == HomeDrawerItem.MY_AREA) areaExpanded = false else responsibleExpanded = false
                                select(child)
                            }) {
                            Box(Modifier.fillMaxWidth().heightIn(min = 58.dp).padding(start = 24.dp, end = 11.dp),
                                contentAlignment = Alignment.CenterStart) {
                                Text(child.label, style = drawerTextStyle(16.sp, FontWeight.SemiBold),
                                    color = drawerStyle.rowLabel)
                            }
                        }
                    }
                }
                shakeSettings?.let { ShakeMenuRow(it, drawerOpen, drawerStyle.accent) }
            }
            Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.LegacySocialCanvas).padding(bottom = 5.dp)) {
                Row(Modifier.fillMaxWidth().heightIn(min = 40.dp)
                    .semantics { stateDescription = if (institutionExpanded) "Expandido" else "Recolhido" }
                    .clickable(role = Role.Button) { institutionExpanded = !institutionExpanded }
                    .padding(start = 60.dp, end = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("CAIXA Loterias", Modifier.weight(1f), style = drawerTextStyle(14.sp, FontWeight.Bold),
                        color = drawerStyle.footerLabel)
                    // In the legacy footer the arrow is an ImageView (10 x 22 dp) using
                    // FIT_CENTER. Its visible vector is therefore 10 x 6.25 dp.
                    DrawerChevron(
                        institutionExpanded,
                        drawerStyle.accent,
                        rotateWhenCollapsed = true,
                        width = 10.dp,
                        height = 6.25.dp,
                    )
                }
                if (institutionExpanded) Text(
                    "CNPJ 24.038.490.0001-83\nED. José Alencar - SEPN 512, Conjunto C, Lote 12, Asa Norte, Brasília - DF\nCEP 70760-500 BRASÍLIA - DF",
                    Modifier.padding(start = 60.dp, top = 5.dp, bottom = 6.dp),
                    style = drawerTextStyle(14.sp), maxLines = 5,
                    color = CaixaDSColorTokens.LegacyDrawerInstitutionText,
                )
            }
            Row(Modifier.fillMaxWidth().heightIn(min = 50.dp).background(drawerStyle.exitBackground)
                .clickable(role = Role.Button) { confirmationName = HomeDrawerConfirmation.LOGOUT.name }
                .padding(start = 24.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(CaixaDSIcons.DrawerLogout, null, Modifier.size(28.dp), tint = CaixaDSColorTokens.White)
                Text("Sair", Modifier.padding(start = 8.dp), color = CaixaDSColorTokens.White,
                    style = drawerTextStyle(18.sp))
                Box(Modifier.weight(1f).padding(horizontal = 5.dp), contentAlignment = Alignment.Center) {
                    if (environmentLabel != null) Text(environmentLabel, color = CaixaDSColorTokens.White,
                        style = drawerTextStyle(18.sp))
                }
                Text(BuildConfig.VERSION_NAME, color = CaixaDSColorTokens.White, style = drawerTextStyle(14.sp))
            }
        }
    }
    confirmationName?.let(HomeDrawerConfirmation::valueOf)?.let { confirmation ->
        CaixaDSLegacyDialog(
            title = confirmation.title, message = confirmation.message,
            positiveLabel = if (confirmation == HomeDrawerConfirmation.LOGOUT) "Sim" else "Confirmar",
            onPositive = {
                confirmationName = null
                val url = confirmation.url
                if (url == null) onLogout() else {
                    try { uriHandler.openUri(url) } catch (_: IllegalArgumentException) { onExternalFailure() }
                }
            },
            negativeLabel = if (confirmation == HomeDrawerConfirmation.LOGOUT) "Não" else "Cancelar",
            onNegative = { confirmationName = null },
        )
    }
}

@Composable
private fun DrawerGreeting(userName: String?, octoberRoseTheme: Boolean, accent: androidx.compose.ui.graphics.Color) {
    val displayName = remember(userName) { homeDrawerUserName(userName) }
    Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 30.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(if (octoberRoseTheme) R.drawable.icon_trevo_rosa else R.drawable.icon_trevo), null,
            Modifier.padding(vertical = 8.dp).rotate(90f))
        Text(buildAnnotatedString {
            if (displayName.isEmpty()) append("Olá, seja bem-vindo ao  App Loterias.") else {
                append("Olá, ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(displayName) }
                append("!")
            }
        }, Modifier.weight(1f).padding(start = 8.dp, top = 12.dp, bottom = 12.dp).semantics { heading() },
            style = drawerTextStyle(18.sp), color = accent, textAlign = TextAlign.Center)
    }
}

@Composable
private fun DrawerChevron(
    expanded: Boolean,
    tint: androidx.compose.ui.graphics.Color,
    rotateWhenCollapsed: Boolean = false,
    width: androidx.compose.ui.unit.Dp = 16.dp,
    height: androidx.compose.ui.unit.Dp = 10.dp,
) {
    val rotation = if (rotateWhenCollapsed) if (expanded) 0f else 180f else 0f
    Icon(CaixaDSIcons.DrawerChevron, null, Modifier.width(width).height(height).rotate(rotation), tint = tint)
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
    // The legacy ImageView uses the icon drawable as its background and stretches it
    // over a square 20 dp view. FIT would preserve the source vector's aspect ratio.
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        contentScale = ContentScale.FillBounds,
        colorFilter = ColorFilter.tint(tint),
    )
}

private fun drawerTextStyle(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily, fontSize = size, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)
