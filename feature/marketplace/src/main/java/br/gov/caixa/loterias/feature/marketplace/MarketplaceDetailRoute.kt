package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceBet
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import java.math.BigDecimal

private fun detailText(size: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily, fontSize = size.sp, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

@Composable
fun MarketplaceDetailRoute(viewModel: MarketplaceDetailViewModel, onBack: () -> Unit,
    onPoolReserved: (MarketplacePoolConfirmation) -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO, cartBetCount: Int = 0, onCart: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { }
    }
    LaunchedEffect(state.confirmation) {
        state.confirmation?.let { confirmation -> viewModel.consumeConfirmation(); onPoolReserved(confirmation) }
    }
    MarketplaceDetailScreen(state, onBack, { viewModel.onAction(MarketplaceDetailAction.Retry) },
        { viewModel.onAction(MarketplaceDetailAction.DecreaseShares) },
        { viewModel.onAction(MarketplaceDetailAction.IncreaseShares) },
        { viewModel.onAction(MarketplaceDetailAction.AddToCart) }, cartTotal, cartBetCount, onCart, viewModel::requestFavorite)
    state.pendingFavorite?.let { target ->
        CaixaDSLegacyDialog("Atenção", if (target)
            "Deseja incluir ${state.detail?.lotteryName.orEmpty()} na sua lista de favoritas?"
        else "Deseja excluir ${state.detail?.lotteryName.orEmpty()} da sua lista de favoritas?",
            "Confirmar", viewModel::confirmFavorite, "Cancelar", viewModel::cancelFavorite)
    }
    state.favoriteError?.let { CaixaDSLegacyDialog("Atenção", it, "Entendi", viewModel::dismissFavoriteError) }
    if (state.isUpdatingFavorite || state.isAddingToCart || state.isLoading) CaixaDSAuthenticationLoadingDialog()
}

@Composable
fun MarketplaceDetailScreen(state: MarketplaceDetailUiState, onBack: () -> Unit, onRetry: () -> Unit,
    onDecrease: () -> Unit, onIncrease: () -> Unit, onAddToCart: () -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO, cartBetCount: Int = 0, onCart: (() -> Unit)? = null,
    onFavorite: () -> Unit = {},
) {
    val detail = state.detail
    val colors = marketplaceColors(detail?.modality.orEmpty())
    Column(Modifier.fillMaxSize().background(CaixaDSColorTokens.White).windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(Modifier.fillMaxWidth().height(52.dp).background(colors.light), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(44.dp).height(48.dp).clickable(role = Role.Button, onClick = onBack), Alignment.CenterEnd) {
                Icon(CaixaDSIcons.Back, "Voltar", Modifier.padding(end = 8.dp).size(12.dp, 18.dp), tint = colors.content)
            }
            Text(detail?.modality?.let(::marketplaceDetailModalityLabel) ?: "Bolão",
                Modifier.weight(1f).padding(start = 3.dp), style = detailText(18, FontWeight.Bold).copy(fontFamily = CaixaDSLegacyFuturaFontFamily), color = colors.content)
            if (detail != null) {
                HeaderDatum("Concurso", detail.contest.toString(), colors.content)
                HeaderDatum(if (detail.modality.contains("LOTECA", true)) "Resultado" else "Sorteio", detail.drawDate.ifBlank { detail.drawDateAndTime }.take(5), colors.content)
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = colors.dark)
                state.errorMessage != null -> CaixaDSLegacyEmptyState(title = "Não foi possível carregar o bolão", message = state.errorMessage,
                    actionLabel = "Tentar novamente", onAction = onRetry)
                detail != null -> LazyColumn(Modifier.fillMaxSize()) {
                    item {
                        Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(marketplaceDetailMoney(detail.estimatedPrize), style = detailText(24, FontWeight.Bold), color = colors.light)
                            marketplaceDetailPrizeInWords(detail.estimatedPrize)?.let {
                                Text("($it)", style = detailText(19, FontWeight.Bold), color = colors.light)
                            }
                            Text("Prêmio estimado", style = detailText(18), color = CaixaDSColorTokens.LegacyTicketChoiceMuted)
                        }
                        DetailDivider(Modifier.padding(top = 16.dp))
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(Modifier.fillMaxWidth()) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(marketplaceDetailRetailerName(detail.lotteryName), style = detailText(18, FontWeight.SemiBold), color = CaixaDSColorTokens.LegacyRetailSearchText)
                                    if (detail.lotteryNumber.isNotBlank()) Text("Código: ${detail.lotteryNumber}", style = detailText(18, FontWeight.SemiBold), color = CaixaDSColorTokens.LegacyRetailSearchText)
                                    if (detail.cityAndState.isNotBlank()) Text(detail.cityAndState, style = detailText(18, FontWeight.SemiBold), color = CaixaDSColorTokens.LegacyRetailSearchText)
                                }
                                Column(Modifier.width(80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    state.favoriteLottery?.let { favorite ->
                                        Box(Modifier.size(48.dp).clickable(enabled = !state.isUpdatingFavorite, role = Role.Button, onClick = onFavorite), Alignment.Center) {
                                            Icon(if (favorite) CaixaDSIcons.Favorite else CaixaDSIcons.FavoriteBorder,
                                                if (favorite) "Excluir lotérica favorita" else "Favoritar lotérica", Modifier.size(28.dp), tint = CaixaDSColorTokens.LegacyMarketplaceFavorite)
                                        }
                                        Text(if (favorite) "Lotérica\nFavorita" else "Favoritar\nLotérica", style = detailText(16, FontWeight.SemiBold),
                                            color = CaixaDSColorTokens.LegacyMarketplaceFavorite, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                            Text(buildAnnotatedString {
                                withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) { append(detail.betQuantity.toString()) }
                                append(if (detail.betQuantity == 1) " aposta de " else " apostas de ")
                                withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) { append(if (detail.modality.contains("MILION", true) && detail.bets.firstOrNull()?.numbers?.isNotEmpty() == true) detail.bets.first().numbers.size.toString() else detail.numberQuantity.toString()) }
                                append(if (detail.modality.contains("LOTECA", true)) " palpites" else " dezenas")
                            }, style = detailText(18), color = CaixaDSColorTokens.LegacyTicketChoiceMuted)
                            Text(buildAnnotatedString {
                                withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) { append(detail.availableShares.toString()) }
                                append(if (detail.availableShares == 1) " cota disponível de " else " cotas disponíveis de ")
                                withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) { append(detail.totalShares.toString()) }
                            }, style = detailText(18), color = CaixaDSColorTokens.LegacyTicketChoiceMuted)
                            Text("Valor da Cota: ${marketplaceDetailMoney(detail.sharePriceWithoutFee)}", style = detailText(18), color = CaixaDSColorTokens.LegacyTicketChoiceMuted)
                            Text("Tarifa de Serviço: ${marketplaceDetailMoney(detail.lastShareFee)}", style = detailText(18), color = CaixaDSColorTokens.LegacyTicketChoiceMuted)
                            Text("Total: ${marketplaceDetailMoney(detail.lastSharePriceWithFee)}", style = detailText(16, FontWeight.Bold), color = CaixaDSColorTokens.LegacyRetailSearchText)
                            Text("*Devido a regras de arredondamento, o valor cobrado pela cota deste bolão poderá ser menor que o exibido.",
                                Modifier.padding(end = 80.dp), style = detailText(12), color = CaixaDSColorTokens.LegacyTicketChoiceMuted)
                        }
                        DetailDivider()
                    }
                    if (detail.modality.contains("LOTECA", true)) {
                        itemsIndexed(detail.bets.firstOrNull()?.lotecaSelections.orEmpty()) { index, selection -> DetailLotecaMatch(index, selection) }
                    } else {
                        itemsIndexed(detail.bets) { index, bet ->
                            val titleColor = if (detail.modality.contains("TIMEMANIA", true) || detail.modality.contains("DIA", true) || detail.modality.contains("SUPER", true)) colors.content else colors.light
                            DetailBetGrid(index, bet, titleColor, detail.modality)
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
        if (detail != null && !state.isLoading && state.errorMessage == null) {
            DetailPurchasePanel(state, onDecrease, onIncrease, onAddToCart, cartTotal, cartBetCount, onCart)
        }
    }
}

@Composable
private fun HeaderDatum(label: String, value: String, color: Color) {
    Column(Modifier.padding(end = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = detailText(14), color = color)
        Text(value, style = detailText(12, FontWeight.Bold), color = color)
    }
}

@Composable
private fun DetailDivider(modifier: Modifier = Modifier) = Box(modifier.fillMaxWidth().height(.8.dp).background(CaixaDSColorTokens.LegacyPrizeDivider))

@Composable
private fun DetailBetGrid(position: Int, bet: MarketplaceBet, color: Color, modality: String) {
    if (modality.contains("SUPER", true) && bet.numberColumns.isNotEmpty()) {
        DetailSuperSevenGrid(position, bet, color, marketplaceColors(modality).dark)
        return
    }
    Column(Modifier.fillMaxWidth()) {
        Text("Jogo ${position + 1}", Modifier.padding(16.dp), style = detailText(14, FontWeight.Bold), color = color)
        if (bet.surprise) Text("Surpresinha", Modifier.padding(horizontal = 16.dp), style = detailText(14), color = color)
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            bet.numbers.chunked(6).forEachIndexed { rowIndex, numbers ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(6) { column ->
                        Box(Modifier.weight(1f).padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            numbers.getOrNull(column)?.let { number ->
                                Box(Modifier.size(35.dp).border(1.dp, CaixaDSColorTokens.LegacyMarketplaceNumber, CircleShape).testTag("pool-detail-ball-$position-${rowIndex * 6 + column}"), Alignment.Center) {
                                    Text(if (number == 0 || number == 100) "00" else number.toString(), style = detailText(14, FontWeight.Bold), color = CaixaDSColorTokens.LegacyMarketplaceNumber)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (modality.contains("TIMEMANIA", true)) Text("Time do coração: ${bet.favoriteTeam?.let(::detailFavoriteTeamName) ?: "-"}", Modifier.padding(16.dp), style = detailText(14), color = color)
        if (modality.contains("DIA", true)) Text("Mês de sorte: ${bet.luckyMonth?.name ?: "-"}", Modifier.padding(16.dp), style = detailText(14), color = color)
        if (bet.clovers.isNotEmpty()) {
            Text("Trevos", Modifier.padding(16.dp), style = detailText(14), color = color)
            bet.clovers.chunked(6).forEachIndexed { rowIndex, clovers ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    repeat(6) { column ->
                        Box(Modifier.weight(1f).padding(vertical = 8.dp), Alignment.Center) {
                            clovers.getOrNull(column)?.let { number ->
                                Box(Modifier.size(35.dp).testTag("pool-detail-clover-$position-${rowIndex * 6 + column}"), Alignment.Center) {
                                    Icon(CaixaDSLegacyDetailClover, null, Modifier.fillMaxSize(), tint = CaixaDSColorTokens.LegacyMarketplaceNumber)
                                    Text(number.toString(), style = detailText(14, FontWeight.Bold), color = CaixaDSColorTokens.LegacyMarketplaceNumber)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailPurchasePanel(state: MarketplaceDetailUiState, decrease: () -> Unit, increase: () -> Unit, add: () -> Unit,
    cartTotal: BigDecimal, cartBetCount: Int, cart: (() -> Unit)?) {
    val ink = CaixaDSColorTokens.LegacyMarketplaceFooterText
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).testTag("pool-detail-purchase-panel")) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Quantidade de Cotas", Modifier.weight(1f), style = detailText(14), color = ink)
            DetailQuantityButton("−", "Diminuir cotas", state.canDecrease, decrease)
            Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                Text(state.selectedShares.toString(), style = detailText(18), color = ink)
                Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(1.dp).background(ink))
            }
            DetailQuantityButton("+", "Aumentar cotas", state.canIncrease, increase)
        }
        state.addToCartError?.let { Text(it, Modifier.padding(horizontal = 24.dp), style = detailText(14), color = CaixaDS.colors.error) }
        Box(Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 8.dp).fillMaxWidth().height(48.dp)
            .background(CaixaDSColorTokens.LegacyMegaSenaLight, RoundedCornerShape(4.dp))
            .clickable(enabled = state.selectedShares > 0 && !state.isAddingToCart, role = Role.Button, onClick = add), Alignment.Center) {
            Text(if (state.isAddingToCart) "Adicionando..." else "Adicionar ao carrinho", style = detailText(16, FontWeight.Bold), color = CaixaDSColorTokens.White)
        }
        Row(Modifier.padding(horizontal = 24.dp).padding(bottom = 8.dp).fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            DetailFooterValue("Valor dessa aposta", marketplaceDetailMoney(state.purchaseAmount.toPlainString()), Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(44.dp).background(CaixaDSColorTokens.LegacyMarketplaceFooterDivider))
            DetailFooterValue("Total do carrinho", marketplaceDetailMoney(cartTotal.toPlainString()), Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(44.dp).background(CaixaDSColorTokens.LegacyMarketplaceFooterDivider))
            Box(Modifier.width(60.dp).fillMaxHeight().clickable(enabled = cart != null, role = Role.Button) { cart?.invoke() }
                .semantics { contentDescription = "Carrinho de apostas" }, Alignment.Center) {
                Icon(CaixaDSIcons.Cart, null, Modifier.size(34.dp), tint = ink)
                Box(Modifier.align(Alignment.TopEnd).padding(top = 11.dp, end = 10.dp).size(18.dp).background(CaixaDS.colors.accent, CircleShape), Alignment.Center) {
                    Text(if (cartBetCount > 999) "+999" else cartBetCount.toString(), style = detailText(9, FontWeight.Bold), color = ink)
                }
            }
        }
    }
}

@Composable
private fun DetailQuantityButton(label: String, description: String, enabled: Boolean, action: () -> Unit) {
    Box(Modifier.size(48.dp).clickable(enabled = enabled, role = Role.Button, onClick = action)
        .semantics { contentDescription = description }.padding(8.dp)
        .background(if (enabled) CaixaDSColorTokens.LegacyMarketplaceFooterText else CaixaDSColorTokens.LegacyMarketplaceFooterDisabled, RoundedCornerShape(3.dp)), Alignment.Center) {
        Text(label, style = detailText(16), color = CaixaDSColorTokens.White)
    }
}

@Composable
private fun DetailFooterValue(label: String, value: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = detailText(12), color = CaixaDSColorTokens.LegacyMarketplaceFooterText, textAlign = TextAlign.Center)
        Text(value, style = detailText(13, FontWeight.Bold), color = CaixaDSColorTokens.LegacyMarketplaceFooterText, textAlign = TextAlign.Center)
    }
}

@Composable
private fun DetailSuperSevenGrid(position: Int, bet: MarketplaceBet, textColor: Color, columnColor: Color) {
    Column(Modifier.fillMaxWidth()) {
        Text("Jogo ${position + 1}", Modifier.padding(16.dp), style = detailText(14, FontWeight.Bold), color = textColor)
        Text("Colunas", Modifier.padding(start = 16.dp, top = 8.dp), style = detailText(14), color = textColor)
        Row(Modifier.padding(start = 12.dp, top = 8.dp)) {
            repeat(7) { index ->
                Box(Modifier.width(48.dp), Alignment.Center) {
                    Text((index + 1).toString(), Modifier.background(columnColor).padding(8.dp), style = detailText(14), color = CaixaDSColorTokens.White)
                }
            }
        }
        Text("Números", Modifier.padding(start = 16.dp, top = 16.dp), style = detailText(14), color = textColor)
        Row(Modifier.padding(start = 12.dp, top = 8.dp)) {
            repeat(7) { column ->
                Column(Modifier.width(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    bet.numberColumns.getOrNull(column).orEmpty().forEachIndexed { row, number ->
                        Box(Modifier.padding(vertical = 8.dp).size(35.dp).border(1.dp, CaixaDSColorTokens.LegacyMarketplaceNumber, CircleShape)
                            .testTag("pool-detail-super-$position-$column-$row")
                            .semantics { contentDescription = "Coluna ${column + 1}, número $number" }, Alignment.Center) {
                            Text(number.toString(), style = detailText(14, FontWeight.Bold), color = CaixaDSColorTokens.LegacyMarketplaceNumber)
                        }
                    }
                }
            }
        }
    }
}

private fun detailFavoriteTeamName(team: SportsTeam): String =
    if (team.name.isBlank()) "-" else team.name + team.state.takeIf { it.isNotBlank() }?.let { "-$it" }.orEmpty()

private fun detailTeamName(team: SportsTeam): String {
    if (team.selectionIndicator) return team.name
    val suffix = if (team.countryCode.isNotBlank() && (!team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true))) team.countryCode else team.state
    return team.name + suffix.takeIf { it.isNotBlank() }?.let { "/$it" }.orEmpty()
}

@Composable
private fun DetailLotecaMatch(index: Int, selection: LotecaMatchSelection) {
    val legend = selection.match.legendDescription.takeIf { it.length > 4 }?.drop(4)?.let { " ($it)" }.orEmpty()
    Column(Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailLotecaChoice("${detailTeamName(selection.match.home)}$legend", selection.homeWin, "pool-detail-loteca-$index-home")
        DetailLotecaChoice("X", selection.draw, "pool-detail-loteca-$index-draw")
        DetailLotecaChoice("${detailTeamName(selection.match.away)}$legend", selection.awayWin, "pool-detail-loteca-$index-away")
        DetailDivider(Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun DetailLotecaChoice(label: String, checked: Boolean, tag: String) {
    Row(Modifier.fillMaxWidth().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(28.dp).testTag(tag).semantics { selected = checked; role = Role.RadioButton; contentDescription = label }) {
            drawCircle(CaixaDSColorTokens.Black, radius = 10.dp.toPx(), style = Stroke(2.dp.toPx()))
            if (checked) drawCircle(CaixaDSColorTokens.Black, radius = 5.dp.toPx())
        }
        Text(label, Modifier.padding(start = 8.dp), style = detailText(14, FontWeight.Bold), color = CaixaDSColorTokens.Black)
    }
}
