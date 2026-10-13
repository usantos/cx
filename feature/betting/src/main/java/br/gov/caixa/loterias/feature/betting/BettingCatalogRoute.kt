package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.designsystem.CaixaDSCartSummary
import androidx.compose.ui.res.painterResource
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import androidx.compose.foundation.Image
import br.gov.caixa.loterias.core.designsystem.caixaDSLegacyLotteryClover
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyBottomSummary
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCheckbox
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDivider
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySectionHeader
import br.gov.caixa.loterias.core.domain.betting.BetCombo
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import java.math.BigDecimal

@Composable
fun LotteryModalitiesRoute(
    viewModel: BettingCatalogViewModel,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CaixaDSLegacyScaffold(title = "Modalidades", onBack = onBack) { padding ->
        BettingCatalogContentState(state.loading, state.errorMessage, padding, viewModel::load) {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                item {
                    CaixaDSLegacySectionHeader(
                        "Escolha uma modalidade",
                        "Consulte o próximo concurso e faça sua aposta.",
                        Modifier.padding(16.dp),
                    )
                }
                items(state.modalities, key = LotteryModality::id) { modality ->
                    val game = state.parameters?.games?.firstOrNull { it.modality.id == modality.id }
                    Row(
                        Modifier.fillMaxWidth().clickable(enabled = game?.open != false) { onSelect(modality.id) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (modality.id == 14) {
                            Image(painterResource(CaixaDSR.drawable.legacy_trevo_lotogol), null, Modifier.size(44.dp))
                        } else {
                            Image(caixaDSLegacyLotteryClover(modality.id), contentDescription = null, modifier = Modifier.size(44.dp))
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                            Text(modality.name, color = modalityColor(modality.id), style = CaixaDS.typography.titleMedium)
                            Text(
                                if (game == null) "Consulte a disponibilidade" else "Concurso ${game.contestNumber} • ${game.drawDate}",
                                color = CaixaDS.colors.legacyMutedText,
                                style = CaixaDS.typography.bodySmall,
                            )
                        }
                        Icon(CaixaDSIcons.Forward, contentDescription = null, modifier = Modifier.size(20.dp), tint = CaixaDS.colors.brand)
                    }
                    CaixaDSLegacyDivider()
                }
            }
        }
    }
}

@Composable
fun BetCombosRoute(
    viewModel: BettingCatalogViewModel,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CaixaDSLegacyScaffold(title = "Combos", onBack = onBack, bottomBar = { CaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart) }) { padding ->
        BettingCatalogContentState(state.loading, state.errorMessage, padding, viewModel::load) {
            LazyColumn(Modifier.fillMaxSize().padding(padding)
                .background(Brush.linearGradient(listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.LegacyComboGradientEnd))),
                contentPadding = PaddingValues(start = 25.dp, end = 25.dp, bottom = 10.dp)) {
                item {
                    Text("Confira nossos combos e escolha o seu favorito!\nAproveite para aumentar suas chances de ganhar.",
                        Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 17.dp), fontFamily = CaixaDSFontFamily,
                        fontSize = 13.sp, color = CaixaDSColorTokens.LegacyComboDescription, textAlign = TextAlign.Center)
                }
                items(state.combos.chunked(2)) { row ->
                    Row(Modifier.fillMaxWidth()) {
                        row.forEach { combo ->
                            Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                                LegacyComboCatalogCard(combo) { onSelect(combo.id) }
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                if (state.combos.isEmpty()) item { Text("Nenhum combo disponível no momento.", fontFamily = CaixaDSFontFamily) }
            }
        }
    }
}

@Composable
fun BetComboDetailRoute(
    viewModel: BettingCatalogViewModel,
    onBack: () -> Unit,
    onAdded: () -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.addedToCart) { if (state.addedToCart) onAdded() }
    val combo = state.selectedCombo
    CaixaDSLegacyScaffold(
        title = "Detalhes do combo",
        onBack = onBack,
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).padding(top = 16.dp, bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatBetAmount(combo?.amount?.multiply(BigDecimal(state.comboQuantity))), fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Black, fontSize = 18.sp, color = CaixaDSColorTokens.Black, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                    Text("Quantidade de apostas", Modifier.padding(top = 4.dp), fontFamily = CaixaDSFontFamily, fontSize = 16.sp, color = CaixaDSColorTokens.Black, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CatalogCounterButton(false, viewModel::decreaseQuantity)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.comboQuantity.toString(), fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CaixaDSColorTokens.LegacyComboDark, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                            Image(painterResource(R.drawable.linha_inferior_qtd_apostas_detalhe_combo), null, Modifier.width(31.dp).height(1.dp))
                        }
                        CatalogCounterButton(true, viewModel::increaseQuantity)
                    }
                    Box(Modifier.padding(top = 15.dp).width(201.dp).height(59.dp)
                        .background(CaixaDSColorTokens.LegacyComboDark, RoundedCornerShape(4.dp)).clickable(enabled = !state.loading && combo != null, onClick = viewModel::addSelectedCombo), contentAlignment = Alignment.Center) {
                        Text(buildAnnotatedString { withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Adicionar") }; append("\nao carrinho de apostas") }, fontFamily = CaixaDSFontFamily, fontSize = 14.sp, color = CaixaDSColorTokens.White, textAlign = TextAlign.Center, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                    }
                }
                Spacer(Modifier.height(8.dp))
                CaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart)
            }
        },
    ) { padding ->
        BettingCatalogContentState(state.loading, state.errorMessage, padding, viewModel::load) {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.LegacyComboDetailCanvas),
                contentPadding = PaddingValues(bottom = 36.dp),
            ) {
                combo?.let { selected ->
                    item {
                        Row(Modifier.padding(start = 20.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painterResource(R.drawable.trevo_fundo_claro_detalhe_combo), null, Modifier.size(24.dp))
                            Text(selected.name, Modifier.padding(start = 8.dp), fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = CaixaDSColorTokens.LegacyComboDetailHeading, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                        }
                        Text("Com esse pacote você garante apostas para todas as modalidades, incluindo teimosinhas para a semana inteira. Já pensou?", Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp), fontFamily = CaixaDSFontFamily, fontSize = 13.sp, color = CaixaDSColorTokens.LegacyComboDescription, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                    }
                    items(selected.entries) { entry ->
                        Box(Modifier.fillMaxWidth().padding(start = 19.dp, end = 17.dp, top = 3.dp).height(70.dp)
                            .background(CaixaDSColorTokens.White, RoundedCornerShape(3.5.dp)).border(1.dp, CaixaDSColorTokens.LegacyComboDark, RoundedCornerShape(3.5.dp))) {
                            Image(painterResource(R.drawable.trevo_background_lista_combo), null, Modifier.padding(start = 1.dp, top = 1.dp).width(40.dp).height(37.dp))
                            Column(Modifier.fillMaxSize().padding(top = 6.dp, bottom = 6.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                                Text("${entry.betQuantity} jogos", fontFamily = CaixaDSFontFamily, fontSize = 12.sp, color = CaixaDSColorTokens.Black, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                                Text(entry.modality.name.lowercase(), Modifier.padding(start = 30.dp, end = 15.dp), fontSize = 14.sp, color = modalityColor(entry.modality.id), textAlign = TextAlign.Center, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                                Text("concurso ${entry.contestNumber}", fontFamily = CaixaDSFontFamily, fontSize = 12.sp, color = CaixaDSColorTokens.Black, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                            }
                        }
                    }
                    item {
                        Column(Modifier.fillMaxWidth().padding(start = 17.dp, end = 17.dp, top = 3.dp, bottom = 36.dp)) {
                            Text("Os números dos jogos dos combos são escolhidos aleatoriamente pelo sistema. Mas antes de efetuar o pagamento é possível visualizar os números selecionados.", fontFamily = CaixaDSFontFamily, fontSize = 12.sp, color = CaixaDSColorTokens.Black, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
                            Row(
                                Modifier.fillMaxWidth().height(48.dp).clickable { viewModel.toggleSurprise() }
                                    .padding(start = 1.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CaixaDSLegacyCheckbox(
                                    checked = !state.surprise,
                                    onClick = viewModel::toggleSurprise,
                                    modifier = Modifier.size(32.dp),
                                    color = CaixaDSColorTokens.LegacySocialGraphite,
                                )
                                Text(
                                    "Quero ver os números escolhidos antes de pagar.",
                                    Modifier.padding(start = 4.dp),
                                    fontFamily = CaixaDSFontFamily,
                                    fontSize = 14.sp,
                                    color = CaixaDSColorTokens.Black,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                                )
                            }
                            Spacer(Modifier.height(360.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BettingCatalogContentState(
    loading: Boolean,
    error: String?,
    padding: PaddingValues,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    when {
        loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.legacyTeal)
        }
        error != null -> Column(
            Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(error, color = CaixaDS.colors.error)
            Text("Tentar novamente", Modifier.clickable(onClick = onRetry).padding(20.dp), color = CaixaDS.colors.brand)
        }
        else -> content()
    }
}

@Composable
private fun CatalogCounterButton(increase: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(48.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Image(painterResource(if (increase) R.drawable.botao_mais_detalhe_combo else R.drawable.botao_menos_detalhe_combo),
            if (increase) "Adicionar apostas" else "Remover apostas", Modifier.width(30.dp).height(29.dp))
    }
}

private fun modalityColor(id: Int) = caixaDSLotteryStyle(id).let { style ->
    if (style.content != CaixaDSColorTokens.White) style.content else style.dark
}

@Composable
private fun LegacyComboCatalogCard(combo: BetCombo, onClick: () -> Unit) {
    Box(Modifier.padding(top = 8.dp).width(147.dp).height(102.dp)) {
        Box(Modifier.align(Alignment.BottomCenter).width(147.dp).height(96.dp)
            .background(CaixaDSColorTokens.White, RoundedCornerShape(4.dp))
            .border(1.dp, CaixaDSColorTokens.LegacyComboCardBorder, RoundedCornerShape(4.dp)).clickable(onClick = onClick)) {
            Image(painterResource(R.drawable.trevo_background_lista_combo), null,
                Modifier.offset(x = 1.dp, y = (-2.5).dp).width(59.dp).height(61.dp))
        }
        Box(Modifier.align(Alignment.TopCenter).width(39.dp).height(33.dp)) {
            Image(painterResource(R.drawable.tarja_trevo_padrao_lista_combos), null, Modifier.fillMaxSize())
            Image(painterResource(R.drawable.trevo_lista_combo), null,
                Modifier.align(Alignment.CenterStart).padding(start = 6.dp).width(22.dp).height(21.dp))
        }
        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(combo.name, Modifier.padding(start = 5.dp, end = 5.dp, top = 13.dp), fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = CaixaDSColorTokens.LegacyComboCardName, textAlign = TextAlign.Center)
            Text(formatBetAmount(combo.amount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaixaDSColorTokens.Black, textAlign = TextAlign.Center, style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
        }
    }
}
