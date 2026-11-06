package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.loteriasCaixaDSCampaignPalette
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Dimensões, tipografia e cores do menu lateral branco original. */
object LoteriasCaixaDSDrawerDefaults {
    val itemTextStyle: androidx.compose.ui.text.TextStyle
        @Composable get() = androidx.compose.ui.text.TextStyle(
            fontFamily = LoteriasCaixaDSFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    val foreground: androidx.compose.ui.graphics.Color
        @Composable get() = loteriasCaixaDSCampaignPalette(LocalLoteriasCaixaDSCampaign.current).primary
    val secondaryForeground: androidx.compose.ui.graphics.Color
        @Composable get() = if (LocalLoteriasCaixaDSCampaign.current == LoteriasCaixaDSCampaign.DEFAULT) {
            LoteriasCaixaDSColorTokens.BetAction
        } else {
            foreground
        }
    val expandedBackground = LoteriasCaixaDSColorTokens.DrawerChild
    val footerBackground: androidx.compose.ui.graphics.Color
        @Composable get() = foreground
    val background = LoteriasCaixaDSColorTokens.White
}

@Composable
fun LoteriasCaixaDSDrawerSheet(
    campaign: LoteriasCaixaDSCampaign = LoteriasCaixaDSCampaign.DEFAULT,
    content: @Composable ColumnScope.() -> Unit,
) {
    LoteriasCaixaDSCampaignTheme(campaign) {
        ModalDrawerSheet(
            modifier = Modifier.width(300.dp),
            drawerContainerColor = LoteriasCaixaDSDrawerDefaults.background,
            drawerShape = RectangleShape,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Column(
                Modifier.fillMaxSize().background(LoteriasCaixaDSDrawerDefaults.background)
                    .statusBarsPadding().navigationBarsPadding(),
                content = content
            )
        }
    }
}

@Composable
fun LoteriasCaixaDSDrawerHeader(greeting: String, onClose: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(start = 20.dp, top = 4.dp, end = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(
                if (LocalLoteriasCaixaDSCampaign.current == LoteriasCaixaDSCampaign.OCTOBER_ROSE) {
                    R.drawable.legacy_home_icon_trevo_outubro_rosa
                } else {
                    R.drawable.drawer_header_clover
                }
            ),
            uiStrings.getString(LocalizedR.string.ui_fechar_menu_7ff314),
            Modifier.padding(vertical = 8.dp).size(60.dp).rotate(DrawerMetrics.CHEVRON_ROTATION_DEGREES)
                .uiId("ds.0108").clickable(role = Role.Button, onClick = onClose)
        )
        Text(
            greeting,
            Modifier.weight(1f).padding(start = 8.dp, top = 12.dp, bottom = 12.dp)
                .semantics { heading() },
            color = LoteriasCaixaDSDrawerDefaults.foreground,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = LoteriasCaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            textAlign = TextAlign.Center
        )
    }
    Spacer(Modifier.height(12.dp))
}

private object DrawerMetrics {
    const val CHEVRON_ROTATION_DEGREES = 90f
}
