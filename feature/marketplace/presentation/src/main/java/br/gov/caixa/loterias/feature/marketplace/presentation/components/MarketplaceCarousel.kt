package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSMarketplaceTrapeze
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplacePoolModalityLabel
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.totalFor
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceCity
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceDrawDate
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceListRetailerName
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplacePrizeWords
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceRetailerName
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

internal fun marketplaceText(size: Int, weight: FontWeight = FontWeight.Normal) = CaixaDSMarketplaceStyle.text(size, weight)

internal data class MarketplaceColors(
    val light: Color,
    val dark: Color,
    val content: Color = CaixaDSColorTokens.White,
    val darkContent: Color = CaixaDSColorTokens.White,
    val listText: Color = light,
    val textOnSurface: Color = listText,
    val carouselHeader: Color = light,
)

@Composable
internal fun marketplaceColors(modality: String): MarketplaceColors {
    val style = caixaDSLotteryStyle(0, modality)
    val campaignStyle = style != resolveCaixaDSLotteryStyle(0, modality, CaixaDSCampaign.DEFAULT)
    val dark = if (modality.contains("LOTECA", true)) style.light else style.dark
    return MarketplaceColors(
        style.light,
        dark,
        style.content,
        style.darkContent,
        style.listText,
        if (campaignStyle) style.textOnSurface else style.listText,
        if (campaignStyle) style.dark else style.light
    )
}

@Composable
internal fun MarketplaceContestHeader(game: LotteryGameParameters, specialName: String? = null) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val colors = marketplaceColors(game.modality.code)
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(55.dp).background(colors.light)) {
            MarketplaceContestArtwork(game, colors)
            Row(Modifier.fillMaxSize().padding(start = 93.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                val special = game.contestType.equals("ESPECIAL", true) || game.contestType.equals("E", true)
                Text(
                    (
                        specialName ?: if (special) {
                            game.modality.specialName.ifBlank {
                                game.modality.name
                            }
                        } else {
                            game.modality.name
                        }
                        ).lowercase(Locale.forLanguageTag("pt-BR")),
                    (Modifier.weight(1f).padding(end = 24.dp)).uiId("pool.0001"),
                    color = colors.content,
                    style = marketplaceText(16, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily),
                    maxLines = 2
                )
                Column(Modifier.padding(end = 8.dp).offset(y = CaixaDSMarketplaceStyle.contestMetadataOffset)) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_concurso_1997f5),
                        style = marketplaceText(14),
                        color = colors.content,
                        modifier = Modifier.uiId("pool.0002")
                    )
                    Text(
                        game.contestNumber.toString(),
                        (Modifier.padding(top = 2.dp)).uiId("pool.0003"),
                        style = marketplaceText(14, FontWeight.SemiBold),
                        color = colors.content
                    )
                }
                Column(Modifier.offset(y = CaixaDSMarketplaceStyle.contestMetadataOffset)) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_sorteio_a49bf4),
                        style = marketplaceText(14),
                        color = colors.content,
                        modifier = Modifier.uiId("pool.0004")
                    )
                    Text(
                        marketplaceDrawDate(game.drawDate),
                        (Modifier.padding(top = 2.dp)).uiId("pool.0005"),
                        style = marketplaceText(14, FontWeight.SemiBold),
                        color = colors.content
                    )
                }
            }
        }
        Column(
            Modifier.fillMaxWidth().height(92.dp).background(colors.dark),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                formatBrazilianCurrency(game.estimatedPrize),
                style = marketplaceText(28, FontWeight.Bold),
                color = colors.darkContent,
                modifier = Modifier.uiId("pool.0006")
            )
            marketplacePrizeWords(game.estimatedPrize).takeIf(String::isNotBlank)?.let {
                Text(it, style = marketplaceText(22, FontWeight.Bold), color = colors.darkContent, modifier = Modifier.uiId("pool.0007"))
            }
            Text(
                uiStrings.getString(LocalizedR.string.ui_premio_estimado_63dd56),
                style = marketplaceText(16),
                color = colors.darkContent,
                modifier = Modifier.uiId("pool.0008")
            )
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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val pools = state.carouselPools
    val pager = rememberPagerState { pools.size }
    val coroutine = rememberCoroutineScope()
    Column(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 10.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            HorizontalPager(
                pager,
                modifier = Modifier.fillMaxWidth().height(461.dp).legacyAutomationId("marketplace-carousel"),
                pageSize = PageSize.Fixed(280.dp),
                contentPadding = PaddingValues(horizontal = ((maxWidth - 280.dp) / 2).coerceAtLeast(0.dp))
            ) { page ->
                val pool = pools[page]
                val offset = abs((pager.currentPage - page) + pager.currentPageOffsetFraction).coerceIn(0f, 1f)
                Box(
                    Modifier.graphicsLayer {
                        scaleX = 1f - .2f * offset
                        scaleY = scaleX
                    }
                ) {
                    MarketplaceCarouselCard(
                        pool,
                        state.selectedShares[pool.code] ?: 1,
                        state.lotteryFiltered,
                        pool.lotteryId in state.updatingFavoriteLotteryIds,
                        pool.code in state.addingPoolCodes,
                        MarketplaceCardActions(
                            { onDecrease(pool.code) },
                            { onIncrease(pool.code) },
                            { onFavorite(pool.code) },
                            { onDetail(pool.code) },
                            { onAdd(pool.code) }
                        ),
                        state.modalityNames,
                        state.specialContests
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
            pools.forEachIndexed { index, _ ->
                Box(
                    Modifier.padding(horizontal = 3.dp).size(12.dp).clip(CircleShape)
                        .background(
                            if (pager.currentPage ==
                                index
                            ) {
                                CaixaDSColorTokens.CaixaBlue
                            } else {
                                CaixaDSColorTokens.White.copy(alpha = 0f)
                            }
                        )
                        .border(1.dp, CaixaDSColorTokens.CaixaBlue, CircleShape)
                        .uiId("pool.0009").clickable(role = Role.Button) { coroutine.launch { pager.animateScrollToPage(index) } }
                        .semantics {
                            selected = pager.currentPage == index
                            contentDescription =
                                uiStrings.getString(
                                    LocalizedR.string.ui_bolao_1_s_de_2_s_214183,
                                    (index + 1).toString(),
                                    pools.size.toString()
                                )
                        }
                )
            }
        }
    }
}

@Composable
private fun MarketplaceCarouselCard(
    pool: MarketplacePool,
    shares: Int,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    adding: Boolean,
    actions: MarketplaceCardActions,
    modalityNames: Map<Pair<String, Int>, String>,
    specialContests: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests,
) {
    if (LocalDensity.current.fontScale > 1f) {
        MarketplaceLargeFontCarouselCard(pool, shares, lotteryFiltered, updatingFavorite, adding, actions, modalityNames, specialContests)
        return
    }
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val colors = marketplaceColors(pool.modality)
    Box(Modifier.width(280.dp).height(461.dp)) {
        Box(
            Modifier.fillMaxWidth().height(
                18.dp
            ).clip(
                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
            ).background(if (lotteryFiltered) colors.light else colors.carouselHeader)
        )
        Box(Modifier.fillMaxWidth().offset(y = 18.dp).height(393.dp).background(CaixaDSColorTokens.White))
        MarketplacePoolIdentity(pool, lotteryFiltered, updatingFavorite, actions.favorite, colors, modalityNames, specialContests)
        Box(Modifier.fillMaxWidth().offset(y = 146.dp).height(25.dp), contentAlignment = Alignment.Center) {
            Text(
                pool.prognosticLabel(),
                style = marketplaceText(16),
                color = CaixaDSColorTokens.TicketChoiceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("pool.0010")
            )
        }
        Box(
            Modifier.align(
                Alignment.TopCenter
            ).offset(y = 183.dp).size(88.dp, 28.dp).border(1.dp, colors.listText, RoundedCornerShape(4.dp))
                .legacyAutomationId("carousel-detail-${pool.code}").uiId("pool.0011").clickable(onClick = actions.detail),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_detalhes_62c950),
                style = marketplaceText(14),
                color = colors.listText,
                modifier = Modifier.uiId("pool.0012")
            )
        }
        Box(Modifier.fillMaxWidth().offset(y = 223.dp).height(25.dp), contentAlignment = Alignment.Center) {
            Text(
                pool.shareAvailability(),
                style = marketplaceText(18),
                color = CaixaDSColorTokens.TicketChoiceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("pool.0013")
            )
        }
        Box(Modifier.fillMaxWidth().offset(y = 262.dp).height(26.dp), contentAlignment = Alignment.Center) {
            Text(
                formatBrazilianCurrency(pool.totalFor(shares)),
                style = marketplaceText(18, FontWeight.Bold),
                color = CaixaDSColorTokens.RetailSearchText,
                modifier = Modifier.uiId("pool.0014")
            )
        }
        MarketplacePoolQuantity(pool, shares, adding, colors, actions)
        Box(
            Modifier.align(Alignment.TopCenter).offset(y = 376.dp).size(208.dp, 70.dp).clip(RoundedCornerShape(10.dp))
                .background(colors.dark).legacyAutomationId("carousel-add-${pool.code}")
                .uiId("pool.0015").clickable(enabled = pool.availableShares > 0 && !adding, onClick = actions.add),
            contentAlignment = Alignment.Center
        ) {
            Text(
                buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(if (adding) "Adicionando" else "Adicionar")
                    pop()
                    append(uiStrings.getString(LocalizedR.string.ui_ao_carrinho_de_apostas_ef1eb2))
                },
                style = marketplaceText(16),
                color = colors.darkContent,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("pool.0016")
            )
        }
    }
}

@Composable
internal fun PoolQuantityButton(label: String, enabled: Boolean, colors: MarketplaceColors, tag: String, action: () -> Unit) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        Modifier.then(if (largeFont) Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp).padding(4.dp) else Modifier.size(32.dp))
            .clip(RoundedCornerShape(4.dp)).background(if (enabled) colors.dark else CaixaDSColorTokens.NotificationSearch)
            .legacyAutomationId(tag).uiId("pool.0017").clickable(enabled = enabled, role = Role.Button, onClick = action)
            .semantics {
                contentDescription = if (label ==
                    "−"
                ) {
                    "Diminuir quantidade de cotas"
                } else {
                    "Aumentar quantidade de cotas"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = CaixaDSColorTokens.White,
            style = marketplaceText(if (label == "−") 24 else 16),
            modifier = Modifier
                .uiId("pool.0018")
        )
    }
}

internal fun MarketplacePool.prognosticLabel(): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp))
    append(betQuantity.toString())
    pop()
    append(if (betQuantity == 1) messageText("aposta_de_9a14cf") else messageText("apostas_de_11b8ee"))
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp))
    append(numberQuantity.toString())
    pop()
    append(if (modality.contains("LOTECA", true)) " palpites" else " dezenas")
}
internal fun MarketplacePool.shareAvailability(): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
    append(availableShares.toString())
    pop()
    append(if (availableShares == 1) messageText("cota_disponivel_de_fae690") else messageText("cotas_disponiveis_de_010d84"))
    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
    append(totalShares.toString())
    pop()
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.MarketplaceContestArtwork(game: LotteryGameParameters, colors: MarketplaceColors) {
    val trapezoid =
        caixaDSCampaignDrawable(
            game.modality.id,
            game.modality.name,
            if (game.contestType.equals("ESPECIAL", true) ||
                game.contestType.equals("E", true)
            ) {
                CaixaDSLotteryArtwork.SPECIAL_TRAPEZE
            } else {
                CaixaDSLotteryArtwork.TRAPEZE
            }
        )
            ?: if (game.contestType.equals("ESPECIAL", true) || game.contestType.equals("E", true)) {
                null
            } else {
                caixaDSMarketplaceTrapeze(game.modality.id, game.modality.name)
            }
    if (trapezoid != null) {
        androidx.compose.foundation.Image(caixaDSDrawablePainter(trapezoid), null, Modifier.width(85.dp).fillMaxHeight())
    } else {
        Canvas(Modifier.width(85.dp).fillMaxHeight()) {
            drawPath(
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width * .698f, size.height)
                    lineTo(0f, size.height)
                    close()
                },
                colors.dark
            )
        }
    }
    val clover =
        caixaDSCampaignDrawable(game.modality.id, game.modality.name, CaixaDSLotteryArtwork.CLOVER_ON_DARK)
            ?: caixaDSMarketplaceFilterClover(game.modality.id, game.modality.name, selected = true)
    if (clover != null) {
        androidx.compose.foundation.Image(
            caixaDSDrawablePainter(clover),
            null,
            Modifier.align(Alignment.CenterStart).padding(start = 20.dp).size(27.dp, 28.dp)
        )
    } else {
        CaixaDSLotteryMark(
            (Modifier.align(Alignment.CenterStart).padding(start = 20.dp).size(27.dp, 28.dp)).uiId("pool.0019"),
            lightColor = colors.content,
            accentColor = colors.content.copy(alpha = .5f)
        )
    }
}

internal data class MarketplaceCardActions(
    val decrease: () -> Unit,
    val increase: () -> Unit,
    val favorite: () -> Unit,
    val detail: () -> Unit,
    val add: () -> Unit,
)

@Composable
private fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolIdentity(
    pool: MarketplacePool,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    favorite: () -> Unit,
    colors: MarketplaceColors,
    modalityNames: Map<Pair<String, Int>, String>,
    specialContests: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (lotteryFiltered) {
        Column(Modifier.fillMaxWidth().offset(y = 24.dp).height(114.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                marketplacePoolModalityLabel(pool, modalityNames, specialContests),
                style = marketplaceText(18, FontWeight.Bold),
                color = colors.dark,
                modifier = Modifier.uiId("pool.0020")
            )
            val prize = pool.estimatedPrizeValue()
            prize?.let {
                Text(
                    formatBrazilianCurrency(it),
                    style = marketplaceText(22, FontWeight.Bold),
                    color = colors.dark,
                    modifier = Modifier.uiId("pool.0021")
                )
            }
            Text(
                uiStrings.getString(LocalizedR.string.ui_premio_estimado_do_concurso_1_s_f15d0b, pool.contest.toString()),
                style = marketplaceText(14),
                color = CaixaDSColorTokens.SocialGraphite,
                modifier = Modifier.uiId("pool.0022")
            )
        }
    } else {
        Box(
            Modifier.fillMaxWidth().padding(start = 37.dp, end = 38.dp).offset(y = 24.dp).height(57.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                marketplaceListRetailerName(pool.lotteryName),
                style = marketplaceText(18, FontWeight.SemiBold),
                color = CaixaDSColorTokens.RetailSearchText,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.uiId("pool.0023")
            )
        }
        Box(
            Modifier.fillMaxWidth().padding(start = 37.dp, end = 38.dp).offset(y = 81.dp).height(57.dp).padding(top = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                marketplaceCity(pool.cityAndState),
                style = marketplaceText(18),
                color = CaixaDSColorTokens.SocialGraphite,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.uiId("pool.0024")
            )
        }
        MarketplacePoolFavorite(pool, updatingFavorite, favorite, colors)
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolQuantity(
    pool: MarketplacePool,
    shares: Int,
    adding: Boolean,
    colors: MarketplaceColors,
    actions: MarketplaceCardActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.align(Alignment.TopCenter).offset(y = 295.dp).width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.height(22.dp), contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_quantidade_de_cotas_ddb3fd),
                style = marketplaceText(16),
                color = CaixaDSColorTokens.TicketChoiceMuted,
                modifier = Modifier.uiId("pool.0025")
            )
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp).height(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PoolQuantityButton("−", shares > 1 && !adding, colors, "carousel-decrease-${pool.code}", actions.decrease)
            Box(Modifier.width(50.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    shares.toString(),
                    style = marketplaceText(18),
                    color = CaixaDSColorTokens.SocialGraphite,
                    modifier = Modifier
                        .uiId("pool.0026")
                )
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp).width(34.dp).height(1.dp).background(colors.dark))
            }
            PoolQuantityButton(
                "+",
                shares < pool.availableShares && !adding,
                colors,
                "carousel-increase-${pool.code}",
                actions.increase
            )
        }
    }
}

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolFavorite(
    pool: MarketplacePool,
    updatingFavorite: Boolean,
    favorite: () -> Unit,
    colors: MarketplaceColors,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.align(Alignment.TopEnd).padding(end = 4.dp).size(32.dp).clip(CircleShape)
            .background(CaixaDSColorTokens.White).border(3.dp, colors.light, CircleShape)
            .legacyAutomationId("carousel-favorite-${pool.code}")
            .uiId("pool.0027").clickable(enabled = pool.lotteryId > 0 && !updatingFavorite, onClick = favorite),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            androidx.compose.ui.res.painterResource(
                if (pool.favoriteLottery) {
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_marketplace_favorite
                } else {
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_marketplace_unfavorite
                }
            ),
            if (pool.favoriteLottery) {
                uiStrings.getString(
                    LocalizedR.string.ui_excluir_loterica_favorita_1ab99f
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_favoritar_loterica_9b9006)
            },
            Modifier.padding(top = 1.dp).size(20.dp)
        )
    }
}

internal fun MarketplacePool.estimatedPrizeValue() = estimatedPrize.toBigDecimalOrNull()
    ?: estimatedPrize.replace(".", "").replace(',', '.').toBigDecimalOrNull()
