package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.DrawerChevron
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.caixaDSCampaignPalette
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Dimensões, tipografia e cores do menu lateral branco original. */
object CaixaDSDrawerDefaults {
    val itemTextStyle: androidx.compose.ui.text.TextStyle
        @Composable get() = androidx.compose.ui.text.TextStyle(
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    val foreground: androidx.compose.ui.graphics.Color
        @Composable get() = caixaDSCampaignPalette(LocalCaixaDSCampaign.current).primary
    val secondaryForeground: androidx.compose.ui.graphics.Color
        @Composable get() = if (LocalCaixaDSCampaign.current == CaixaDSCampaign.DEFAULT) {
            CaixaDSColorTokens.BetAction
        } else {
            foreground
        }
    val expandedBackground = CaixaDSColorTokens.DrawerChild
    val footerBackground: androidx.compose.ui.graphics.Color
        @Composable get() = foreground
    val background = CaixaDSColorTokens.White
}

@Composable
fun CaixaDSDrawerSheet(campaign: CaixaDSCampaign = CaixaDSCampaign.DEFAULT, content: @Composable ColumnScope.() -> Unit) {
    CaixaDSCampaignTheme(campaign) {
        ModalDrawerSheet(
            modifier = Modifier.width(300.dp),
            drawerContainerColor = CaixaDSDrawerDefaults.background,
            drawerShape = RectangleShape,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Column(
                Modifier.fillMaxSize().background(CaixaDSDrawerDefaults.background)
                    .statusBarsPadding().navigationBarsPadding(),
                content = content
            )
        }
    }
}

@Composable
fun CaixaDSDrawerHeader(greeting: String, onClose: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(start = 20.dp, top = 4.dp, end = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(
                if (LocalCaixaDSCampaign.current == CaixaDSCampaign.OCTOBER_ROSE) {
                    R.drawable.legacy_home_icon_trevo_outubro_rosa
                } else {
                    R.drawable.drawer_header_clover
                }
            ),
            uiStrings.getString(LocalizedR.string.ui_fechar_menu_7ff314),
            Modifier.padding(vertical = 8.dp).size(60.dp).rotate(90f)
                .uiId("ds.0108").clickable(role = Role.Button, onClick = onClose)
        )
        Text(
            greeting,
            Modifier.weight(1f).padding(start = 8.dp, top = 12.dp, bottom = 12.dp)
                .semantics { heading() },
            color = CaixaDSDrawerDefaults.foreground,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            textAlign = TextAlign.Center
        )
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
fun CaixaDSDrawerItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean? = null,
    child: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().background(if (child) CaixaDSDrawerDefaults.expandedBackground else CaixaDSColorTokens.White)
                .heightIn(min = if (child) 58.dp else 55.dp)
                .drawBehind {
                    if (!child) {
                        drawLine(
                            CaixaDSColorTokens.DrawerDivider,
                            Offset(0f, size.height - .5.dp.toPx()),
                            Offset(size.width, size.height - .5.dp.toPx()),
                            1.dp.toPx()
                        )
                    }
                }
                .semantics { expanded?.let { stateDescription = if (it) "Expandido" else "Recolhido" } }
                .uiId("ds.0109").clickable(role = Role.Button, onClick = onClick)
                .padding(start = if (child) 24.dp else 0.dp, end = if (child) 11.dp else 0.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) { it() }
                Spacer(Modifier.width(20.dp))
            }
            Text(
                label,
                Modifier.weight(1f),
                color = if (child &&
                    LocalCaixaDSCampaign.current == CaixaDSCampaign.DEFAULT
                ) {
                    CaixaDSColorTokens.SocialGraphite
                } else {
                    CaixaDSDrawerDefaults.secondaryForeground
                },
                style = CaixaDSDrawerDefaults.itemTextStyle
            )
            expanded?.let {
                Icon(
                    CaixaDSIcons.DrawerChevron,
                    null,
                    Modifier.padding(start = 2.dp).size(10.dp, 22.dp).rotate(if (it) 180f else 0f),
                    tint = CaixaDSDrawerDefaults.foreground
                )
            }
        }
        if (child) Box(Modifier.padding(horizontal = 15.dp).fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.White))
    }
}

@Composable
fun CaixaDSDrawerInstitution(expanded: Boolean, onClick: () -> Unit, details: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.fillMaxWidth().background(CaixaDSColorTokens.SocialCanvas).padding(bottom = 5.dp)
            .heightIn(min = 42.dp).semantics { stateDescription = if (expanded) "Expandido" else "Recolhido" }
            .uiId("ds.0110").clickable(role = Role.Button, onClick = onClick)
    ) {
        Column(Modifier.fillMaxWidth().padding(start = 60.dp, top = 10.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_caixa_loterias_567fcc),
                Modifier.fillMaxWidth().padding(end = 50.dp),
                color = if (
                    LocalCaixaDSCampaign.current == CaixaDSCampaign.DEFAULT
                ) {
                    CaixaDSColorTokens.DrawerInstitutionHeading
                } else {
                    CaixaDSDrawerDefaults.foreground
                },
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = CaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
            if (expanded) {
                Text(
                    details,
                    Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 6.dp),
                    color = CaixaDSColorTokens.DrawerInstitutionText,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = CaixaDSRegularFontFamily,
                        fontSize = 14.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    )
                )
            }
        }
        Icon(
            CaixaDSIcons.DrawerChevron,
            null,
            Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 24.dp)
                .size(10.dp, 22.dp).rotate(if (expanded) 180f else 0f),
            tint = CaixaDSDrawerDefaults.foreground
        )
    }
}
