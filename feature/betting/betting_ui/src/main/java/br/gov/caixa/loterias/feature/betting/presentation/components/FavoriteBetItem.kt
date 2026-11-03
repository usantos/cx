package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSFavoriteBetCartIcon
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSFavoriteBetDeleteIcon
import br.gov.caixa.loterias.core.designsystem.icon.loteriasCaixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun FavoriteBetItem(favorite: FavoriteBet, showModalityHeader: Boolean, onAdd: () -> Unit, onDelete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lotteryStyle = loteriasCaixaDSLotteryStyle(favorite.modalityId, favorite.modalityName)
    Column(Modifier.fillMaxWidth()) {
        if (showModalityHeader) {
            FavoriteBetModalityHeader(favorite)
        }
        Text(
            favorite.name,
            (Modifier.padding(start = 20.dp, top = 10.dp)).uiId("bet.0103"),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            style = favoriteLegacyTextStyle
        )
        Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 14.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f).padding(top = 10.dp, end = 12.dp)) {
                if (favorite.isSuperSeven) {
                    FavoriteSuperSevenColumns(favorite.numberColumns, lotteryStyle)
                } else {
                    Text(
                        favorite.numbers.joinToString(" - ") { if (it == LOTOMANIA_ZERO_NUMBER) "00" else it.toString().padStart(2, '0') },
                        color = LoteriasCaixaDSColorTokens.RetailSearchText,
                        fontSize = 16.sp,
                        style = favoriteLegacyTextStyle,
                        modifier = Modifier.uiId("bet.0104")
                    )
                }
                if (favorite.heartTeam.isNotBlank() || favorite.luckyMonth.isNotBlank() || favorite.clovers.isNotEmpty()) {
                    Row(Modifier.padding(top = 16.dp)) {
                        val detail = when {
                            favorite.heartTeam.isNotBlank() -> uiStrings.getString(
                                DeliveryR.string.ui_time_do_coracao_1_s_7e407c,
                                favorite.heartTeam
                            )

                            favorite.luckyMonth.isNotBlank() -> uiStrings.getString(
                                DeliveryR.string.ui_mes_de_sorte_1_s_42ca39,
                                favorite.luckyMonth
                            )

                            else -> "Trevos: ${favorite.clovers.joinToString(" - ")}"
                        }
                        Text(
                            detail,
                            color = LoteriasCaixaDSColorTokens.SocialGraphite,
                            fontSize = 16.sp,
                            style = favoriteLegacyTextStyle,
                            modifier = Modifier.uiId("bet.0105")
                        )
                    }
                }
            }
            Row(Modifier.padding(top = 6.dp)) {
                Box(Modifier.size(30.dp).uiId("bet.0106").clickable(onClick = onAdd), contentAlignment = Alignment.Center) {
                    Icon(
                        LoteriasCaixaDSFavoriteBetCartIcon,
                        uiStrings.getString(DeliveryR.string.ui_adicionar_aposta_86ec9a),
                        (Modifier.width(26.dp).height(24.dp)).uiId("bet.0107"),
                        tint = LoteriasCaixaDS.colors.brand
                    )
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(30.dp).uiId("bet.0108").clickable(onClick = onDelete), contentAlignment = Alignment.Center) {
                    Icon(
                        LoteriasCaixaDSFavoriteBetDeleteIcon,
                        uiStrings.getString(DeliveryR.string.ui_excluir_aposta_86e1ac),
                        (Modifier.width(17.dp).height(24.dp)).uiId("bet.0109"),
                        tint = LoteriasCaixaDS.colors.brand
                    )
                }
            }
        }
        Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.DrawerDivider))
    }
}

@Composable
private fun FavoriteBetModalityHeader(favorite: FavoriteBet) {
    val lotteryStyle = loteriasCaixaDSLotteryStyle(favorite.modalityId, favorite.modalityName)
    val color = lotteryStyle.light
    Row(
        Modifier.fillMaxWidth().padding(top = 15.dp).height(55.dp).background(color),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(80.dp).height(55.dp)) {
            FavoriteTrapezoid(favorite.modalityId, Modifier.fillMaxSize())
            Image(loteriasCaixaDSLotteryClover(favorite.modalityId), null, Modifier.padding(start = 18.dp, top = 10.dp).size(28.dp))
        }
        Text(
            favorite.modalityName.lowercase(),
            (Modifier.padding(horizontal = 12.dp)).uiId("bet.0102"),
            color = lotteryStyle.content,
            fontFamily = LoteriasCaixaDSFuturaFontFamily,
            fontSize = 20.sp,
            style = favoriteLegacyTextStyle,
            fontWeight = FontWeight.Bold
        )
    }
}

private const val LOTOMANIA_ZERO_NUMBER = 100
