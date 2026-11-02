package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSOctoberRoseRibbon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.loteriasCaixaDSCampaignPalette
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation

@Composable internal fun HomeV2Presentation.Header(onCampaignClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val campaign = LocalLoteriasCaixaDSCampaign.current
    Row(
        Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.Blue700)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_texto_67cfd5),
            color = if (campaign ==
                LoteriasCaixaDSCampaign.OCTOBER_ROSE
            ) {
                loteriasCaixaDSCampaignPalette(campaign).primary
            } else {
                CxHomeV2Colors.White
            },
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
        if (campaign == LoteriasCaixaDSCampaign.OCTOBER_ROSE) {
            IconButton(onClick = onCampaignClick, modifier = Modifier.uiId("home.0091")) {
                LoteriasCaixaDSOctoberRoseRibbon(
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
