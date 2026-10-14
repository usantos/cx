package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePool
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch
import kotlin.math.abs

private fun marketplaceText(size: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily, fontSize = size.sp, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

internal data class MarketplaceColors(val light: Color, val dark: Color, val content: Color = CaixaDSColorTokens.White)
internal fun marketplaceColors(modality: String): MarketplaceColors = when {
    modality.contains("LOTOFACIL", true) || modality.contains("LOTOFÁCIL", true) -> MarketplaceColors(CaixaDSColorTokens.LegacyLotofacilLight, CaixaDSColorTokens.LegacyLotofacilDark)
    modality.contains("QUINA", true) -> MarketplaceColors(CaixaDSColorTokens.LegacyQuinaLight, CaixaDSColorTokens.LegacyQuinaDark)
    modality.contains("LOTOMANIA", true) -> MarketplaceColors(CaixaDSColorTokens.LegacyLotomaniaLight, CaixaDSColorTokens.LegacyLotomaniaDark)
    modality.contains("TIMEMANIA", true) -> MarketplaceColors(CaixaDSColorTokens.LegacyTimemaniaLight, CaixaDSColorTokens.LegacyTimemaniaDark, CaixaDSColorTokens.LegacyTimemaniaContent)
    modality.contains("DUPLA", true) -> MarketplaceColors(CaixaDSColorTokens.LegacyDuplaSenaLight, CaixaDSColorTokens.LegacyDuplaSenaDark)
    modality.contains("DIA", true) -> MarketplaceColors(CaixaDSColorTokens.LegacyDiaDeSorteLight, CaixaDSColorTokens.LegacyDiaDeSorteDark, CaixaDSColorTokens.LegacyDiaDeSorteContent)
    modality.contains("SUPER", true) -> MarketplaceColors(CaixaDSColorTokens.LegacySuperSeteLight, CaixaDSColorTokens.LegacySuperSeteDark, CaixaDSColorTokens.LegacySuperSeteContent)
    modality.contains("MILION", true) -> MarketplaceColors(CaixaDSColorTokens.LegacyMaisMilionariaLight, CaixaDSColorTokens.LegacyMaisMilionariaDark)
    modality.contains("LOTECA", true) -> MarketplaceColors(CaixaDSColorTokens.Loteca, CaixaDSColorTokens.Loteca)
    else -> MarketplaceColors(CaixaDSColorTokens.LegacyMegaSenaLight, CaixaDSColorTokens.LegacyMegaSenaDark)
}

@Composable
internal fun MarketplaceContestHeader(game: LotteryGameParameters) {
    val colors = marketplaceColors(game.modality.code)
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(55.dp).background(colors.light)) {
            Canvas(Modifier.width(85.dp).fillMaxHeight()) {
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width * .698f, size.height); lineTo(0f, size.height); close() }, colors.dark)
            }
            CaixaDSLotteryMark(Modifier.align(Alignment.CenterStart).padding(start = 20.dp).size(27.dp, 28.dp),
                lightColor = colors.content, accentColor = colors.content.copy(alpha = .5f))
            Row(Modifier.fillMaxSize().padding(start = 93.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                val special = game.contestType.equals("ESPECIAL", true) || game.contestType.equals("E", true)
                Text((if (special) game.modality.specialName.ifBlank { game.modality.name } else game.modality.name).lowercase(Locale.forLanguageTag("pt-BR")),
                    Modifier.weight(1f).padding(end = 24.dp), color = colors.content,
                    style = marketplaceText(16, FontWeight.Bold).copy(fontFamily = CaixaDSLegacyFuturaFontFamily), maxLines = 2)
                Column(Modifier.padding(end = 8.dp)) {
                    Text("Concurso", style = marketplaceText(14), color = colors.content)
                    Text(game.contestNumber.toString(), Modifier.padding(top = 2.dp), style = marketplaceText(14, FontWeight.SemiBold), color = colors.content)
                }
                Column {
                    Text("Sorteio", style = marketplaceText(14), color = colors.content)
                    Text(marketplaceDrawDate(game.drawDate), Modifier.padding(top = 2.dp), style = marketplaceText(14, FontWeight.SemiBold), color = colors.content)
                }
            }
        }
        Column(Modifier.fillMaxWidth().height(92.dp).background(colors.dark), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(marketplaceMoney(game.estimatedPrize), style = marketplaceText(28, FontWeight.Bold), color = colors.content)
            marketplacePrizeWords(game.estimatedPrize).takeIf(String::isNotBlank)?.let {
                Text(it, style = marketplaceText(22, FontWeight.Bold), color = colors.content)
            }
            Text("Prêmio Estimado", style = marketplaceText(16), color = colors.content)
        }
    }
}

@Composable
internal fun MarketplaceCarousel(
    state: MarketplaceUiState,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onDetail: (String) -> Unit,
    onAdd: (String) -> Unit,
) {
    val pools = state.carouselPools
    val pager = rememberPagerState { pools.size }
    val coroutine = rememberCoroutineScope()
    Column(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 10.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            HorizontalPager(pager, modifier = Modifier.fillMaxWidth().height(461.dp).testTag("marketplace-carousel"),
                pageSize = PageSize.Fixed(280.dp), contentPadding = PaddingValues(horizontal = ((maxWidth - 280.dp) / 2).coerceAtLeast(0.dp))) { page ->
                val pool = pools[page]
                val offset = abs((pager.currentPage - page) + pager.currentPageOffsetFraction).coerceIn(0f, 1f)
                Box(Modifier.graphicsLayer { scaleX = 1f - .2f * offset; scaleY = scaleX }) {
                    MarketplaceCarouselCard(pool, state.selectedShares[pool.code] ?: 1,
                        state.lotteryFiltered, pool.lotteryId in state.updatingFavoriteLotteryIds,
                        pool.code in state.addingPoolCodes,
                        { onDecrease(pool.code) }, { onIncrease(pool.code) }, { onFavorite(pool.code) },
                        { onDetail(pool.code) }, { onAdd(pool.code) })
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.Center) {
            pools.forEachIndexed { index, _ ->
                Box(Modifier.padding(horizontal = 3.dp).size(12.dp).clip(CircleShape)
                    .background(if (pager.currentPage == index) CaixaDSColorTokens.LegacyCaixaBlue else CaixaDSColorTokens.White.copy(alpha = 0f))
                    .border(1.dp, CaixaDSColorTokens.LegacyCaixaBlue, CircleShape)
                    .clickable(role = Role.Button) { coroutine.launch { pager.animateScrollToPage(index) } }
                    .semantics { contentDescription = "Bolão ${index + 1} de ${pools.size}" })
            }
        }
    }
}

@Composable
private fun MarketplaceCarouselCard(pool: MarketplacePool, shares: Int, lotteryFiltered: Boolean,
    updatingFavorite: Boolean, adding: Boolean, decrease: () -> Unit, increase: () -> Unit,
    favorite: () -> Unit, detail: () -> Unit, add: () -> Unit) {
    val colors = marketplaceColors(pool.modality)
    Box(Modifier.width(280.dp).height(461.dp)) {
        Box(Modifier.fillMaxWidth().height(18.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(colors.light))
        Box(Modifier.fillMaxWidth().offset(y = 18.dp).height(393.dp).background(CaixaDSColorTokens.White))
        if (lotteryFiltered) {
            Column(Modifier.fillMaxWidth().offset(y = 24.dp).height(114.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(pool.modality.lowercase(Locale.forLanguageTag("pt-BR")).replace('_', ' '), style = marketplaceText(18, FontWeight.Bold), color = colors.dark)
                val prize = pool.estimatedPrize.toBigDecimalOrNull()
                    ?: pool.estimatedPrize.replace(".", "").replace(',', '.').toBigDecimalOrNull()
                prize?.let { Text(marketplaceMoney(it), style = marketplaceText(22, FontWeight.Bold), color = colors.dark) }
                Text("Prêmio estimado do concurso ${pool.contest}", style = marketplaceText(14), color = CaixaDSColorTokens.LegacySocialGraphite)
            }
        } else {
            Box(Modifier.fillMaxWidth().padding(start = 37.dp, end = 38.dp).offset(y = 24.dp).height(57.dp), contentAlignment = Alignment.BottomCenter) {
                Text(marketplaceRetailerName(pool.lotteryName), style = marketplaceText(18, FontWeight.SemiBold), color = CaixaDSColorTokens.LegacyRetailSearchText,
                    textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Box(Modifier.fillMaxWidth().padding(start = 37.dp, end = 38.dp).offset(y = 81.dp).height(57.dp).padding(top = 8.dp), contentAlignment = Alignment.TopCenter) {
                Text(marketplaceCity(pool.cityAndState), style = marketplaceText(18), color = CaixaDSColorTokens.LegacySocialGraphite, textAlign = TextAlign.Center, maxLines = 2)
            }
            Box(Modifier.align(Alignment.TopEnd).padding(end = 4.dp).size(32.dp).clip(CircleShape)
                .background(CaixaDSColorTokens.White).border(3.dp, colors.light, CircleShape)
                .testTag("carousel-favorite-${pool.code}")
                .clickable(enabled = pool.lotteryId > 0 && !updatingFavorite, onClick = favorite), contentAlignment = Alignment.Center) {
                Icon(if (pool.favoriteLottery) CaixaDSIcons.Favorite else CaixaDSIcons.FavoriteBorder,
                    if (pool.favoriteLottery) "Excluir lotérica favorita" else "Favoritar lotérica", Modifier.size(24.dp), tint = CaixaDS.colors.accent)
            }
        }
        Box(Modifier.fillMaxWidth().offset(y = 146.dp).height(25.dp), contentAlignment = Alignment.Center) {
            Text(pool.prognosticLabel(), style = marketplaceText(16), color = CaixaDSColorTokens.LegacyTicketChoiceMuted, textAlign = TextAlign.Center)
        }
        Box(Modifier.align(Alignment.TopCenter).offset(y = 183.dp).size(88.dp, 28.dp).border(1.dp, colors.light, RoundedCornerShape(4.dp))
            .testTag("carousel-detail-${pool.code}").clickable(onClick = detail), contentAlignment = Alignment.Center) {
            Text("Detalhes", style = marketplaceText(14), color = colors.light)
        }
        Box(Modifier.fillMaxWidth().offset(y = 223.dp).height(25.dp), contentAlignment = Alignment.Center) {
            Text(pool.shareAvailability(), style = marketplaceText(18), color = CaixaDSColorTokens.LegacyTicketChoiceMuted, textAlign = TextAlign.Center)
        }
        Box(Modifier.fillMaxWidth().offset(y = 262.dp).height(26.dp), contentAlignment = Alignment.Center) {
            Text(marketplaceMoney(pool.totalFor(shares)), style = marketplaceText(18, FontWeight.Bold), color = CaixaDSColorTokens.LegacyRetailSearchText)
        }
        Box(Modifier.fillMaxWidth().offset(y = 295.dp).height(22.dp), contentAlignment = Alignment.Center) {
            Text("Quantidade de Cotas", style = marketplaceText(16), color = CaixaDSColorTokens.LegacyTicketChoiceMuted)
        }
        Row(Modifier.align(Alignment.TopCenter).offset(y = 325.dp).height(32.dp), verticalAlignment = Alignment.CenterVertically) {
            PoolQuantityButton("−", shares > 1 && !adding, colors, "carousel-decrease-${pool.code}", decrease)
            Box(Modifier.width(50.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(shares.toString(), style = marketplaceText(18), color = CaixaDSColorTokens.LegacySocialGraphite)
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp).width(34.dp).height(1.dp).background(colors.dark))
            }
            PoolQuantityButton("+", shares < pool.availableShares && !adding, colors, "carousel-increase-${pool.code}", increase)
        }
        Box(Modifier.align(Alignment.TopCenter).offset(y = 376.dp).size(208.dp, 70.dp).clip(RoundedCornerShape(10.dp))
            .background(colors.dark).testTag("carousel-add-${pool.code}")
            .clickable(enabled = pool.availableShares > 0 && !adding, onClick = add), contentAlignment = Alignment.Center) {
            Text(buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold)); append(if (adding) "Adicionando" else "Adicionar"); pop()
                append("\nao carrinho de apostas")
            }, style = marketplaceText(16), color = colors.content, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun PoolQuantityButton(label: String, enabled: Boolean, colors: MarketplaceColors, tag: String, action: () -> Unit) {
    Box(Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)).background(if (enabled) colors.dark else CaixaDSColorTokens.LegacyNotificationSearch)
        .testTag(tag).clickable(enabled = enabled, onClick = action), contentAlignment = Alignment.Center) {
        Text(label, color = CaixaDSColorTokens.White, style = marketplaceText(if (label == "−") 24 else 16))
    }
}

private fun MarketplacePool.prognosticLabel(): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp)); append(betQuantity.toString()); pop()
    append(if (betQuantity == 1) " aposta de " else " apostas de ")
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp)); append(numberQuantity.toString()); pop()
    append(if (modality.contains("LOTECA", true)) " palpites" else " dezenas")
}
private fun MarketplacePool.shareAvailability(): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold)); append(availableShares.toString()); pop()
    append(if (availableShares == 1) " cota disponível de " else " cotas disponíveis de ")
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold)); append(totalShares.toString()); pop()
}
private fun marketplaceMoney(value: java.math.BigDecimal): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)
