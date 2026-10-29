package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseRibbon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.caixaDSCampaignPalette
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.icon
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation

@Composable internal fun HomeV2Presentation.Header(onCampaignClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val campaign = LocalCaixaDSCampaign.current
    Row(
        Modifier.fillMaxWidth().background(CaixaDSColorTokens.Blue700)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_texto_67cfd5),
            color = if (campaign == CaixaDSCampaign.OCTOBER_ROSE) caixaDSCampaignPalette(campaign).primary else CxHomeV2Colors.White,
            fontSize = 25.sp,
            modifier = (
                Modifier.size(40.dp).automationId("simlo.home.menu").clickable(
                    onClickLabel = uiStrings.getString(LocalizedR.string.ui_abrir_menu_7db0b2),
                    onClick = actions::menu
                )
                ).uiId("home.0090")
        )
        Box(Modifier.weight(1f).padding(start = 6.dp)) {
            Image(
                painterResource(R.drawable.loterias_caixa_logo),
                uiStrings.getString(LocalizedR.string.ui_loterias_caixa_267b9d),
                modifier = Modifier.width(200.dp).height(24.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterStart
            )
        }
        if (campaign == CaixaDSCampaign.OCTOBER_ROSE) {
            IconButton(onClick = onCampaignClick, modifier = Modifier.uiId("home.0091")) {
                CaixaDSOctoberRoseRibbon(
                    (Modifier.size(29.dp))
                        .uiId("home.0092")
                )
            }
        }
        IconButton(onClick = actions::notifications, modifier = Modifier.uiId("home.0093")) {
            Icon(
                painterResource(R.drawable.menu8_notificacao),
                uiStrings.getString(LocalizedR.string.ui_notificacoes_6b8e9e),
                tint = CxHomeV2Colors.White,
                modifier = Modifier.uiId("home.0094")
            )
        }
    }
}

@Composable
fun HomeV2Presentation.BottomNavigation(modifier: Modifier, density: Density, onHeightChanged: (Dp) -> Unit, onHome: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        modifier.fillMaxWidth()
            .onGloballyPositioned { onHeightChanged(with(density) { it.size.height.toDp() }) }
            .shadow(6.dp)
            .background(Brush.verticalGradient(listOf(CxHomeV2Colors.White.copy(alpha = .98f), CxHomeV2Colors.White.copy(alpha = .96f))))
            .border(1.dp, CxHomeV2Colors.White.copy(alpha = .65f))
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Nav(
            uiStrings.getString(LocalizedR.string.ui_inicio_325dfc),
            R.drawable.trevo_megasena_fundo_branco,
            true,
            (Modifier.weight(1f)).automationId("simlo.home.apostar")
        ) { onHome() }
        Nav("Minhas\nApostas", R.drawable.ic_home_v2_bilhete, false, (Modifier.weight(1f)).uiId("home.0096"), action = actions::games)
        Nav(
            "Carrinho",
            R.drawable.ic_carrinho2,
            false,
            (Modifier.weight(1f))
                .automationId("simlo.home.carrinho"),
            emphasized = true,
            action = actions::cart
        )
        Nav(
            "Resultados",
            R.drawable.ic_home_v2_resultados,
            false,
            (Modifier.weight(1f))
                .automationId("simlo.home.resultados"),
            action = actions::results
        )
        Nav("Apostas\nFavoritas", R.drawable.ic_favoritar2, false, (Modifier.weight(1f)).uiId("home.0099"), action = actions::favorites)
    }
}

@Composable private fun HomeV2Presentation.Nav(
    title: String,
    icon: Int,
    selected: Boolean,
    modifier: Modifier,
    emphasized: Boolean = false,
    originalColors: Boolean = false,
    action: () -> Unit,
) {
    val ink = if (selected || emphasized) blue else CxHomeV2Colors.CFF30445A
    Column(
        modifier.uiId("home.0100").selectable(selected = selected, role = Role.Tab, onClick = action).padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(42.dp).then(if (emphasized) Modifier.background(CxHomeV2Colors.CFFE9F0FC, CircleShape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            if (originalColors) {
                Image(painterResource(icon), null, Modifier.size(28.dp), contentScale = ContentScale.Fit)
            } else {
                Icon(
                    painterResource(icon),
                    null,
                    tint = ink,
                    modifier = (Modifier.size(28.dp)).uiId("home.0101")
                )
            }
        }
        Text(
            title,
            color = ink,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontFamily = regular,
            textAlign = TextAlign.Center,
            modifier = (Modifier.heightIn(min = 28.dp)).uiId("home.0102")
        )
    }
}
