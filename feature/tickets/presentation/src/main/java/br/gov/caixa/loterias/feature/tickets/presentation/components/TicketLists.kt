package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR

internal val ListTextColor = LoteriasCaixaDSColorTokens.NotificationText

internal fun ticketListText(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
    fontSize = size,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
internal fun LegacyTicketsFilterAction(count: Int, onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(48.dp).uiId("tickets.0065").clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(
            painterResource(R.drawable.legacy_tickets_ic_funil),
            uiStrings.getString(LocalizedR.string.ui_filtrar_06b52c),
            (Modifier.size(24.dp)).uiId("tickets.0066"),
            tint = LoteriasCaixaDSColorTokens.White
        )
        if (count > 0) {
            Box(
                Modifier.align(Alignment.TopEnd).padding(3.dp).size(12.dp)
                    .background(LoteriasCaixaDSColorTokens.TicketNoticeBadge, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    count.toString(),
                    color = LoteriasCaixaDSColorTokens.White,
                    style = ticketListText(8.sp),
                    modifier = Modifier
                        .uiId("tickets.0067")
                )
            }
        }
    }
}

@Composable
internal fun LegacyConfirmedBetsIntro(historyBackend: ConfirmedBetsBackend) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column {
        Image(
            painterResource(
                when (historyBackend) {
                    ConfirmedBetsBackend.NOVA_API -> R.drawable.legacy_tickets_trevo_nova_api
                    ConfirmedBetsBackend.MICROSERVICE -> R.drawable.legacy_tickets_trevo_micro_servico
                    else -> R.drawable.legacy_tickets_trevo_silce
                }
            ),
            null,
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp).height(28.dp)
        )
        Column(Modifier.padding(16.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_o_proximo_ganhador_pode_ser_voce_ja_492506),
                color = LoteriasCaixaDSColorTokens.TicketIntro,
                style = ticketListText(14.sp, FontWeight.Bold),
                modifier = Modifier.uiId("tickets.0068")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_aqui_voce_pode_acompanhar_todas_suas_312271),
                (Modifier.padding(top = 20.dp)).uiId("tickets.0069"),
                color = LoteriasCaixaDSColorTokens.BetAction,
                style = ticketListText(14.sp)
            )
        }
        Box(Modifier.fillMaxWidth().height(56.dp).background(LoteriasCaixaDSColorTokens.TealAction), contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_ver_resultados_08bb7b),
                color = LoteriasCaixaDSColorTokens.White,
                style = ticketListText(12.sp, FontWeight.Bold),
                modifier = Modifier.uiId("tickets.0070")
            )
            Image(painterResource(R.drawable.legacy_tickets_tab_seta), null, Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
internal fun LegacyPurchaseIntro() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_acompanhe_todas_as_compras_que_voce_b96f1f),
        (Modifier.fillMaxWidth().padding(start = 10.dp, end = 17.dp, top = 20.dp)).uiId("tickets.0071"),
        color = LoteriasCaixaDS.colors.brand,
        style = ticketListText(15.5.sp, FontWeight.Bold)
    )
}

@Composable
internal fun LegacyTicketsHelpAction(onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(45.dp).uiId("tickets.0091").clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Image(
            painterResource(R.drawable.legacy_tickets_icon_duvida2),
            uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
            Modifier.size(24.dp)
        )
    }
}
