package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun FavoriteListHeader(selectedName: String, onFilter: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_aqui_ficam_seus_numeros_favoritos_pr_8b2b23),
            (Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 5.dp)).uiId("bet.0115"),
            fontSize = 18.sp,
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            style = favoriteLegacyTextStyle
        )
        Box(
            Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp
            ).fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.DrawerDivider)
        )
        Column(Modifier.fillMaxWidth().uiId("bet.0116").clickable(onClick = onFilter)) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_selecione_a_modalidade_1927fb),
                (Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp)).uiId("bet.0117"),
                fontSize = 16.sp,
                color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
                style = favoriteLegacyTextStyle
            )
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    selectedName,
                    (Modifier.weight(1f).padding(end = 16.dp)).uiId("bet.0118"),
                    fontSize = 18.sp,
                    color = LoteriasCaixaDSColorTokens.RetailSearchText,
                    style = favoriteLegacyTextStyle
                )
                Image(
                    painterResource(DeliveryR.drawable.ic_filtro_circular),
                    uiStrings.getString(DeliveryR.string.ui_selecionar_modalidade_60a513),
                    Modifier.size(25.dp)
                )
            }
        }
        Box(
            Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp
            ).fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.DrawerDivider)
        )
    }
}
