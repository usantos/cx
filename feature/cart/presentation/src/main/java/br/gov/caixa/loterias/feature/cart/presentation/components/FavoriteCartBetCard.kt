package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSRecurringIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.cart.R
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetClovers
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetComplement
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetNumbers
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteDetailModalityName
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteTextStyle

@Composable
internal fun FavoriteBetCard(bet: FavoriteCartBet, onDelete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = caixaDSLotteryStyle(bet.modalityId ?: 0, bet.modality)
    val color = style.listText
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(10.dp)) {
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(top = 5.dp, end = 30.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        favoriteDetailModalityName(bet),
                        color = color,
                        style = favoriteTextStyle.copy(fontFamily = CaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold),
                        modifier = Modifier.uiId("cart.0060")
                    )
                    if (bet.recurringDraws > 0) {
                        Icon(
                            CaixaDSRecurringIcon,
                            uiStrings.getString(LocalizedR.string.ui_teimosinha_dba980),
                            (Modifier.padding(start = 5.dp).size(20.dp)).uiId("cart.0061"),
                            tint = CaixaDSColorTokens.FavoriteCartCard
                        )
                    }
                }
                val complement = favoriteBetComplement(bet)
                if (complement != null) {
                    Text(
                        complement,
                        color = color,
                        style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.uiId("cart.0062")
                    )
                }
                if (bet.modalityId == 7 && bet.numberColumns.isNotEmpty() && !bet.surprise) {
                    FavoriteSuperSevenNumbers(bet, color, style.light)
                } else {
                    Text(
                        favoriteBetNumbers(bet),
                        (Modifier.padding(top = 10.dp, end = 30.dp)).uiId("cart.0065"),
                        color = CaixaDSColorTokens.SecondaryText,
                        style = favoriteTextStyle
                    )
                }
                if (bet.cloverCount > 0) {
                    Text(
                        favoriteBetClovers(bet),
                        (Modifier.padding(top = 10.dp)).uiId("cart.0066"),
                        color = CaixaDSColorTokens.SecondaryText,
                        style = favoriteTextStyle
                    )
                }
            }
            Box(
                Modifier.align(Alignment.TopEnd).size(30.dp)
                    .uiId("cart.0067").clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.legacy_favorite_ic_icone_lixeira_verde),
                    uiStrings.getString(LocalizedR.string.ui_excluir_aposta_86e1ac),
                    (Modifier.size(17.dp, 20.dp)).uiId("cart.0068"),
                    tint = CaixaDSColorTokens.FavoriteCartCard
                )
            }
        }
        HorizontalDivider(color = CaixaDSColorTokens.FavoriteDivider, modifier = Modifier.uiId("cart.0069"))
    }
}

@Composable
private fun FavoriteSuperSevenNumbers(
    bet: FavoriteCartBet,
    color: androidx.compose.ui.graphics.Color,
    backgroundColor: androidx.compose.ui.graphics.Color,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        bet.numberColumns.forEachIndexed { index, numbers ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(
                    Modifier.size(25.dp).background(backgroundColor, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_1_s_d020a2, (index + 1).toString()),
                        color = color,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.uiId("cart.0063")
                    )
                }
                numbers.forEach { number ->
                    Box(
                        Modifier.size(30.dp).border(1.dp, color, RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(number.toString(), color = color, fontSize = 14.sp, modifier = Modifier.uiId("cart.0064"))
                    }
                }
            }
        }
    }
}
